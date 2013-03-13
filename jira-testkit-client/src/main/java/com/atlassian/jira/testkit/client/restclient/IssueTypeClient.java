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
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import java.util.List;

/**
 * Client for issue type.
 *
 * @since v4.3
 */
public class IssueTypeClient extends RestApiClient<IssueTypeClient>
{
    /**
     * Constructs a new IssueTypeClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public IssueTypeClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the issue type with the given id.
     *
     * @return an IssueType
     * @throws UniformInterfaceException if there is a problem getting the issue type
     */
    public List<IssueType> get() throws UniformInterfaceException
    {
        return issueTypes().get(new GenericType<List<IssueType>>(){});
    }

    /**
     * GETs the issue type with the given id.
     *
     * @param issueTypeID a String containing the issue type id
     * @return an IssueType
     * @throws UniformInterfaceException if there is a problem getting the issue type
     */
    public IssueType get(String issueTypeID) throws UniformInterfaceException
    {
        return issueTypeWithID(issueTypeID).get(IssueType.class);
    }

    /**
     * GETs the issue type with the given id, returning a Response.
     *
     * @param issueTypeID a String containing the issue type id
     * @return a Response
     */
    public Response getResponse(final String issueTypeID)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return issueTypeWithID(issueTypeID).get(ClientResponse.class);
            }
        });
    }

    /**
     * Creates a WebResource for all issue types.
     *
     * @return a WebResource
     */
    private WebResource issueTypes()
    {
        return createResource().path("issuetype");
    }

    /**
     * Creates a WebResource for the issue type with the given id.
     *
     * @param issueTypeID a String containing the issue type id
     * @return a WebResource
     */
    private WebResource issueTypeWithID(String issueTypeID)
    {
        return createResource().path("issuetype").path(issueTypeID);
    }
}
