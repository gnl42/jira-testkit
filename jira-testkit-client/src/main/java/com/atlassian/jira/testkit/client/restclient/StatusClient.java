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

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Client for the status resource.
 *
 * @since v4.3
 */
public class StatusClient extends RestApiClient<StatusClient>
{
    /**
     * Constructs a new StatusClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public StatusClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the status with the given id.
     *
     * @param statusID a String containing the status id
     * @return a Status
     * @throws UniformInterfaceException if there's a problem getting the status
     */
    public Status get(String statusID) throws UniformInterfaceException
    {
        return statusWithID(statusID).get(Status.class);
    }

    /**
     * GETs all statuses
     *
     * @return a List of Statuses
     * @throws UniformInterfaceException if there's a problem getting the status
     */
    public List<Status> get() throws UniformInterfaceException
    {
        return status().get(new GenericType<List<Status>>(){});
    }

    /**
     * Move a status
     */
    public void moveStatus(String idOrName, MoveStatus moveStatus)
    {
        statusWithID(idOrName)
                .path("move")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(moveStatus);
    }

    /**
     * GETs the status with the given id, returning a Response object.
     *
     * @param statusID a String containing the status id
     * @return a Response
     */
    public Response getResponse(final String statusID)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return statusWithID(statusID).get(ClientResponse.class);
            }
        });
    }

    /**
     * Returns a WebResource for the all status.
     *
     * @return a WebResource
     */
    protected WebResource status()
    {
        return createResource().path("status");
    }

    /**
     * Returns a WebResource for the status having the given id.
     *
     * @param statusID a String containing the status id
     * @return a WebResource
     */
    protected WebResource statusWithID(String statusID)
    {
        return createResource().path("status").path(statusID);
    }
}
