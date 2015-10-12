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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * Client for the Analytics resource.
 */
public class AnalyticsClient extends RestApiClient<AnalyticsClient>
{
    public enum ReportMode
    {
        BTF("btf_processed"),
        CLOUD("ondemand_processed"),
        RAW("unprocessed");

        final String alias;

        ReportMode(final String alias)
        {
            this.alias = alias;
        }
    }

    /**
     * Constructs a new AttachmentClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public AnalyticsClient(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void acknowledgePolicy() {
        toResponse(new Method() {
            @Override
            public ClientResponse call() {
                return createResource().path("config").path("acknowledge")
                        .type(APPLICATION_JSON_TYPE).put(ClientResponse.class);
            }
        });
    }

    public void disable() {
        toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("config").path("enable")
                        .type(APPLICATION_JSON_TYPE).put(ClientResponse.class, new AnalyticsEnabled(false));
            }
        });
    }


    public void enable() {
        toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("config").path("enable")
                        .type(APPLICATION_JSON_TYPE).put(ClientResponse.class, new AnalyticsEnabled(true));
            }
        });
    }

    /**
     * Start capturing events for getReport()
     */
    public void startCapturing()
    {
        toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("report")
                        .type(APPLICATION_JSON_TYPE).put(ClientResponse.class, new AnalyticsReportConfig(true));
            }
        });
    }

    /**
     * Stop capturing events for getReport()
     */
    public void stopCapturing()
    {
        toResponse(new Method() {
            @Override
            public ClientResponse call() {
                return createResource().path("report")
                        .type(APPLICATION_JSON_TYPE).put(ClientResponse.class, new AnalyticsReportConfig(false));
            }
        });
    }

    /**
     * Deletes all captured events in getReport()
     */
    public void clearCaptured()
    {
        toResponse(new Method() {
            @Override
            public ClientResponse call() {
                return createResource().path("report").delete(ClientResponse.class);
            }
        });
    }

    /**
     * Get the report of all events raised between startCapturing() and stopCapturing()
     */
    public Response<AnalyticsReportBean> getReport(final ReportMode reportMode)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("report").queryParam("mode", reportMode.alias).get(ClientResponse.class);
            }
        }, AnalyticsReportBean.class);
    }



    @Override
    protected WebResource createResource()
    {
        return resourceRoot(getEnvironmentData().getBaseUrl().toExternalForm()).path("rest").path("analytics").path("1.0");
    }
}
