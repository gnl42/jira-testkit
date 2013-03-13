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
import com.atlassian.jira.rest.api.issue.IssueUpdateRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * Client for the transitions sub-resource.
 *
 * @since v4.3
 */
public class TransitionsClient extends RestApiClient<TransitionsClient>
{
    /**
     * Constructs a new TransitionsClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public TransitionsClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the transitions sub-resource for the issue with the given key.
     *
     * @param issueKey a String containing an issue key
     * @return a Transitions
     * @throws UniformInterfaceException if there's a problem
     */
    public IssueTransitionsMeta get(String issueKey) throws UniformInterfaceException
    {
        return transitionsForIssueWithKey(issueKey).get(IssueTransitionsMeta.class);
    }

    public Response postResponse(final String issueKey, final IssueUpdateRequest issueUpdateRequest)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return transitionsForIssueWithKey(issueKey).type(APPLICATION_JSON_TYPE).post(ClientResponse.class, issueUpdateRequest);
            }
        });
    }

    /**
     * Returns a WebResource for the transitions of the issue with the given key.
     *
     * @param issueKey a String containing an issue key
     * @return a WebResource
     */
    private WebResource transitionsForIssueWithKey(String issueKey)
    {
        return createResource().path("issue").path(issueKey).path("transitions").queryParam("expand", "transitions.fields");
    }
}
