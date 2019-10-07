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
import com.atlassian.jira.portal.PortalPageManager;
import com.atlassian.jira.portal.PortletConfiguration;
import com.atlassian.jira.portal.PortletConfigurationManager;
import com.atlassian.jira.sharing.SharePermissionImpl;
import com.atlassian.jira.sharing.SharedEntity;
import com.atlassian.jira.sharing.type.ShareType;
import com.atlassian.jira.testkit.plugin.util.Errors;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * @since v5.0.3
 */
@AnonymousAllowed
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/dashboard")
public class DashboardBackdoor {
    private final PortalPageService portalPageService;
    private final FavouritesService favouritesService;
    private final PortletConfigurationManager portletConfigurationManager;
    private final PortalPageManager portalPageManager;
    private final UserManager userManager;

    public DashboardBackdoor(
            PortalPageService portalPageService,
            UserManager userManager,
            FavouritesService favouritesService,
            PortletConfigurationManager portletConfigurationManager,
            PortalPageManager portalPageManager) {
        this.portalPageService = portalPageService;
        this.userManager = userManager;
        this.favouritesService = favouritesService;
        this.portletConfigurationManager = portletConfigurationManager;
        this.portalPageManager = portalPageManager;
    }

    @GET
    @Path("/{id}")
    public Response getDashboardById(@PathParam("id") Long id, @QueryParam("username") String username) {
        final PortalPage portalPage = portalPageManager.getPortalPageById(id);

        if (portalPage != null) {
            boolean isFavourite = calculateIsFavourite(username, portalPage);
            return Response.ok().cacheControl(never())
                    .entity(new PortalPageBean(portalPage, isFavourite)).build();
        } else {
            return Response.status(NOT_FOUND).cacheControl(never())
                    .entity("Portal page with given ID does not exist.").build();
        }
    }

    private boolean calculateIsFavourite(String username, PortalPage portalPage) {
        username = StringUtils.trimToNull(username);
        if(username != null) {
            final ApplicationUser user = userManager.getUserByName(username);
            return favouritesService.isFavourite(user, portalPage);
        }
        return false;
    }

    @GET
    @Path("my")
    public Response getMyDasbhoards(@QueryParam("username") String username) {
        username = StringUtils.trimToNull(username);
        if (username == null) {
            return Response.status(Response.Status.BAD_REQUEST).cacheControl(never())
                    .entity("No user passed.").build();
        }

        ApplicationUser user = userManager.getUserByName(username);
        if (user == null) {
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
     * @param username     user for which to create the dashboard.
     * @param name         Name of the dashboard. This is shown in the drop down
     *                     containing all visible dashboards and in the dashboard
     *                     management view.
     * @param description  Description of the dashboard.
     * @param global       If <code>true</code> a public dashboard viewable by all is
     *                     created, otherwise the new dashboard will be private.
     * @param layoutString A layout to choose for the new dashboard. Valid values are the
     *                     names of the {@link Layout} enumerations.
     * @param favorite     If <code>true</code> the new dashboard will be added to the
     *                     favorite list.
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
        ApplicationUser user = userManager.getUserByName(username);
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

    /**
     * Removes dashboard with specified id for given user.
     *
     * @param username User for which delete dashboard.
     * @param id       Id of the dashboard to be deleted.
     */
    @GET
    @Path("delete")
    public Response deleteDashboard(@QueryParam("username") String username, @QueryParam("id") Long id) {
        ApplicationUser user = userManager.getUserByName(username);
        SimpleErrorCollection errorCollection = new SimpleErrorCollection();
        JiraServiceContext context = new JiraServiceContextImpl(user, errorCollection);

        portalPageService.deletePortalPage(context, id);
        if (errorCollection.hasAnyErrors()) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .cacheControl(never())
                    .entity(Errors.of(errorCollection)).build();
        }
        return Response
                .ok()
                .cacheControl(never()).build();
    }

    /**
     * Updates dashboard with specified id for given user.
     *
     * @param username       User for which update the dashboard.
     * @param id             Id of the dashboard to be updated.
     * @param name           Name of the dashboard. This is shown in the drop down containing all visible dashboards
     *                       and in the dashboard management view.
     * @param ownername      Name of the user who owns the dashboard.
     * @param description    Description of the dashboard.
     * @param shareGroupName Group name with which dashboard will be shared with rights to view
     * @param favorite       If <code>true</code> the dashboard will be added to the favorite list,
     *                       if <code>false</code> it will be removed from it.
     * @return The information about the updated dashboard.
     */
    @GET
    @Path("update")
    public Response updateDashboard(@QueryParam("username") String username, @QueryParam("id") Long id,
                                    @QueryParam("name") String name, @QueryParam("ownername") String ownername,
                                    @QueryParam("description") String description,
                                    @QueryParam("shareGroupName") String shareGroupName,
                                    @QueryParam("favorite") boolean favorite) {
        ApplicationUser user = userManager.getUserByName(username);
        SimpleErrorCollection errorCollection = new SimpleErrorCollection();
        JiraServiceContext context = new JiraServiceContextImpl(user,
                errorCollection);

        PortalPage.Builder builder = PortalPage.id(id).owner(userManager.getUserByName(ownername));

        if (description != null) {
            builder.description(description);
        }
        if (name != null) {
            builder.name(name);
        }
        SharedEntity.SharePermissions permissions;
        if (shareGroupName != null) {
            permissions = new SharedEntity.SharePermissions(Collections.singleton(
                    new SharePermissionImpl(ShareType.Name.GROUP, shareGroupName, null)));
            builder.permissions(permissions);
        } else {
            permissions = SharedEntity.SharePermissions.PRIVATE;
        }
        builder.permissions(permissions);

        PortalPage portalPage = builder.build();
        portalPageService.updatePortalPage(context, portalPage, favorite);
        if (errorCollection.hasAnyErrors()) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .cacheControl(never())
                    .entity(Errors.of(errorCollection)).build();
        }

        return Response
                .ok()
                .cacheControl(never())
                .entity(new PortalPageBean(portalPage, favouritesService
                        .isFavourite(user, portalPage))).build();
    }

    private Iterable<PortalPageBean> asBeans(final ApplicationUser user, Iterable<? extends PortalPage> portalPages) {
        return Iterables.transform(portalPages, new Function<PortalPage, PortalPageBean>() {
            @Override
            public PortalPageBean apply(PortalPage input) {
                return new PortalPageBean(input, favouritesService.isFavourite(user, input));
            }
        });
    }

    @JsonAutoDetect
    public static class PortalPageBean {
        private Long id;
        private String name;
        private String owner;
        private String description;
        private Long favouriteCount;
        private boolean favourite;

        public PortalPageBean() {
        }

        public PortalPageBean(PortalPage page, boolean favourite) {
            this.id = page.getId();
            this.name = page.getName();
            this.description = page.getDescription();
            this.owner = page.getOwnerUserName();
            this.favouriteCount = page.getFavouriteCount();
            this.favourite = favourite;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getFavouriteCount() {
            return favouriteCount;
        }

        public void setFavouriteCount(Long favouriteCount) {
            this.favouriteCount = favouriteCount;
        }

        public boolean isFavourite() {
            return favourite;
        }

        public void setFavourite(boolean favourite) {
            this.favourite = favourite;
        }
    }
}
