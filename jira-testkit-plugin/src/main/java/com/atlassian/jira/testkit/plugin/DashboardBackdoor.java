/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.gadgets.dashboard.Layout;
import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.favourites.FavouritesService;
import com.atlassian.jira.bc.portal.PortalPageService;
import com.atlassian.jira.portal.PortalPage;
import com.atlassian.jira.portal.PortletConfiguration;
import com.atlassian.jira.portal.PortletConfigurationManager;
import com.atlassian.jira.sharing.SharedEntity;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;

/**
 * @since v5.0.3
 */
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
@Path ("/dashboard")
public class DashboardBackdoor
{
    private final PortalPageService portalPageService;
    private final FavouritesService favouritesService;
	private final PortletConfigurationManager portletConfigurationManager;
	private final UserUtil userUtil;

    public DashboardBackdoor(PortalPageService portalPageService, UserUtil userUtil, FavouritesService favouritesService, PortletConfigurationManager portletConfigurationManager)
    {
        this.portalPageService = portalPageService;
        this.userUtil = userUtil;
        this.favouritesService = favouritesService;
		this.portletConfigurationManager = portletConfigurationManager;
	}

    @GET
    @Path("my")
    public Response getMyDasbhoards(@QueryParam("username") String username)
    {
        username = StringUtils.trimToNull(username);
        if (username == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).cacheControl(never())
                    .entity("No user passed.").build();
        }
        
        ApplicationUser user = userUtil.getUserByName(username);
        if (user == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).cacheControl(never())
                    .entity("User '" + username + "' does not exist.").build();
        }

        return Response.ok().cacheControl(never())
                .entity(asBeans(user, portalPageService.getOwnedPortalPages(user))).build();
    }

	@GET
	@Path("emptySystemDashboard")
	public Response emptySystemDashboard() {
		final PortalPage systemDashboard = portalPageService.getSystemDefaultPortalPage();
		final List<PortletConfiguration> portlets = portletConfigurationManager.getByPortalPage(systemDashboard.getId());
		for (PortletConfiguration pc : portlets) {
			portletConfigurationManager.delete(pc);
		}
		return Response.ok().cacheControl(never()).build();
	}
	
	/**
	 * Creates a dashboard for the given user.
	 * 
	 * @param username
	 *            user for which to create the dashboard.
	 * @param name
	 *            Name of the dashboard. This is shown in the drop down
	 *            containing all visible dashboards and in the dashboard
	 *            management view.
	 * @param description
	 *            Description of the dashboard.
	 * @param global
	 *            If <code>true</code> a public dashboard viewable by all is
	 *            created, otherwise the new dashboard will be private.
	 * @param layoutString
	 *            A layout to choose for the new dashboard. Valid values are the
	 *            names of the {@link Layout} enumerations.
	 * @param favorite
	 *            If <code>true</code> the new dashboard will be added to the
	 *            favorite list.
	 * @return The information about the new dashboard.
	 */
	@GET
	@Path("add")
	public Response createNewDashboard(@QueryParam("username") String username,
			@QueryParam("name") String name,
			@QueryParam("description") String description,
			@QueryParam("global") boolean global,
			@QueryParam("layout") String layoutString,
			@QueryParam("favorite") boolean favorite) {
		ApplicationUser user = userUtil.getUserByName(username);
		SimpleErrorCollection errorCollection = new SimpleErrorCollection();
		JiraServiceContext context = new JiraServiceContextImpl(user,
				errorCollection);

		PortalPage.Builder builder = PortalPage.name(name).owner(user);

		if (description != null) {
			builder.description(description);
		}

		if (layoutString != null) {
			try {
				Layout layout = Layout.valueOf(layoutString);
				builder.layout(layout);
			} catch (RuntimeException e) {
				return Response
						.status(Response.Status.BAD_REQUEST)
						.cacheControl(never())
						.entity("'" + layoutString
								+ "' is not a valid layout name.").build();
			}
		}

		SharedEntity.SharePermissions permissions;
		if (global) {
			permissions = SharedEntity.SharePermissions.GLOBAL;
		} else {
			permissions = SharedEntity.SharePermissions.PRIVATE;
		}
		builder.permissions(permissions);

		PortalPage portal = portalPageService.createPortalPage(context,
				builder.build());

		if (favorite) {
			favouritesService.addFavourite(context, portal);
		}

		return Response
				.ok()
				.cacheControl(never())
				.entity(new PortalPageBean(portal, favouritesService
						.isFavourite(user, portal))).build();
	}

    private Iterable<PortalPageBean> asBeans(final ApplicationUser user, Iterable<? extends PortalPage> portalPages)
    {
        return Iterables.transform(portalPages, new Function<PortalPage, PortalPageBean>()
        {
            @Override
            public PortalPageBean apply(PortalPage input)
            {
                return new PortalPageBean(input, favouritesService.isFavourite(user, input));
            }
        });
    }

    @JsonAutoDetect
    public static class PortalPageBean
    {
        private Long id;
        private String name;
        private String owner;
        private String description;
        private Long favouriteCount;
        private boolean favourite;

        public PortalPageBean()
        {
        }

        public PortalPageBean(PortalPage page, boolean favourite)
        {
            this.id = page.getId();
            this.name = page.getName();
            this.description = page.getDescription();
            this.owner = page.getOwnerUserName();
            this.favouriteCount = page.getFavouriteCount();
            this.favourite = favourite;
        }

        public Long getId()
        {
            return id;
        }

        public void setId(Long id)
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getOwner()
        {
            return owner;
        }

        public void setOwner(String owner)
        {
            this.owner = owner;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public Long getFavouriteCount()
        {
            return favouriteCount;
        }

        public void setFavouriteCount(Long favouriteCount)
        {
            this.favouriteCount = favouriteCount;
        }

        public boolean isFavourite()
        {
            return favourite;
        }

        public void setFavourite(boolean favourite)
        {
            this.favourite = favourite;
        }
    }
}
