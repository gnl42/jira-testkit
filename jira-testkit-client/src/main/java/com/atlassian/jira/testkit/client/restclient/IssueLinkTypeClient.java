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

import javax.ws.rs.core.MediaType;

/**
 *
 * @since v4.3
 */
public class IssueLinkTypeClient extends RestApiClient<IssueLinkTypeClient>
{
   public IssueLinkTypeClient(JIRAEnvironmentData environmentData)
   {
       super(environmentData);
   }

   public IssueLinkTypeClient(JIRAEnvironmentData environmentData, String version)
   {
       super(environmentData, version);
   }

    public IssueLinkTypes getIssueLinkTypes()
    {
        return issueLinkType().get(IssueLinkTypes.class);
    }

    private WebResource issueLinkType()
    {
        return createResource().path("issueLinkType");
    }

    public IssueLinkType getIssueLinkType(String issueLinkTypeID)
    {
        return issueLinkTypeID(issueLinkTypeID).get(IssueLinkType.class);
    }

    public Response deleteIssueLinkType(final String issueLinkTypeID)
    {
        return toResponse(new Method()
        {

            @Override
            public ClientResponse call() {
                return issueLinkTypeID(issueLinkTypeID).delete(ClientResponse.class);
            }
        });
    }

    public IssueLinkType createIssueLinkType(final String name, final String inbound, final String outbound)
    {
        final IssueLinkType linkType = new IssueLinkType();
        linkType.inward = inbound;
        linkType.outward = outbound;
        linkType.name = name;

        return issueLinkType().type(MediaType.APPLICATION_JSON_TYPE).post(IssueLinkType.class, linkType);
    }

    private WebResource issueLinkTypeID(String issueLinkTypeID)
    {
        return createResource().path("issueLinkType/" + issueLinkTypeID);
    }

    public Response getResponseForLinkType(final String issueLinkTypeID)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return issueLinkTypeID(issueLinkTypeID).get(ClientResponse.class);
            }
        });
    }

    public Response getResponseForAllLinkTypes()
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return issueLinkType().get(ClientResponse.class);
            }
        });
    }
}
