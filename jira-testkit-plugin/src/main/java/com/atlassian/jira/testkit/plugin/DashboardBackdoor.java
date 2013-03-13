/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.favourites.FavouritesService;
import com.atlassian.jira.bc.portal.PortalPageService;
import com.atlassian.jira.portal.PortalPage;
import com.atlassian.jira.portal.PortletConfiguration;
import com.atlassian.jira.portal.PortletConfigurationManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

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
        
        User user = userUtil.getUser(username);
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

    private Iterable<PortalPageBean> asBeans(final User user, Iterable<? extends PortalPage> portalPages)
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
