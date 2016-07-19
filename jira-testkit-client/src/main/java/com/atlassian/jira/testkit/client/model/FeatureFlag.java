/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.model;

import static com.atlassian.jira.util.dbc.Assertions.notNull;

/**
 * Feature Flag type to use for setting state, to override the defaultOn value of features
 *
 * @since v7.2.15
 */
public class FeatureFlag {

    public static final String POSTFIX_ENABLED = ".enabled";
    public static final String POSTFIX_DISABLED = ".disabled";

    private final String featureKey;

    private FeatureFlag(String featureKey) {
        notNull(featureKey);
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
     * Creates a new FeatureFlag with the specified key and off by default
     *
     * @param featureKey the key in play
     * @return a new feature flag
     */
    public static FeatureFlag featureFlag(final String featureKey) {
        return new FeatureFlag(featureKey);
    }

}
