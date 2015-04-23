/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.FeatureManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.component.ComponentAccessor.getUserUtil;

/**
 * Use this backdoor to manipulate Dark Features as part of setup for tests.
 *
 * This class should only be called by the <code>com.atlassian.jira.testkit.client.DarkFeaturesControl</code>.
 *
 * @since v5.0
 */
@Path ("darkFeatures")
public class DarkFeaturesBackdoor
{
    @GET
    @AnonymousAllowed
    @Path("user/enable")
    public Response enableForUser(@QueryParam ("username") String username, @QueryParam ("feature") String feature)
    {
        ApplicationUser user = getUserUtil().getUserByName(username);
        getFeatureManager().enableUserDarkFeature(user, feature);

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("user/disable")
    public Response disableForUser(@QueryParam ("username") String username, @QueryParam ("feature") String feature)
    {
        ApplicationUser user = getUserUtil().getUserByName(username);
        getFeatureManager().disableUserDarkFeature(user, feature);

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("site/enable")
    public Response enableForSite(@QueryParam ("feature") String feature)
    {
        getFeatureManager().enableSiteDarkFeature(feature);

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("site/disable")
    public Response disableForSite(@QueryParam ("feature") String feature)
    {
        getFeatureManager().disableSiteDarkFeature(feature);

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("global/enabled")
    public Response isEnabled(@QueryParam ("feature") String feature)
    {
        final boolean isEnabled = getFeatureManager().getDarkFeatures().getGlobalEnabledFeatureKeys().contains(feature);
        return Response.ok(Boolean.toString(isEnabled)).build();
    }

    private FeatureManager getFeatureManager()
    {
        return ComponentAccessor.getComponent(FeatureManager.class);
    }
}
