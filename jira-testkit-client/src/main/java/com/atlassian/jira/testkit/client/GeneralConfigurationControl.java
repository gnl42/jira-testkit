/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.model.JiraMode;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate General Configuration.
 *
 * See <code>com.atlassian.jira.testkit.plugin.ApplicationPropertiesBackdoor</code> in jira-testkit-plugin for backend.
 *
 * @since v5.1
 */
public class GeneralConfigurationControl extends BackdoorControl<GeneralConfigurationControl>
{
    public GeneralConfigurationControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public GeneralConfigurationControl allowUnassignedIssues()
    {
        setAllowUnassignedIssues(true);
        return this;
    }

    public GeneralConfigurationControl disallowUnassignedIssues()
    {
        setAllowUnassignedIssues(false);
        return this;
    }

    public GeneralConfigurationControl setContactAdminFormOn()
    {
        toggleContactAdminForm(true);
        return this;
    }

    public GeneralConfigurationControl setContactAdminFormOff()
    {
        toggleContactAdminForm(false);
        return this;
    }

    public GeneralConfigurationControl setCaptchaOnSignupOn()
    {
        toggleCaptchaOnSignup(true);
        return this;
    }

    public GeneralConfigurationControl setCaptchaOnSignupOff()
    {
        toggleCaptchaOnSignup(false);
        return this;
    }

    public GeneralConfigurationControl setJiraMode(JiraMode mode)
    {
        createResource().path("applicationProperties/string/set").post(new KeyValueHolder("jira.mode", mode.optionValue()));
        return this;
    }



    public void toggleContactAdminForm(boolean isOn)
    {
        get(createResource().path("applicationProperties/option/set")
                .queryParam("key", "jira.show.contact.administrators.form")
                .queryParam("value", Boolean.toString(isOn)));
    }

    public void toggleCaptchaOnSignup(boolean isOn)
    {
        get(createResource().path("applicationProperties/option/set")
                .queryParam("key", "jira.option.captcha.on.signup")
                .queryParam("value", Boolean.toString(isOn)));
    }

    public void useGravatars(boolean allow)
    {
        createResource().path("gravatarSettings/allowGravatars")
                .post(Boolean.valueOf(allow));
    }

    private void setAllowUnassignedIssues(boolean flag)
    {
        get(createResource().path("applicationProperties/option/set")
                .queryParam("key", "jira.option.allowunassigned")
                .queryParam("value", Boolean.toString(flag)));
    }

    private static class KeyValueHolder
    {
        KeyValueHolder(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        public String key;
        public String value;
    }
}
