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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;

/**
 * Client class for the Project resource.
 *
 * @since v4.3
 */
public class ProjectClient extends RestApiClient<ProjectClient>
{

    public static class UpdateBean
    {
        private final Map<String, String> json;

        private UpdateBean(Map<ProjectUpdateField, String> fieldsToUpdate)
        {
            ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
            for (ProjectUpdateField field : fieldsToUpdate.keySet())
            {
                mapBuilder.put(field.jsonFieldName(), fieldsToUpdate.get(field));
            }
            json = mapBuilder.build();
        }

        public Map<String, String> getJson()
        {
            return Maps.newHashMap(json);
        }

        public static UpdateBeanBuilder builder()
        {
            return new UpdateBeanBuilder(Collections.<ProjectUpdateField, String>emptyMap());
        }

        public static class UpdateBeanBuilder
        {
            private final Map<ProjectUpdateField, String> builder;

            public UpdateBeanBuilder(Map<ProjectUpdateField, String> builder)
            {
                this.builder = builder;
            }

            public UpdateBeanBuilder with(ProjectUpdateField field, Object value)
            {
                Map<ProjectUpdateField, String> newMap = ImmutableMap.<ProjectUpdateField, String>builder().putAll(builder).put(field, value.toString()).build();
                return new UpdateBeanBuilder(newMap);
            }

            public UpdateBean build()
            {
                return new UpdateBean(builder);
            }
        }
    }

    /**
     * Constructs a new ProjectClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public ProjectClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the project having the given key.
     *
     * @param projectKey a String containing the project key
     * @return a Project
     * @throws com.sun.jersey.api.client.UniformInterfaceException if there is a problem
     */
    public Project get(String projectKey) throws UniformInterfaceException
    {
        return projectWithKey(projectKey).get(Project.class);
    }

    /**
     * GETs the project having the given key, with all fields expanded
     *
     * @param projectKey a String containing the project key
     * @return a Project
     * @throws com.sun.jersey.api.client.UniformInterfaceException if there is a problem
     */
    public Project getAndExpandAll(String projectKey) throws UniformInterfaceException
    {
        return projectWithKey(projectKey).queryParam("expand", "description,lead,url,projectKeys").get(Project.class);
    }

