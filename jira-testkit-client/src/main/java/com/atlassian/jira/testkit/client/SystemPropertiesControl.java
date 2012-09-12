package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Allows setting and unsetting of system properties.
 *
 * See {@link com.atlassian.jira.testkit.plugin.SystemPropertyBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.2
 */
public class SystemPropertiesControl extends BackdoorControl<SystemPropertiesControl>
{
    public SystemPropertiesControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * Gets a system property in JIRA using {@link System#getProperty(String)}
     *
     * @param name a String containing the property name
     * @return the property value, or null if not set
     */
    public String getProperty(String name)
    {
        return propertyResource(name).get(String.class);
    }

    /**
     * Sets a system property in JIRA using {@link System#setProperty(String, String)}.
     *
     * @param name a String containing the property name
     * @param value a String containing the property value
     */
    public void setProperty(String name, String value)
    {
        propertyResource(checkNotNull(name)).queryParam("value", checkNotNull(value)).post();
    }

    /**
     * Unsets a system property in JIRA.
     *
     * @param name a String containing the property name
     */
    public void unsetProperty(String name)
    {
        propertyResource(checkNotNull(name)).delete();
    }

    private WebResource propertyResource(String propertyName)
    {
        return createResource().path("systemproperty").path(propertyName);
    }
}
