package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.BackdoorControl;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.sun.jersey.api.client.WebResource;

import javax.annotation.Nonnull;

/**
 * Provides convenient access to the REST API exposed by the applinks plugin.
 */
public class ApplinksPluginRestClient extends BackdoorControl
{
    /**
     * Constructor.
     *
     * @param jiraEnvironment the JIRA environment that hosts the applinks plugin
     */
    public ApplinksPluginRestClient(@Nonnull final JIRAEnvironmentData jiraEnvironment)
    {
        super(jiraEnvironment);
    }

    /**
     * Returns the root of REST API provided by the applinks plugin.
     *
     * @return see above
     */
    @Nonnull
    public WebResource rootResource()
    {
        return createResourceForPath("applinks", "1.0");
    }
}
