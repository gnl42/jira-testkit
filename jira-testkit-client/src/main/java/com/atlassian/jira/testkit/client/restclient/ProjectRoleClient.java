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
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * @since v4.4
 */
public class ProjectRoleClient extends RestApiClient<ProjectRoleClient>
{
    protected final ProjectRoleClient2 projectRoleClient2;

    public GenericType<Map<String, String>> TYPE = new GenericType<Map<String, String>>(HashMap.class);

    public ProjectRoleClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
        projectRoleClient2 = new ProjectRoleClient2(environmentData);
    }

    public Map<String, String> get(String projectKey) throws UniformInterfaceException
    {
        return rolesWithProjectKey(projectKey).get(TYPE);
    }

    public void deleteRole(String name) {
        projectRoleClient2.deleteRole(name);
    }

    public ProjectRole get(String projectKey, String role)
    {
        final WebResource webResource = resourceRoot(get(projectKey).get(role));
        return webResource.get(ProjectRole.class);
    }

    public Response addActors(final String projectKey, final String role, @Nullable final String[] groupNames,
            @Nullable final String[] userNames)
    {
        final ProjectRole projectRole = get(projectKey, role);

        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                final WebResource webResource = rolesWithProjectKey(projectKey).path(projectRole.id.toString());
                final Map<String, String[]> parameter = Maps.newHashMap();
                if(groupNames != null)
                {
                    parameter.put("group", groupNames);
                }
                if(userNames != null)
                {
                    parameter.put("user", userNames);
                }
                return webResource.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, parameter);
            }
        });
    }

    public Response deleteGroup(final String projectKey, final String role, final String groupName)
    {
        final ProjectRole projectRole = get(projectKey, role);

        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                final WebResource webResource = rolesWithProjectKey(projectKey).path(projectRole.id.toString()).queryParam("group", groupName);
                return webResource.delete(ClientResponse.class);
            }
        });
    }

    public Response deleteUser(final String projectKey, final String role, final String userName)
    {
        final ProjectRole projectRole = get(projectKey, role);

        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                final WebResource webResource = rolesWithProjectKey(projectKey).path(projectRole.id.toString()).queryParam("user", userName);
                return webResource.delete(ClientResponse.class);
            }
        });
    }

    protected WebResource rolesWithProjectKey(String projectKey)
    {
        return createResource().path("project").path(projectKey).path("role");
    }

    public Response setActors(final String projectKey, final String role, final Map<String, String[]> actors)
    {
        final ProjectRole projectRole = get(projectKey, role);

        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                final WebResource webResource = rolesWithProjectKey(projectKey).path(projectRole.id.toString());
                final ProjectRoleActorsUpdate projectRoleActorsUpdate = new ProjectRoleActorsUpdate(
                        projectRole.id, actors
                );
                return webResource.type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, projectRoleActorsUpdate);
            }
        });
    }
}
