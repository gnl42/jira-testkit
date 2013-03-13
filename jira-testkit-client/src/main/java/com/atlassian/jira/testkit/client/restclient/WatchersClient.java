/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * Client for the watchers sub-resource.
 *
 * @since v4.3
 */
public class WatchersClient extends RestApiClient<WatchersClient>
{
    /**
     * Constructs a new WatchersClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public WatchersClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the watchers for the issue with the given key.
     *
     * @param issueKey a String containing an issue key
     * @return a Watchers
     * @throws UniformInterfaceException if there's a problem
     */
    public Watches get(String issueKey) throws UniformInterfaceException
    {
        return watchersForIssueWithKey(issueKey).get(Watches.class);
    }


    /**
     * GETs the watchers for the issue with the given key, returning a Response.
     *
     * @param issueKey a String containing an issue key
     * @return a Response
     */
    public Response getResponse(final String issueKey)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return watchersForIssueWithKey(issueKey).get(ClientResponse.class);
            }
        });
    }

    /**
     * POSTs a user name to the watchers sub-resource of the issue with the given key, returning a Response.
     *
     * @param issueKey a String containing an issue key
     * @param username the username to POST
     * @return a Response
     */
    public Response postResponse(final String issueKey, final String username)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                if (username == null)
                {
                    return watchersForIssueWithKey(issueKey).type(APPLICATION_JSON_TYPE).post(ClientResponse.class);
                }

                return watchersForIssueWithKey(issueKey).type(APPLICATION_JSON_TYPE).post(ClientResponse.class, String.format("\"%s\"", username));
            }
        });
    }

    /**
     * DELETEs a user from the watchers sub-resource of the issue with the given key.
     *
     * @param issueKey a String containing an issue key
     * @param username the username to delete from the watcher list
     * @return a Response
     */
    public Response deleteResponse(final String issueKey, final String username)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return watchersForIssueWithKey(issueKey).queryParam("username", username).delete(ClientResponse.class);
            }
        });
    }

    /**
     * Returns a WebResource for the watchers sub-resource of the issue with the given key.
     *
     * @param issueKey a String containing an issue key
     * @return a WebResource
     */
    private WebResource watchersForIssueWithKey(String issueKey)
    {
        return createResource().path("issue").path(issueKey).path("watchers");
    }
}
