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

import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Client for the work log resource.
 *
 * @since v4.3
 */
public class WorklogClient extends RestApiClient<WorklogClient>
{
    /**
     * Constructs a new WorklogClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public WorklogClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the work log with the given id, returning a Response object.
     *
     * @param issueKey the issue key
     * @return a Response
     */
    public WorklogWithPaginationBean getAll(final String issueKey)
    {
        return createResource().path("issue").path(issueKey).path("worklog").get(WorklogWithPaginationBean.class);
    }

    /**
     * GETs the work log with the given id.
     *
     * @param issueKey the issue key
     * @param worklogID a String containing the work log id
     * @return a Worklog
     * @throws UniformInterfaceException if there is a problem getting the work log
     */
    public Worklog get(String issueKey, String worklogID) throws UniformInterfaceException
    {
        return worklogWithID(issueKey, worklogID).get(Worklog.class);
    }

    /**
     * GETs the work log with the given id, returning a Response object.
     *
     * @param issueKey the issue key
     * @param worklogID a String containing the work log id
     * @return a Response
     */
    public Response getResponse(final String issueKey, final String worklogID)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return worklogWithID(issueKey, worklogID).get(ClientResponse.class);
            }
        });
    }

    /**
     * Returns a WebResource for the work log with the given id.
     *
     * @param issueKey worklog is associated with
     * @param worklogID a String containing the work log id
     * @return a WebResource
     */
    protected WebResource worklogWithID(String issueKey, String worklogID)
    {
        return createResource().path("issue").path(issueKey).path("worklog").path(worklogID);
    }

    public Response<Worklog> put(final String issueKey, final Worklog worklog)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issue").path(issueKey).path("worklog").path(worklog.id).type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, worklog);
            }
        }, Worklog.class);

    }

    public Response<Worklog> put(final String issueKey, final Worklog worklog, final Map<String, String> queryParams)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                WebResource path = createResource().path("issue").path(issueKey).path("worklog").path(worklog.id);
                for (Map.Entry<String, String> entry : queryParams.entrySet())
                {
                    path = path.queryParam(entry.getKey(), entry.getValue());
                }
                return path.type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, worklog);
            }
        }, Worklog.class);
    }

    public Response<Worklog> post(final String issueKey, final Worklog worklog)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issue").path(issueKey).path("worklog").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, worklog);
            }
        }, Worklog.class);
    }

    public Response<Worklog> post(final String issueKey, final Worklog worklog, final Map<String, String> queryParams)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                    WebResource path = createResource().path("issue").path(issueKey).path("worklog");
                    for (Map.Entry<String, String> entry : queryParams.entrySet())
                    {
                       path = path.queryParam(entry.getKey(), entry.getValue());
                    }
                   return path.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, worklog);
            }
        }, Worklog.class);
    }


    public Response delete(final String issueKey, final Worklog worklog)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issue").path(issueKey).path("worklog").path(worklog.id).type(MediaType.APPLICATION_JSON_TYPE).delete(ClientResponse.class);
            }
        });
    }

    public Response delete(final String issueKey, final Worklog worklog, final Map<String, String> queryParams)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                WebResource path = createResource().path("issue").path(issueKey).path("worklog").path(worklog.id);
                for (Map.Entry<String, String> entry : queryParams.entrySet())
                {
                    path = path.queryParam(entry.getKey(), entry.getValue());
                }
                return path.type(MediaType.APPLICATION_JSON_TYPE).delete(ClientResponse.class);
            }
        });
    }

    public Response<WorklogSincePage> getUpdatedWorklogsSince(Long since)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("worklog").path("updated").queryParam("since", String.valueOf(since)).get(ClientResponse.class);
            }
        }, WorklogSincePage.class);
    }
}
