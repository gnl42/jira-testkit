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
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javax.ws.rs.core.MediaType;

/**
 * Client for the user resource.
 *
 * @since v4.3
 */
public class UserClient extends RestApiClient<UserClient>
{
    /**
     * Constructs a new UserClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public UserClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the user with the given username.
     *
     * @param username a String containing the username
     * @param expand a set of attributes to expand
     * @return a User
     */
    public User get(String username, User.Expand... expand)
    {
        return userWithUsername(username, setOf(User.Expand.class, expand)).get(User.class);
    }

    /**
     * GETs the user with the given key.
     *
     * @param key a String containing the key
     * @param expand a set of attributes to expand
     * @return a User
     */
    public User getByKey(String key, User.Expand... expand)
    {
        return userWithKey(key, setOf(User.Expand.class, expand)).get(User.class);
    }

    public List<User> searchAssignable(String query, String issueKey, String startAt, String maxResults)
    {
        WebResource resource = getSearchAssignableResource(query, issueKey, startAt, maxResults);
        return Arrays.asList(resource.get(User[].class));
    }

    public List<User> multiProjectSearchAssignable(String query, String projectKeys, String startAt, String maxResults)
    {
        WebResource resource = getMultiProjectSearchAssignableResource(query, projectKeys, startAt, maxResults);
        return Arrays.asList(resource.get(User[].class));
    }
    
    public List<User> searchViewableIssue(String query, String issueKey, String startAt, String maxResults)
    {
        WebResource resource = getSearchViewableIssueResource(query, issueKey, startAt, maxResults);
        return Arrays.asList(resource.get(User[].class));
    }

    public List<User> search(String query, String startAt, String maxResults)
    {
        WebResource resource = getSearchResource(query, startAt, maxResults, null, null);
        return Arrays.asList(resource.get(User[].class));
    }

    public List<User> search(String query, String startAt, String maxResults, Boolean includeActive, Boolean includeInactive)
    {
        WebResource resource = getSearchResource(query, startAt, maxResults, includeActive, includeInactive);
        return Arrays.asList(resource.get(User[].class));
    }

    public UserPickerResults picker(String query, String maxResults)
    {
        WebResource resource = getPickerResource(query, maxResults);
        return resource.get(UserPickerResults.class);
    }

    public UserBean createUser(final UserBean userBean)
    {
        return createResource().path("user")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(UserBean.class, userBean);
    }

    public UserBean updateEmail(String userName, String newEmail)
    {
        return updateUser(userName, UserBean.builder().setEmailAddress(newEmail).build());
    }

    public UserBean updateDisplayName(String userName, String displayName)
    {
        return updateUser(userName, UserBean.builder().setDisplayName(displayName).build());
    }

    public UserBean updatePassword(String userName, String password)
    {
        return updateUser(userName, UserBean.builder().setPassword(password).build());
    }

    public UserBean updateName(String userName, String newName)
    {
        return updateUser(userName, UserBean.builder().setName(newName).build());
    }

    public UserBean updateUser(final String userName, final UserBean updateBean)
    {
        return createResource().path("user").queryParam("username", userName)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(UserBean.class, updateBean);
    }

    public void addUserToApplication(final String userName, final String applicationKey)
    {
        applicationAccessResource(userName, applicationKey).post();
    }

    public Response addUserToApplicationResponse(final String username, final String applicationKey)
    {
        return postResponse(applicationAccessResource(username, applicationKey));
    }

    public void removeUserFromApplication(String username, String applicationKey)
    {
        applicationAccessResource(username, applicationKey).delete();
    }

    public Response removeUserFromApplicationResponse(final String username, final String applicationKey)
    {
        return deleteResponse(applicationAccessResource(username, applicationKey));
    }

    public WebResource applicationAccessResource(final String username, final String applicationKey)
    {
        return createResource().path("user").path("application").queryParam("username", username).queryParam("applicationKey", applicationKey);
    }

