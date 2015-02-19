/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.beans.IssueSecurityType;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * Client for issue type.
 *
 * @since v4.3
 */
public class IssueSecurityTypeClient extends RestApiClient<IssueSecurityTypeClient>
{
    /**
     * Constructs a new IssueSecurityTypeClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public IssueSecurityTypeClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the issue type with the given id.
     *
     * @param issueSecurityTypeID a String containing the issue type id
     * @return an IssueSecurityType
     * @throws com.sun.jersey.api.client.UniformInterfaceException if there is a problem getting the issue type
     */
    public IssueSecurityType get(String issueSecurityTypeID) throws UniformInterfaceException
    {
        return issueSecurityTypeWithID(issueSecurityTypeID).get(IssueSecurityType.class);
    }

    /**
     * GETs the issue type with the given id, returning a Response.
     *
     * @param issueSecurityTypeID a String containing the issue type id
     * @return a Response
     */
    public Response getResponse(final String issueSecurityTypeID)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return issueSecurityTypeWithID(issueSecurityTypeID).get(ClientResponse.class);
            }
        });
    }

    /**
     * Creates a WebResource for the issue type with the given id.
     *
     * @param issueSecurityTypeID a String containing the issue type id
     * @return a WebResource
     */
    private WebResource issueSecurityTypeWithID(String issueSecurityTypeID)
    {
        return createResource().path("securitylevel").path(issueSecurityTypeID);
    }
}
