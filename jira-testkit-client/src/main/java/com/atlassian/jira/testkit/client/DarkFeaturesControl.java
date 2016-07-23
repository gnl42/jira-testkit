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
import com.atlassian.jira.testkit.client.rules.FeatureFlagRule;
import com.atlassian.jira.testkit.client.rules.FeatureFlagUserRule;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Dark Features that are managed
 * by JIRAs Feature Manager component.
 * <p>
 * See <code>com.atlassian.jira.testkit.plugin.DarkFeaturesBackdoor</code> in jira-testkit-plugin for backend.
 * <p>
 * When using this control with {@link FeatureFlag} the way to this in a test is as follows:
 * # Call {@link #enableForSite(FeatureFlag)} or {@link #disableForSite(FeatureFlag)}
 * # Run test code
 * # Call {@link #resetForSite(FeatureFlag)}
 * <p>
 * This also applies to the methods to set for a specific user as opposed to the site.
 * <p>
 * Alternatively, the rules {@link FeatureFlagRule} and {@link FeatureFlagUserRule} exist to assist with the usage of this
 * control.
 *
 * @since v5.0
 */
public class DarkFeaturesControl extends BackdoorControl<DarkFeaturesControl> {

    public DarkFeaturesControl(JIRAEnvironmentData environmentData) {
        super(environmentData);
    }

    /**
     * Create a key for the provided user, of the provided feature name.
     * <p>
     * NB: This is not actually enabling a feature, rather it is creating a record in the features database, and the feature itself
     * is determined in code as to whether it means on/off.
     * <p>
     * For example, a feature of "my.feature.enabled" will do the following for the provided user:
     * - create "my.feature.enabled"
     *
     * @deprecated since v7.2.15 we should prefer to create new features in code using the Feature Flag approach, and therefore should
     * be using {@link #enableForUser(String, FeatureFlag)} or {@link #disableForUser(String, FeatureFlag)} instead.
     */
    @Deprecated
    public void enableForUser(String username, String feature) {
        get(createResource().path("darkFeatures").path("user").path("enable").queryParam("username", username).queryParam("feature", feature));
    }

    /**
     * Remove a key for the provided user, of the provided feature name.
     * <p>
     * NB: This is not actually disabling a feature, rather it is removing a record in the features database if it existed,
     * and the feature itself is determined in code as to whether it means on/off.
     * <p>
     * For example, a feature of "my.feature.enabled" will do the following for the provided user:
     * - remove "my.feature.enabled"
     *
     * @deprecated since v7.2.15 we should prefer to create new features in code using the Feature Flag approach, and therefore should
     * be using {@link #enableForUser(String, FeatureFlag)} or {@link #disableForUser(String, FeatureFlag)} instead.
     */
    @Deprecated
    public void disableForUser(String username, String feature) {
        get(createResource().path("darkFeatures").path("user").path("disable").queryParam("username", username).queryParam("feature", feature));
    }

    /**
     * Create a key for the site, of the provided feature name.
     * <p>
     * NB: This is not actually enabling a feature, rather it is creating a record in the features database, and the feature itself
     * is determined in code as to whether it means on/off.
     * <p>
     * For example, a feature of "my.feature.enabled" will do the following for the provided user:
     * - create "my.feature.enabled"
     *
     * @deprecated since v7.2.15 we should prefer to create new features in code using the Feature Flag approach, and therefore should
     * be using {@link #enableForSite(FeatureFlag)} or {@link #disableForSite(FeatureFlag)} instead.
     */
    @Deprecated
    public void enableForSite(String feature) {
        get(createResource().path("darkFeatures").path("site").path("enable").queryParam("feature", feature));
    }

    /**
     * Remove a key for the site, of the provided feature name.
     * <p>
     * NB: This is not actually disabling a feature, rather it is removing a record in the features database if it existed,
     * and the feature itself is determined in code as to whether it means on/off.
     * <p>
     * For example, a feature of "my.feature.enabled" will do the following for the provided user:
     * - remove "my.feature.enabled"
     *
     * @deprecated since v7.2.15 we should prefer to create new features in code using the Feature Flag approach, and therefore should
     * be using {@link #enableForUser(String, FeatureFlag)} or {@link #disableForUser(String, FeatureFlag)} instead.
     */
    @Deprecated
    public void disableForSite(String feature) {
        get(createResource().path("darkFeatures").path("site").path("disable").queryParam("feature", feature));
    }

    /**
     * Return whether the feature is enabled, and if it is for the site.
     * <p>
     * This will return true if the feature string has been created. This means that if you're feature is "my.feature.disabled" and
     * you have created it for the site, perhaps by calling {@link #enableForSite(String)} then this would return true.
     *
     * @return True if enabled for the site, false if not enabled or only for current user
     * @deprecated since v7.2.15 we should prefer to create new features in code using the Feature Flag approach, and therefore should
     * be using {@link #isGlobalEnabled(FeatureFlag)} instead.
     */
    @Deprecated
    public boolean isGlobalEnabled(String feature) {
        return Boolean.parseBoolean(createResource().path("darkFeatures").path("global").path("enabled").queryParam("feature", feature).get(String.class));
    }

