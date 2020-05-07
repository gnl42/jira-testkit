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

import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

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
     * @param expand   a set of attributes to expand
     * @return a User
     */
    public User get(String username, User.Expand... expand)
    {
        return get(username, false, expand);
    }

    /**
     * GETs the user with the given username.
     *
     * @param username       a String containing the username
     * @param includeDeleted Whether deleted user should be returned (flag is only available to users with administer right). Deleted user exists in jira app_users DB table (has user key and username) but does not exist in crowd (no user data eg. Full name, email etc.).
     * @param expand         a set of attributes to expand
     * @return a User
     *
     * @since Jira 8.10.0
     */
    public User get(String username, boolean includeDeleted, User.Expand... expand)
    {
        return userWithUsername(username, includeDeleted, setOf(User.Expand.class, expand)).get(User.class);
    }

    /**
     * GETs the user with the given key.
     *
     * @param key    a String containing the key
     * @param expand a set of attributes to expand
     * @return a User
     */
    public User getByKey(String key, User.Expand... expand)
    {
        return getByKey(key, false, expand);
    }

    /**
     * GETs the user with the given key.
     *
     * @param key            a String containing the key
     * @param includeDeleted Whether deleted user should be returned (flag is only available to users with administer right). Deleted user exists in jira app_users DB table (has user key and username) but does not exist in crowd (no user data eg. Full name, email etc.).
     * @param expand         a set of attributes to expand
     * @return a User
     *
     * @since Jira 8.10.0
     */
    public User getByKey(String key, boolean includeDeleted, User.Expand... expand)
    {
        return userWithKey(key, includeDeleted, setOf(User.Expand.class, expand)).get(User.class);
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

    public List<User> searchByPermission(String query, String permissions, String issueKey, String projectKey, String startAt, String maxResults)
    {
        WebResource resource = getSearchByPermissionResource(query, permissions, issueKey, projectKey, startAt, maxResults);
        return Arrays.asList(resource.get(User[].class));
    }

    public Response searchByPermissionResponse(String query, String permissions, String issueKey, String projectKey, String startAt, String maxResults)
    {
        WebResource resource = getSearchByPermissionResource(query, permissions, issueKey, projectKey, startAt, maxResults);
        return getResponse(resource);
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

    public WebResource getSearchByPermissionResource(String query, String permissions, String issueKey, String projectKey, String startAt, String maxResults)
    {
        WebResource resource = createResource().path("user").path("permission").path("search");
        resource = StringUtils.isNotBlank(query) ? resource.queryParam("username", query) : resource;
        resource = StringUtils.isNotBlank(issueKey) ? resource.queryParam("issueKey", issueKey) : resource;
        resource = StringUtils.isNotBlank(projectKey) ? resource.queryParam("projectKey", projectKey) : resource;
        resource = StringUtils.isNotBlank(startAt) ? resource.queryParam("startAt", startAt) : resource;
        resource = StringUtils.isNotBlank(maxResults) ? resource.queryParam("maxResults", maxResults) : resource;
        resource = resource.queryParam("permissions", permissions);
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
        return getUserResponse(username, false);
    }

    /**
     * GETs the user with the given username, returning a Response object.
     *
     * @param username       a String containing the username
     * @param includeDeleted Whether deleted user should be returned (flag is only available to users with administer right). Deleted user exists in jira app_users DB table (has user key and username) but does not exist in crowd (no user data eg. Full name, email etc.).
     * @return a Response
     *
     * @since Jira 8.10.0
     */
    public Response getUserResponse(final String username, boolean includeDeleted)
    {
        return getResponse(userWithUsername(username, includeDeleted, setOf(User.Expand.class)));
    }

    /**
     * GETs the user with the given key, returning a Response object.
     *
     * @param key a String containing the key
     * @return a Response
     */
    public Response getUserResponseByKey(final String key)
    {
        return getUserResponseByKey(key, false);
    }

    /**
     * GETs the user with the given key, returning a Response object.
     *
     * @param key            a String containing the key
     * @param includeDeleted Whether deleted user should be returned (flag is only available to users with administer right). Deleted user exists in jira app_users DB table (has user key and username) but does not exist in crowd (no user data eg. Full name, email etc.).
     * @return a Response
     *
     * @since Jira 8.10.0
     */
    public Response getUserResponseByKey(final String key, final boolean includeDeleted)
    {
        return getResponse(userWithKey(key, includeDeleted, setOf(User.Expand.class)));
    }


    public Response getResponse(final WebResource resource)
    {
        return toResponse(() -> resource.get(ClientResponse.class));
    }

    private Response postResponse(final WebResource resource)
    {
        return toResponse(() -> resource.post(ClientResponse.class));
    }

    private Response deleteResponse(final WebResource resource)
    {
        return toResponse(() -> resource.delete(ClientResponse.class));
    }

    /**
     * Returns a WebResource for the user with the given username.
     *
     * @param username a String containing the username
     * @param includeDeleted Whether deleted user should be returned (flag is only available to users with administer right). Deleted user exists in jira app_users DB table (has user key and username) but does not exist in crowd (no user data eg. Full name, email etc.).
     * @param expands  an EnumSet indicating what attributes to expand
     * @return a WebResource
     *
     * @since Jira 8.10.0
     */
    private WebResource userWithUsername(String username, boolean includeDeleted, EnumSet<User.Expand> expands)
    {
        WebResource result = createResource().path("user");
        if (username != null)
        {
            result = result.queryParam("username", percentEncode(username));
        }
        if (includeDeleted) {
            result = result.queryParam("includeDeleted", "true");
        }

        return expanded(result, expands);
    }

    /**
     * Returns a WebResource for the user with the given key.
     *
     * @param key     a String containing the key
     * @param includeDeleted Whether deleted user should be returned (flag is only available to users with administer right). Deleted user exists in jira app_users DB table (has user key and username) but does not exist in crowd (no user data eg. Full name, email etc.).
     * @param expands an EnumSet indicating what attributes to expand
     * @return a WebResource
     *
     * @since Jira 8.10.0
     */
    private WebResource userWithKey(String key, boolean includeDeleted, EnumSet<User.Expand> expands)
    {
        WebResource result = createResource().path("user");
        if (key != null)
        {
            result = result.queryParam("key", percentEncode(key));
        }
        if (includeDeleted) {
            result = result.queryParam("includeDeleted", "true");
        }

        return expanded(result, expands);
    }
}
