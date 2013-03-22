package com.atlassian.jira.testkit.client;

import com.atlassian.jira.webtests.util.JIRAEnvironmentData;

/**
 * Backdoor control for changing user preferences.
 *
 * @since v5.2.4
 */
public class UserPreferencesControl
{
    private final UserProfileControl userProfileControl;
    private final String username;

    public UserPreferencesControl(JIRAEnvironmentData environmentData, UserProfileControl userProfileControl, String username)
    {
        this.userProfileControl = userProfileControl;
        this.username = username;
    }

    /**
     * Sets a String user preference.
     *
     * @param name the preference name
     * @param value the preference value
     * @return this
     */
    public UserPreferencesControl set(String name, String value)
    {
        return setObject(name, value);
    }

    /**
     * Sets a Boolean user preference.
     *
     * @param name the preference name
     * @param value the preference value
     * @return this
     */
    public UserPreferencesControl set(String name, Boolean value)
    {
        return setObject(name, value);
    }

    /**
     * Sets a Long user preference.
     *
     * @param name the preference name
     * @param value the preference value
     * @return this
     */
    public UserPreferencesControl set(String name, Long value)
    {
        return setObject(name, value);
    }

    /**
     * Removes a user preference.
     *
     * @param name the preference name
     * @return this
     */
    public UserPreferencesControl remove(String name)
    {
        userProfileControl.createResource().path("preference").path(name).delete();
        return this;
    }

    private UserPreferencesControl setObject(String name, Object value)
    {
        userProfileControl.createResource().path("preference").path(name).put(value.toString());
        return this;
    }
}