    public WebResource getSearchAssignableResource(String query, String issueKey, String startAt, String maxResults)
    {
        WebResource resource = applyPagingParams(query, startAt, maxResults, createResource().path("user").path("assignable").path("search"));
        resource = resource.queryParam("issueKey", issueKey);
        return resource;
    }

    public WebResource getMultiProjectSearchAssignableResource(String query, String projectKeys, String startAt, String maxResults)
    {
        WebResource resource = applyPagingParams(query, startAt, maxResults, createResource().path("user").path("assignable").path("multiProjectSearch"));
        resource = resource.queryParam("projectKeys", projectKeys);
        return resource;
    }
    
    public WebResource getSearchViewableIssueResource(String query, String issueKey, String startAt, String maxResults)
    {
        WebResource resource = applyPagingParams(query, startAt, maxResults, createResource().path("user").path("viewissue").path("search"));
        resource = resource.queryParam("issueKey", issueKey);
        return resource;
    }

    public WebResource getSearchResource(String query, String startAt, String maxResults, Boolean includeActive, Boolean includeInactive)
    {
        WebResource resource = applyPagingParams(query, startAt, maxResults, createResource().path("user").path("search"));
        if (includeActive != null)
            resource = resource.queryParam("includeActive", includeActive.toString());
        if (includeInactive != null)
            resource = resource.queryParam("includeInactive", includeInactive.toString());
        return resource;
    }

    public WebResource getPickerResource(String query, String maxResults)
    {
        WebResource resource = createResource().path("user").path("picker");
        if (StringUtils.isNotBlank(query))
        {
            resource = resource.queryParam("query", query);
        }
        if (StringUtils.isNotBlank(maxResults))
        {
            resource = resource.queryParam("maxResults", maxResults);
        }
        return resource;
    }

    private WebResource applyPagingParams(String query, String startAt, String maxResults, WebResource resource)
    {
        resource = resource.queryParam("username", query);
        if (StringUtils.isNotBlank(startAt))
        {
            resource = resource.queryParam("startAt", startAt);
        }
        if (StringUtils.isNotBlank(maxResults))
        {
            resource = resource.queryParam("maxResults", maxResults);
        }
        return resource;
    }

    /**
     * GETs the user with the given username, returning a Response object.
     *
     * @param username a String containing the username
     * @return a Response
     */
    public Response getUserResponse(final String username)
    {
        return getResponse(userWithUsername(username, setOf(User.Expand.class)));
    }

    /**
     * GETs the user with the given key, returning a Response object.
     *
     * @param key a String containing the key
     * @return a Response
     */
    public Response getUserResponseByKey(final String key)
    {
        return getResponse(userWithKey(key, setOf(User.Expand.class)));
    }
    
    public Response getResponse(final WebResource resource)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return resource.get(ClientResponse.class);
            }
        });
    }

    protected Response postResponse(final WebResource resource)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return resource.post(ClientResponse.class);
            }
        });
    }

    protected Response deleteResponse(final WebResource resource)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return resource.delete(ClientResponse.class);
            }
        });
    }

    /**
     * Returns a WebResource for the user with the given username.
     *
     * @param username a String containing the username
     * @param expands an EnumSet indicating what attributes to expand
     * @return a WebResource
     */
    private WebResource userWithUsername(String username, EnumSet<User.Expand> expands)
    {
        WebResource result = createResource().path("user");
        if (username != null)
        {
            result = result.queryParam("username", percentEncode(username));
        }

        return expanded(result, expands);
    }

    /**
     * Returns a WebResource for the user with the given key.
     *
     * @param key a String containing the key
     * @param expands an EnumSet indicating what attributes to expand
     * @return a WebResource
     */
    private WebResource userWithKey(String key, EnumSet<User.Expand> expands)
    {
        WebResource result = createResource().path("user");
        if (key != null)
        {
            result = result.queryParam("key", percentEncode(key));
        }

        return expanded(result, expands);
    }
}
