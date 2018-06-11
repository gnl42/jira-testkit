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
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.MediaType;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Client for the Filter resource.
 *
 * @since v5.0
 */
public class FilterClient extends RestApiClient<FilterClient>
{
    /**
     * Constructs a new FilterClient for a JIRA instance.
     *
     * @param environmentData the JIRA environment data
     */
    public FilterClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the filter with the given ID.
     *
     * @param filterId a String containing a filter id
     * @param expand the properties to expand
     * @return a Filter
     * @throws UniformInterfaceException if there is a problem getting the filter
     */
    public Filter get(String filterId, Filter.Expand... expand) throws UniformInterfaceException
    {
        return filterWithId(filterId, expand).get(Filter.class);
    }

    public List<Filter> getFavouriteFilters(Filter.Expand... expand)
    {
        return addExpands(createResource().path("filter").path("favourite"), expand).get(Filter.FILTER_TYPE);
    }

    /**
     * GETs the filter with the given ID, and returns a Response.
     *
     * @param filterId a String containing a filter ID
     * @return a Response
     */
    public Response getResponse(final String filterId)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return filterWithId(filterId).get(ClientResponse.class);
            }
        });
    }

    /**
     * Creates a WebResource.Builder for the filter resource.
     *
     * @return a WebResource.Builder
     */
    private WebResource filterResource()
    {
        return createResource().path("filter");
    }

    private WebResource addExpands(WebResource resource, Filter.Expand... expand)
    {
        EnumSet<Filter.Expand> expands = setOf(Filter.Expand.class, expand);
        if (expands.isEmpty())
        {
            return resource;
        }
        return resource.queryParam("expand", percentEncode(StringUtils.join(expands, ",")));
    }

    /**
     * Returns a WebResource for the filter with the given ID.
     *
     * @param filterId a String containing a filter ID
     * @param expand any expands
     * @return a WebResource
     */
    protected WebResource filterWithId(String filterId, Filter.Expand... expand)
    {
        return addExpands(createResource().path("filter").path(filterId), expand);
    }

    /**
     * Creates a filter using a POST, and returns a Response. The JQL query and other parameters are passed in the JSON
     * payload.
     *
     * @param filter a Filter object
     * @param expand any expands
     * @return a Response
     */
    public Response<Filter> postFilterResponse(final Filter filter, final Filter.Expand... expand)
    {
        return toResponse(new Method()
        {
            public ClientResponse call()
            {
                return filterResourceForPost(expand).post(ClientResponse.class, filter);
            }
        }, Filter.class);
    }

    /**
     * Updates a filter using a PUT, and returns a Response. The JQL query and other parameters are passed in the JSON
     * payload.
     *
     * @param filter a Filter object
     * @param expand any expands
     * @return a Response
     */
    public Response<Filter> putFilterResponse(final Filter filter, final Filter.Expand... expand)
    {
        return toResponse(new Method() { public ClientResponse call()
            {
                return filterResourceForPut(filter.id, expand).put(ClientResponse.class, filter);
            }
        }, Filter.class);
    }


    public Response<List<Filter.FilterPermission>> getFilterPermissions(String filterId)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return filterWithId(filterId).path("permission").get(ClientResponse.class);
            }
        }, new GenericType<List<Filter.FilterPermission>>(){});
    }

    public Response<Filter.FilterPermission> getFilterPermission(String filterId, String filterPermissionId)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return filterWithId(filterId).path("permission").path(filterPermissionId).get(ClientResponse.class);
            }
        }, Filter.FilterPermission.class);
    }

    public Response deleteFilterPermission(String filterId, String filterPermissionId)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return filterWithId(filterId).path("permission").path(filterPermissionId).delete(ClientResponse.class);
            }
        });
    }

    public Response<List<Filter.FilterPermission>> postFilterPermission(String filterId, FilterPermissionInputBean inputBean)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return filterWithId(filterId).path("permission").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, inputBean);
            }
        }, new GenericType<List<Filter.FilterPermission>>(){});
    }

    public static class FilterPermissionInputBean
    {
        public String type;
        public String projectId;
        public String projectRoleId;
        public String groupname;
        public String userKey;
        public boolean view;
        public boolean edit;

        public FilterPermissionInputBean(String type, String projectId, String projectRoleId, String groupname) {
            this.type = type;
            this.projectId = projectId;
            this.projectRoleId = projectRoleId;
            this.groupname = groupname;
        }

        public FilterPermissionInputBean(String type, String projectId, String projectRoleId, String groupname, String userKey, boolean view, boolean edit) {
            this(type, projectId, projectRoleId, groupname);
            this.userKey = userKey;
            this.view = view;
            this.edit = edit;
        }
    }

    /**
     * Returns a WebResource.Builder that can be used to PUT a search.
     *
     * @param filterId the filter ID
     * @param expand any expands
     * @return see above
     */
    private WebResource.Builder filterResourceForPut(String filterId, Filter.Expand... expand)
    {
        return addExpands(filterResource().path(filterId), expand).type(MediaType.APPLICATION_JSON_TYPE);
    }


    /**
     * Returns a WebResource.Builder that can be used to POST a search.
     *
     * @param expand any expands
     * @return a WebResource.Builder
     */
    private WebResource.Builder filterResourceForPost(Filter.Expand... expand)
    {
        return addExpands(filterResource(), expand).type(MediaType.APPLICATION_JSON_TYPE);
    }

    public Map<String,String> getDefaultShareScope()
    {
        return createResource().path("filter").path("defaultShareScope").get(new GenericType<Map<String, String>>(){});
    }

    public Map<String,String> setDefaultShareScope(Map<String,String> scope)
    {
        return createResource().path("filter").path("defaultShareScope")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(new GenericType<Map<String, String>>(){}, scope);
    }
}
