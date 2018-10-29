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
import com.atlassian.jira.config.FeatureFlag;
import com.atlassian.jira.config.FeatureManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.fugue.Option;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import java.util.function.Supplier;

import static com.atlassian.jira.component.ComponentAccessor.getUserUtil;

/**
 * Use this backdoor to manipulate Dark Features as part of setup for tests.
 * <p>
 * This class should only be called by the <code>com.atlassian.jira.testkit.client.DarkFeaturesControl</code>.
 *
 * @since v5.0
 */
@Path("darkFeatures")
public class DarkFeaturesBackdoor {
    @GET
    @AnonymousAllowed
    @Path("user/enable")
    public Response enableForUser(@QueryParam("username") String username, @QueryParam("feature") String feature) {
        ApplicationUser user = getUserUtil().getUserByName(username);
        getFeatureManager().enableUserDarkFeature(user, feature);

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("user/disable")
    public Response disableForUser(@QueryParam("username") String username, @QueryParam("feature") String feature) {
        ApplicationUser user = getUserUtil().getUserByName(username);
        getFeatureManager().disableUserDarkFeature(user, feature);

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("site/enable")
    public Response enableForSite(@QueryParam("feature") String feature) {
        getFeatureManager().enableSiteDarkFeature(feature);

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("site/disable")
    public Response disableForSite(@QueryParam("feature") String feature) {
        getFeatureManager().disableSiteDarkFeature(feature);

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("global/enabled")
    public Response isGloballyEnabled(@QueryParam("feature") String feature) {
        final FeatureManager featureManager = getFeatureManager();
        final Option<FeatureFlag> featureFlag = featureManager.getFeatureFlag(feature);

        // we know that globally it is considered enabled. But is that because it is truly global, or only been turned on for user?
        // - if the feature is a Feature Flag and it is on by default, then we know that globally it is on
        // - or if it either a Feature Flag or string feature, it could have been enabled globally by setting the correct string value
        // - otherwise it is not enabled
        final boolean isEnabled;
        if (isFeatureEnabled(featureManager, feature, featureFlag)) {
            isEnabled = isFeatureFlagOnByDefault(featureFlag) || isEnabledFeatureCreatedAsGlobalDarkFeature(featureManager, feature, featureFlag);
        } else if (isFeatureFlagOnByDefault(featureFlag)) {
            // it's a feature flag and enabled by default, so who disabled it.
            // if the disable flag exists in global features, then it is off globally, otherwise only off for current user
            isEnabled = !isDisabledFeatureCreatedAsGlobalDarkFeature(featureManager, feature, featureFlag);
        } else {
            isEnabled = false;
        }

        return Response.ok(Boolean.toString(isEnabled)).build();
    }

    private boolean isFeatureEnabled(final FeatureManager featureManager, final String feature, final Option<FeatureFlag> featureFlagOption) {
        // if this is a FeatureFlag, then the isEnabled method for string handles this correctly to return the default state if not been overridden.
        // however to be explicit, as that behaviour is not documented, let's use the retrieved feature flag if it exists and check that, else,
        // call the string method ourselves knowing that it won't find a matching feature flag.
        return featureFlagOption
                .map(featureManager::isEnabled)
                .getOrElse((Supplier<? extends Boolean>) () -> featureManager.isEnabled(feature));
    }

    private boolean isFeatureFlagOnByDefault(Option<FeatureFlag> featureFlagOption) {
        return featureFlagOption.exists(FeatureFlag::isOnByDefault);
    }

    private boolean isEnabledFeatureCreatedAsGlobalDarkFeature(final FeatureManager featureManager, final String feature, final Option<FeatureFlag> featureFlagOption) {
        // if it is off by default, then how was it turned on? Want to return true if it is done globally and not only for our current user
        // If this is a FeatureFlag, then it needs to check <key>.enabled, otherwise just <key>
        final String featureKeyEnabled = featureFlagOption.map(FeatureFlag::enabledFeatureKey).getOrElse(feature);
        return featureManager.getDarkFeatures().getGlobalEnabledFeatureKeys().contains(featureKeyEnabled);
    }

    private boolean isDisabledFeatureCreatedAsGlobalDarkFeature(final FeatureManager featureManager, final String feature, final Option<FeatureFlag> featureFlagOption) {
        final String featureKeyDisabled = featureFlagOption.map(FeatureFlag::disabledFeatureKey).getOrElse(feature);
        return featureManager.getDarkFeatures().getGlobalEnabledFeatureKeys().contains(featureKeyDisabled);
    }

    @VisibleForTesting
    protected FeatureManager getFeatureManager() {
        return ComponentAccessor.getComponent(FeatureManager.class);
    }
}
