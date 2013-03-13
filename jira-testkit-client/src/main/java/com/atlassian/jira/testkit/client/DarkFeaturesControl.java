/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Dark Features.
 *
 * See {@link com.atlassian.jira.testkit.plugin.DarkFeaturesBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class DarkFeaturesControl extends BackdoorControl<DarkFeaturesControl>
{
    public DarkFeaturesControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void enableForUser(String username, String feature)
    {
        get(createResource().path("darkFeatures").path("user").path("enable").queryParam("username", username).queryParam("feature", feature));
    }

    public void disableForUser(String username, String feature)
    {
        get(createResource().path("darkFeatures").path("user").path("disable").queryParam("username", username).queryParam("feature", feature));
    }

    public void enableForSite(String feature)
    {
        get(createResource().path("darkFeatures").path("site").path("enable").queryParam("feature", feature));
    }

    public void disableForSite(String feature)
    {
        get(createResource().path("darkFeatures").path("site").path("disable").queryParam("feature", feature));
    }
    
    public boolean isGlobalEnabled(String feature)
    {
        return Boolean.parseBoolean(createResource().path("darkFeatures").path("global").path("enabled").queryParam("feature", feature).get(String.class));
    }
}
