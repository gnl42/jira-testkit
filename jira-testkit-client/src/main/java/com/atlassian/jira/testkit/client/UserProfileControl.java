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
 * Use this class from func/selenium/page-object tests that need to manipulate User Profiles.
 *
 * See com.atlassian.jira.testkit.plugin.UserProfileBackdoor in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
@SuppressWarnings ("UnusedDeclaration")
public class UserProfileControl extends BackdoorControl<UserProfileControl>
{
    private final JIRAEnvironmentData environmentData;

    public UserProfileControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
        this.environmentData = environmentData;
    }

    /**
     * Changes the given user's email format to the one supplied.
     *
     * @param username the user to change the email format for
     * @param format either "html" or "text"
     */
    @SuppressWarnings ("UnusedDeclaration")
    public void changeUserNotificationType(String username, String format)
    {
        get(createResource().path("userProfile/notificationType/set")
                .queryParam("username", username)
                .queryParam("format", format));
    }

    /**
     * Changes the given user's time zone to one supplied.
     *
     * @param username the user to change the time zone for.
     * @param timeZone the time zone to set.
     */
    @SuppressWarnings ("UnusedDeclaration")
    public void setUserTimeZone(String username, String timeZone)
    {
        createResource().path("userProfile/timeZone")
                .queryParam("username", username)
                .queryParam("timeZone", timeZone)
                .put();
    }

    /**
     * Returns a new UserPreferencesControl for the user with {@code username}.
     *
     * @param username     a username
     * @return a new UserPreferencesControl
     */
    public UserPreferencesControl preferences(String username)
    {
        return new UserPreferencesControl(environmentData, username);
    }
}
