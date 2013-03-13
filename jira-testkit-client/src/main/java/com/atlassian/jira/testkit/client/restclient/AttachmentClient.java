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

import java.util.Map;

/**
 * Client for the Attachment resource.
 *
 * @since v4.3
 */
public class AttachmentClient extends RestApiClient<AttachmentClient>
{
    /**
     * Constructs a new AttachmentClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public AttachmentClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the attachment with the given id.
     *
     * @param attachmentID a String containing the attachment id
     * @return an Attachment
     * @throws com.sun.jersey.api.client.UniformInterfaceException if there is a problem
     */
    public Attachment get(String attachmentID) throws UniformInterfaceException
    {
        return attachmentWithID(attachmentID)
                .get(Attachment.class);
    }

    /**
     * GETs the attachment with the given ID, and returns the Response.
     *
     * @param attachmentID a String containing the attachment ID
     * @return a Response
     */
    public Response getResponse(final String attachmentID)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return attachmentWithID(attachmentID).get(ClientResponse.class);
            }
        });
    }

    /**
     * Deletes the attachment with the given ID, and returns the Response.
     *
     * @param attachmentID a String containing the attachment ID
     * @return a Response
     */
    public Response deleteResponse(final String attachmentID)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return attachmentWithID(attachmentID).delete(ClientResponse.class);
            }
        });
    }

    /**
     * Returns the WebResource for the attachment having the given id.
     *
     * @param attachmentID a String containing the attachment id
     * @return a WebResource
     */
    protected WebResource attachmentWithID(String attachmentID)
    {
        return createResource().path("attachment").path(attachmentID);
    }

    /**
     * GETs the global attachment meta
     *
     * @return a Response
     */
    public Map getMeta()
    {
        return attachmentMeta().get(Map.class);
    }

    /**
     * Returns the WebResource for the attachment meta
     *
     * @return a WebResource
     */
    protected WebResource attachmentMeta()
    {
        return createResource().path("attachment").path("meta");
    }

}
