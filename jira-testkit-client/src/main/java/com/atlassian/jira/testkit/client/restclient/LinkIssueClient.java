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

import javax.ws.rs.core.MediaType;

/**
 * Client for LinkIssueResource
 *
 * @since v4.3
 */
public class LinkIssueClient extends RestApiClient<LinkIssueClient>
{

   public LinkIssueClient(JIRAEnvironmentData environmentData)
   {
       super(environmentData);
   }

   public LinkIssueClient(JIRAEnvironmentData environmentData, String version)
   {
       super(environmentData, version);
   }

    /**
     * Links the two issues specified in the LinkRequest using the specified
     * link type.
     *
     * @param linkRequest contains all information that is required two link two issues
     * 
     * @return the response two determine if the two issues are successfully linked to each other.
     */
    public Response linkIssues(final LinkRequest linkRequest)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issueLink").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, linkRequest);
            }
        });
    }

    public IssueLink getIssueLink(String id)
    {
        return createResource().path("issueLink").path(id).type(MediaType.APPLICATION_JSON_TYPE).get(IssueLink.class);
    }

    public Response getIssueLinkResponse(final String id)
    {
       return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issueLink").path(id).type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
            }
        });
    }

    public Response deleteIssueLink(final String id)
    {
       return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issueLink").path(id).type(MediaType.APPLICATION_JSON_TYPE).delete(ClientResponse.class);
            }
        });
    }


}
