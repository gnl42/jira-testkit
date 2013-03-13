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
import com.sun.jersey.api.client.WebResource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * Client for the votes sub-resource.
 *
 * @since v4.3
 */
public class VotesClient extends RestApiClient<VotesClient>
{
    /**
     * Constructs a new VotesClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public VotesClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * Gets the voters sub-resource for the issue with the given key.
     *
     * @param issueKey a String containing an issue key
     * @return a Vote
     */
    public Vote get(String issueKey)
    {
        return votersForIssueWithID(issueKey).get(Vote.class);
    }

    /**
     * Gets the voters sub-resource for the issue with the given key, returning a Response object.
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
                return votersForIssueWithID(issueKey).get(ClientResponse.class);
            }
        });
    }

    /**
     * POSTs the users's vote to the issue with the given key, returning a Response object.
     *
     * @param issueKey a String containing an issue key
     * @return a Response
     */
    public Response postResponse(final String issueKey)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return votersForIssueWithID(issueKey).type(APPLICATION_JSON_TYPE).post(ClientResponse.class, "{}");
            }
        });
    }

    /**
     * DELETEs the user's vote from the issue with the given key, returning a Response object.
     *
     * @param issueKey a String containing an issue key
     * @return a Response
     */
    public Response deleteResponse(final String issueKey)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return votersForIssueWithID(issueKey).delete(ClientResponse.class);
            }
        });
    }

    /**
     * Returns a WebResource for the voters sub-resource of the issue having the given key.
     *
     * @param issueKey a String containing an issue key
     * @return a WebResource
     */
    private WebResource votersForIssueWithID(String issueKey)
    {
        return createResource().path("issue").path(issueKey).path("votes");
    }
}
