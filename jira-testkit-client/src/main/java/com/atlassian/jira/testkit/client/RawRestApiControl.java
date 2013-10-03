package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;

/**
 * Control for using the REST API.
 * @since v6.0.49
 */
public class RawRestApiControl extends BackdoorControl
{

    /**
     * Creates a new BackdoorControl.
     *
     * @param environmentData a JIRAEnvironmentData
     */
    public RawRestApiControl(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);

    }

    /**
     * Creates a root resource pointing at
     * @return the resource pointing at the root of all JIRA REST API.
     */
    public WebResource rootReource()
    {
        return createResourceForPath(BackdoorControl.API_REST_PATH, BackdoorControl.API_REST_VERSION);
    }
}