    /**
     * Explicitly turns on the given feature flag for site.
     * <p>
     * It has the effect of removing the disabled state of the flag if previously set, and setting the enabled state to
     * to ensure that the default state of the flag is overridden.
     * <p>
     * For example, a feature flag of "my.feature" will do the following for the site:
     * - remove "my.feature.disabled"
     * - create "my.feature.enabled"
     * <p>
     * NB: Remember to call {@link #resetForSite(FeatureFlag)} when done with this flag state
     *
     * @since v7.2.15
     */
    public void enableForSite(final FeatureFlag featureFlag) {
        disableForSite(featureFlag.disabledFeatureKey());
        enableForSite(featureFlag.enabledFeatureKey());
    }

    /**
     * Explicitly turns off the given feature flag for site.
     * <p>
     * It has the effect of removing the enabled state of the flag if previously set, and setting the disabled state to
     * to ensure that the default state of the flag is overridden.
     * <p>
     * * For example, a feature flag of "my.feature" will do the following for the site:
     * - remove "my.feature.enabled"
     * - create "my.feature.disabled"
     * <p>
     * NB: Remember to call {@link #resetForSite(FeatureFlag)} when done with this flag state
     *
     * @since v7.2.15
     */
    public void disableForSite(final FeatureFlag featureFlag) {
        disableForSite(featureFlag.enabledFeatureKey());
        enableForSite(featureFlag.disabledFeatureKey());
    }

    /**
     * Restores the feature flag to its default state.
     * <p>
     * For example, a feature flag of "my.feature" will do the following for the site:
     * - remove "my.feature.disabled"
     * - remove "my.feature.enabled"
     *
     * @since v7.2.15
     */
    public void resetForSite(final FeatureFlag featureFlag) {
        disableForSite(featureFlag.disabledFeatureKey());
        disableForSite(featureFlag.enabledFeatureKey());
    }

    /**
     * Explicitly turns on the given feature flag for user.
     * <p>
     * It has the effect of removing the disabled state of the flag if previously set, and setting the enabled state to
     * to ensure that the default state of the flag is overridden.
     * <p>
     * For example, a feature flag of "my.feature" will do the following for the provided user:
     * - remove "my.feature.disabled"
     * - create "my.feature.enabled"
     * <p>
     * NB: Remember to call {@link #resetForUser(String, FeatureFlag)} when done with this flag state
     *
     * @since v7.2.15
     */
    public void enableForUser(final String username, final FeatureFlag featureFlag) {
        disableForUser(username, featureFlag.disabledFeatureKey());
        enableForUser(username, featureFlag.enabledFeatureKey());
    }

    /**
     * Explicitly turns off the given feature flag for user.
     * <p>
     * It has the effect of removing the enabled state of the flag if previously set, and setting the disabled state to
     * to ensure that the default state of the flag is overridden.
     * <p>
     * * For example, a feature flag of "my.feature" will do the following for the provided user:
     * - remove "my.feature.enabled"
     * - create "my.feature.disabled"
     * <p>
     * NB: Remember to call {@link #resetForUser(String, FeatureFlag)} when done with this flag state
     *
     * @since v7.2.15
     */
    public void disableForUser(final String username, final FeatureFlag featureFlag) {
        disableForUser(username, featureFlag.enabledFeatureKey());
        enableForUser(username, featureFlag.disabledFeatureKey());
    }

    /**
     * Restores the feature flag for user to its default state.
     * <p>
     * For example, a feature flag of "my.feature" will do the following for the provided user:
     * - remove "my.feature.disabled"
     * - remove "my.feature.enabled"
     *
     * @since v7.2.15
     */
    public void resetForUser(final String username, final FeatureFlag featureFlag) {
        disableForUser(username, featureFlag.disabledFeatureKey());
        disableForUser(username, featureFlag.enabledFeatureKey());
    }

    /**
     * Return whether the feature flag is enabled, and if it is for the site.
     * <p>
     * This will return true in the following cases:
     * <p>
     * - The feature flag has a default state of on
     * - The feature flag has a default state of off, but the flag has been enabled. So if my flag is "my.feature" but the
     * key "my.feature.enabled" has been created for the site, perhaps by calling {@link #enableForSite(FeatureFlag)},
     * then this would also return true.
     *
     * @return True if enabled for the site, false if not enabled or only for current user
     * @since v7.2.15
     */
    public boolean isGlobalEnabled(final FeatureFlag featureFlag) {
        return isGlobalEnabled(featureFlag.featureKey());
    }

}
