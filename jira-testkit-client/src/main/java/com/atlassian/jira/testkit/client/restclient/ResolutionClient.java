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
 * Client class for the Resolution resource.
 *
 * @since v4.3
 */
public class ResolutionClient extends RestApiClient<ResolutionClient>
{
    /**
     * Constructs a new ResolutionClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public ResolutionClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs all priorities
     *
     * @return a list of Resolution
     * @throws UniformInterfaceException if anything goes wrong
     */
    public List<Resolution> get() throws UniformInterfaceException
    {
        return resolution().get(new GenericType<List<Resolution>>(){});
    }

    /**
     * GETs the resolution having the given id.
     *
     * @param resolutionID a String containing the resolution id
     * @return a Resolution
     * @throws UniformInterfaceException if there is an error
     */
    public Resolution get(String resolutionID) throws UniformInterfaceException
    {
        return resolutionWithID(resolutionID).get(Resolution.class);
    }

    /**
     * GETs the resolution having the given id, and returns a Reponse.
     *
     * @param resolutionID a String containing the resolution id
     * @return a Response
     */
    public Response getResponse(final String resolutionID)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return resolutionWithID(resolutionID).get(ClientResponse.class);
            }
        });
    }

    /**
     * Creates a WebResource for the resolution having the given id.
     *
     * @param resolutionID a String containing the resolution id
     * @return a WebResource
     */
    private WebResource resolutionWithID(String resolutionID)
    {
        return createResource().path("resolution").path(resolutionID);
    }

    /**
     * Creates a WebResource for the resolutions
     *
     * @return a WebResource
     */
    private WebResource resolution()
    {
        return createResource().path("resolution");
    }
}
