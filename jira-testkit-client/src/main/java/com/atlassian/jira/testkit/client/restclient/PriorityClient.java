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
 * TODO: Document this class / interface here
 *
 * @since v4.3
 */
public class PriorityClient extends RestApiClient<PriorityClient>
{
    /**
     * Constructs a new PriorityClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public PriorityClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs all priorities
     *
     * @return a list of Priority
     * @throws UniformInterfaceException if anything goes wrong
     */
    public List<Priority> get() throws UniformInterfaceException
    {
        return priority().get(new GenericType<List<Priority>>(){});
    }

    /**
     * GETs the priority with the given ID.
     *
     * @param priorityID a String containing a priority ID
     * @return a Priority
     * @throws UniformInterfaceException if anything goes wrong
     */
    public Priority get(String priorityID) throws UniformInterfaceException
    {
        return priorityWithID(priorityID).get(Priority.class);
    }

    /**
     * GETs the priority with the given ID, returning a Response object.
     *
     * @param priorityID a String containing a priority ID
     * @return a Response
     */
    public Response getResponse(final String priorityID)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return priorityWithID(priorityID).get(ClientResponse.class);
            }
        });
    }

    /**
     * Returns a WebResource for priorities.
     *
     * @return a WebResource
     */
    protected WebResource priority()
    {
        return createResource().path("priority");
    }

    /**
     * Returns a WebResource for the priority having the given ID.
     *
     * @param priorityID a String containing a priority ID
     * @return a WebResource
     */
    protected WebResource priorityWithID(String priorityID)
    {
        return createResource().path("priority").path(priorityID);
    }
}
