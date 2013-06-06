package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;

/**
 * Backdoor control for changing user preferences.
 *
 * @since 6.0.19
 */
@SuppressWarnings ("UnusedDeclaration")
public class UserPreferencesControl extends BackdoorControl<UserPreferencesControl>
{
    private final String username;

    UserPreferencesControl(JIRAEnvironmentData environmentData, String username)
    {
        super(environmentData);
        this.username = username;
    }

    /**
     * Sets a String user preference.
     *
     * @param name the preference name
     * @param value the preference value
     * @return this
     * @since 6.0.19
     */
    public UserPreferencesControl set(String name, String value)
    {
        return setPreference("string", name, value);
    }

    /**
     * Sets a Boolean user preference.
     *
     * @param name the preference name
     * @param value the preference value
     * @return this
     * @since 6.0.19
     */
    public UserPreferencesControl set(String name, Boolean value)
    {
        return setPreference("boolean", name, value);
    }

    /**
     * Gets a user preference String value in JIRA
     *
     * @param name the preference name
     * @return the String property value
     * @since 6.0.28
     */
    public String getString(String name)
    {
        return createResource().path(name)
                .queryParam("type", "string")
                .queryParam("username", username)
                .get(String.class);
    }

    /**
     * Gets a user preference Long value in JIRA
     *
     * @param name the preference name
     * @return the Long property value
     * @since 6.0.28
     */
    public Long getLong(String name)
    {
        return createResource().path(name)
                .queryParam("type", "long")
                .queryParam("username", username)
                .get(Long.class);
    }

    /**
     * Gets a user preference Boolean value in JIRA
     *
     * @param name the preference name
     * @return the Boolean property value
     * @since 6.0.28
     */
    public Boolean getBoolean(String name)
    {
        return createResource().path(name)
                .queryParam("type", "boolean")
                .queryParam("username", username)
                .get(Boolean.class);
    }

    /**
     * Sets a Long user preference.
     *
     * @param name the preference name
     * @param value the preference value
     * @return this
     * @since 6.0.19
     */
    public UserPreferencesControl set(String name, Long value)
    {
        return setPreference("long", name, value);
    }

    /**
     * Removes a user preference.
     *
     * @param name the preference name
     * @return this
     * @since 6.0.19
     */
    public UserPreferencesControl remove(String name)
    {
        createResource().path("preference").path(name).delete();
        return this;
    }

    @Override
    protected WebResource createResource()
    {
        return super.createResource().path("userProfile").path("preference");
    }

    private UserPreferencesControl setPreference(String type, String name, Object value)
    {
        createResource().path(name)
                .queryParam("username", username)
                .queryParam("type", type)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .put(value.toString());

        return this;
    }
}
