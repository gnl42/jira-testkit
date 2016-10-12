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
 * See <code>com.atlassian.jira.testkit.plugin.I18nBackdoor</code> in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class I18nControl extends BackdoorControl<UsersAndGroupsControl>
{
    public I18nControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public String getText(String key, String locale)
    {
        return createResource().path("i18n")
                .queryParam("key", key)
                .queryParam("locale", locale).get(String.class);
    }

    public String getText(String key, String value1, String locale)
    {
        return createResource().path("i18n")
                .queryParam("key", key)
                .queryParam("locale", locale)
                .queryParam("value1", value1)
                .get(String.class);
    }
}
