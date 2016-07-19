/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.model.FeatureFlag;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Dark Features.
 * <p>
 * See <code>com.atlassian.jira.testkit.plugin.DarkFeaturesBackdoor</code> in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class DarkFeaturesControl extends BackdoorControl<DarkFeaturesControl> {

    public DarkFeaturesControl(JIRAEnvironmentData environmentData) {
        super(environmentData);
    }

    public void enableForUser(String username, String feature) {
        get(createResource().path("darkFeatures").path("user").path("enable").queryParam("username", username).queryParam("feature", feature));
    }

    public void disableForUser(String username, String feature) {
        get(createResource().path("darkFeatures").path("user").path("disable").queryParam("username", username).queryParam("feature", feature));
    }

    public void enableForSite(String feature) {
        get(createResource().path("darkFeatures").path("site").path("enable").queryParam("feature", feature));
    }

    public void disableForSite(String feature) {
        get(createResource().path("darkFeatures").path("site").path("disable").queryParam("feature", feature));
    }

    public boolean isGlobalEnabled(String feature) {
        return Boolean.parseBoolean(createResource().path("darkFeatures").path("global").path("enabled").queryParam("feature", feature).get(String.class));
    }

    /**
     * Turns feature on if it off by default, otherwise has no effect
     *
     * @param featureFlag
     * @since v7.2.15
     */
    public void enableFeatureFlagForSite(final FeatureFlag featureFlag) {
        enableForSite(featureFlag.enabledFeatureKey());
    }

    /**
     * Turns feature off if it is on by default, otherwise has no effect
     *
     * @param featureFlag
     * @since v7.2.15
     */
    public void disableFeatureFlagForSite(final FeatureFlag featureFlag) {
        enableForSite(featureFlag.disabledFeatureKey());
    }

    /**
     * @since v7.2.15
     */
    public void forceEnableFeatureFlagForSite(final FeatureFlag featureFlag) {
        disableForSite(featureFlag.disabledFeatureKey());
        enableForSite(featureFlag.enabledFeatureKey());
    }

    /**
     * @since v7.2.15
     */
    public void forceDisableFeatureFlagForSite(final FeatureFlag featureFlag) {
        disableForSite(featureFlag.enabledFeatureKey());
        enableForSite(featureFlag.disabledFeatureKey());
    }

    /**
     * Restores the feature flag to its defaultOn state
     *
     * @param featureFlag
     * @since v7.2.15
     */
    public void resetFeatureFlagStateForSite(final FeatureFlag featureFlag) {
        disableForSite(featureFlag.disabledFeatureKey());
        disableForSite(featureFlag.enabledFeatureKey());
    }

    /**
     * Turns feature on for user if it off by default, otherwise has no effect
     *
     * @param featureFlag
     * @since v7.2.15
     */
    public void enableFeatureFlagForUser(final String username, final FeatureFlag featureFlag) {
        enableForUser(username, featureFlag.enabledFeatureKey());
    }

    /**
     * Turns feature off for user if it is on by default, otherwise has no effect
     *
     * @param featureFlag
     * @since v7.2.15
     */
    public void disableFeatureFlagForUser(final String username, final FeatureFlag featureFlag) {
        enableForUser(username, featureFlag.disabledFeatureKey());
    }

    /**
     * @since v7.2.15
     */
    public void forceEnableFeatureFlagForUser(final String username, final FeatureFlag featureFlag) {
        disableForUser(username, featureFlag.disabledFeatureKey());
        enableForUser(username, featureFlag.enabledFeatureKey());
    }


    /**
     * @since v7.2.15
     */
    public void forceDisableFeatureFlagForUser(final String username, final FeatureFlag featureFlag) {
        disableForUser(username, featureFlag.enabledFeatureKey());
        enableForUser(username, featureFlag.disabledFeatureKey());
    }

    /**
     * Restores the feature flag for user to its defaultOn state
     *
     * @param featureFlag
     * @since v7.2.15
     */
    public void resetFeatureFlagStateForUser(final String username, final FeatureFlag featureFlag) {
        disableForUser(username, featureFlag.disabledFeatureKey());
        disableForUser(username, featureFlag.enabledFeatureKey());
    }

}
