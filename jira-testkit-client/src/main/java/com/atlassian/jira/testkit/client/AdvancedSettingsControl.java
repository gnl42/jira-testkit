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
 * Use this class from func/selenium/page-object tests that need to manipulate Advanced Configuration.
 *
 * See {@link com.atlassian.jira.testkit.plugin.ApplicationPropertiesBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class AdvancedSettingsControl extends BackdoorControl<AdvancedSettingsControl>
{
    public AdvancedSettingsControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void setTextFieldCharacterLengthLimit(long limit)
    {
        createResource().path("applicationProperties/text/set").post(String.class, new KeyValueHolder("jira.text.field.character.limit", String.valueOf(limit)));
    }

    private static class KeyValueHolder
    {
        public String key;
        public String value;

        public KeyValueHolder(String key, String value)
        {
            this.key = key;
            this.value = value;
        }
    }
}
