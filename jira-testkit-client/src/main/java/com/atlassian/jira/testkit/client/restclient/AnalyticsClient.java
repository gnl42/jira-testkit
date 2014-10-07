/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import javax.ws.rs.core.MediaType;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Client for the Analytics resource.
 */
public class AnalyticsClient extends RestApiClient<AnalyticsClient>
{
    /**
     * Constructs a new AttachmentClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public AnalyticsClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void acknowledgePolicy() {
        toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("acknowledge")
                        .type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);
            }
        });
    }

    public void disable() {
        toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("enable")
                        .type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, new AnalyticsEnabled(false));
            }
        });
    }


    public void enable() {
        toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("enable")
                        .type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, new AnalyticsEnabled(true));
            }
        });
    }

    @Override
    protected WebResource createResource()
    {
        return resourceRoot(getEnvironmentData().getBaseUrl().toExternalForm()).path("rest").path("analytics").path("1.0").path("config");
    }
}
