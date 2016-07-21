/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.model;

import javax.annotation.Nonnull;

import static com.atlassian.jira.util.dbc.Assertions.notBlank;

/**
 * Feature Flag type to use for setting state within {@link com.atlassian.jira.testkit.client.DarkFeaturesControl}
 * that is intended to override the default state of feature flags in code.
 *
 * @since v7.2.15
 */
public class FeatureFlag {

    private static final String POSTFIX_ENABLED = ".enabled";
    private static final String POSTFIX_DISABLED = ".disabled";

    private final String featureKey;

    private FeatureFlag(String featureKey) {
        this.featureKey = featureKey;
    }

    /**
     * @return this feature key with the enabled postfix of {@link #POSTFIX_ENABLED}
     */
    public String enabledFeatureKey() {
        return featureKey + POSTFIX_ENABLED;
    }

    /**
     * @return this feature key with the enabled postfix of {@link #POSTFIX_DISABLED}
     */
    public String disabledFeatureKey() {
        return featureKey + POSTFIX_DISABLED;
    }

    /**
     * @return this feature key
     */
    public String featureKey() {
        return featureKey;
    }

    /**
     * Creates a {@link FeatureFlag} model for use within {@link com.atlassian.jira.testkit.client.DarkFeaturesControl}.
     * <p>
     * This string should not end with .enabled or .disabled, as that will be appended automatically for you, depending
     * on whether you want to enable or disable this feature.
     * <p>
     * NB: A quick note on how the feature flags are used in code, and what they will do here:
     * <p>
     * Each feature flag has a defaultOn boolean, that determines if the feature is on or off, however this can be flip
     * by entering a certain value in JIRA.
     * <p>
     * - If defaultOn is false, we can flip the feature to enabled by adding the feature key string in JIRA: "feature key" + ".enabled", e.g. "my.feature.flag.enabled"
     * - If defaultOn is true, it can be flipped to disabled by adding the feature key string: "feature key" + ".disabled", e.g. "my.feature.flag.disabled"
     * <p>
     * All that needs to be provided here is the "feature key", ie. "my.feature"
     * <p>
     * This feature flag method of enabling/disabling code in JIRA should be preferred over using a string value on it's own
     *
     * @param featureKey the key in play that is not null or blank
     * @return a new feature flag key
     */
    public static FeatureFlag featureFlag(@Nonnull final String featureKey) {
        return new FeatureFlag(notBlank("featureKey", featureKey));
    }

}