    /**
     * Creates a specified project
     *
     * @param project a Java class that will be JSON-ized and send to the server
     * @return a {@link com.sun.jersey.api.client.ClientResponse} object
     */
    public ClientResponse create(Object project)
    {
        return registerResponse(projects().type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, project));
    }

    /**
     * Updates a specified project
     *
     * @param keyOrId key or id of project to update
     * @param newData object containing new values for fields
     * @return a {@link com.sun.jersey.api.client.ClientResponse} object
     */
    public ClientResponse update(String keyOrId, UpdateBean newData)
    {
        return registerResponse(projectWithKey(keyOrId).type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, newData.json));
    }

    /**
     * Deletes a specified project
     *
     * @param keyOrId key or id of project to delete
     * @return a {@link com.sun.jersey.api.client.ClientResponse} object
     */
    public ClientResponse delete(String keyOrId)
    {
        return registerResponse(projectWithKey(keyOrId).type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).delete(ClientResponse.class));
    }

    /**
     * GETs a list of projects, which are visible to the current user.
     *
     * @return a list of projects.
     */
    public List<Project> getProjects()
    {
        return projects().get(Project.PROJECTS_TYPE);
    }

    /**
     * GETs a list of projects, which are visible to the current user, possibly expanding one or more fields.
     *
     * @param expand a comma separated list of fields to expand.
     * @return a list of projects.
     * @since 7.0
     */
    public List<Project> getProjects(String expand)
    {
        return projects(expand).get(Project.PROJECTS_TYPE);
    }

    /**
     * GETs a list of current user recent projects, possibly expanding one or more fields.
     *
     * @param expand a comma separated list of fields to expand.
     * @return a list of projects.
     * @since 7.0
     */
    public ClientResponse getRecentProjects(String expand, int count)
    {
        return projects(expand).queryParam("recent", String.valueOf(count)).get(ClientResponse.class);
    }

    /**
     * GETs a list of current user recent projects.
     *
     * @return a list of projects.
     * @since 7.0
     */
    public ClientResponse getRecentProjects(int count)
    {
        return registerResponse(projects().queryParam("recent", String.valueOf(count)).get(ClientResponse.class));
    }

    /**
     * GETs a list of versions, associated with the passed project.
     *
     * @param key the key of the project to query.
     * @return a list of versions.
     */
    public List<Version> getVersions(String key)
    {
        return projectVersionWithKey(key).get(Version.VERSIONS_TYPE);
    }

    /**
     * GETs a list of versions, associated with the passed project.
     *
     * @param key the key of the project to query.
     * @return a list of versions.
     */
    public PageBean<Version> getVersionsPaged(String key, final Long startAt, final Integer maxResults, final String orderBy)
    {
        WebResource webResource = projectVersionsPaged(key);
        if (startAt != null)
        {
            webResource = webResource.queryParam("startAt", startAt.toString());
        }
        if (maxResults != null)
        {
            webResource = webResource.queryParam("maxResults", maxResults.toString());
        }
        if (orderBy != null)
        {
            webResource = webResource.queryParam("orderBy", orderBy);
        }

        return webResource.get(Version.VERSIONS_PAGED_TYPE);
    }

    /**
     * GETs a map of avatars, associated with the passed project.
     *
     * @param key the key of the project to query.
     * @return a map of avatars, system and custom
     */
    public Map<String, List<Avatar>> getAvatars(String key)
    {
        return projectWithKey(key).path("avatars").get(Avatar.ALLAVATARS_TYPE);
    }

    /**
     * GETs a single avatar, associated with the passed project
     *
     * @param key the key of the project to query
     * @param id  the database id of the avatar
     * @return avatar
     */
    public Avatar getAvatar(String key, Long id)
    {
        return projectWithKey(key).path("avatar").path(id.toString())
                .get(Avatar.AVATAR_TYPE);
    }

    /**
     * GETs the project having the given key, and returns a Response.
     *
     * @param projectKey a String containing the project key
     * @return a Response
     */
    public Response getResponse(final String projectKey)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return projectWithKey(projectKey).get(ClientResponse.class);
            }
        });
    }

    /**
     * GETs the versions associated with the passed project.
     *
     * @param projectKey a String containing the project key
     * @return a Response
     */
    public Response getVersionsResponse(final String projectKey)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return projectWithKey(projectKey).get(ClientResponse.class);
            }
        });
    }

    /**
     * GETs a list of components, associated with the passed project.
     *
     * @param key the key of the project to query.
     * @return a list of components.
     */
    public List<Component> getComponents(String key)
    {
        return projectComponentWithKey(key).get(Component.COMPONENTS_TYPE);
    }

    /**
     * Returns a WebResource for the project having the given key.
     *
     * @param projectKey a String containing the project key
     * @return a Response
     */
    protected WebResource projectWithKey(String projectKey)
    {
        return projects().path(projectKey);
    }

    /**
     * Returns a WebResource for the project versions 
     *
     * @param projectKey a String containing the project key
     * @return a Response
     */
    protected WebResource projectVersionWithKey(String projectKey)
    {
        return projectWithKey(projectKey).path("versions");
    }

    /**
     * Returns a WebResource for the versions gi
     *
     * @param projectKey a String containing the project key
     * @return a Response
     */
    protected WebResource projectVersionsPaged(String projectKey)
    {
        return projectWithKey(projectKey).path("version");
    }

    /**
     * Returns a WebResource for the versions gi
     *
     * @param projectKey a String containing the project key
     * @return a Response
     */
    protected WebResource projectComponentWithKey(String projectKey)
    {
        return projectWithKey(projectKey).path("components");
    }

    /**
     * Returns a WebResource for the projects visible to the current user.
     *
     * @return a Response
     */
    protected WebResource projects()
    {
        return createResource().path("project");
    }

    /**
     * Returns a WebResource for the projects visible to the current user, optionally expanding fields.
     *
     * @param expand a comma separated list of fields to expand in the response
     * @return a Response
     */
    protected WebResource projects(String expand)
    {
        return projects().queryParam("expand", expand);
    }
}
