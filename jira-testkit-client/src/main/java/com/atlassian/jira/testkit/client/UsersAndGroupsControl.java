/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import java.util.Arrays;
import java.util.List;

import com.atlassian.jira.testkit.beans.DirectoryDTO;
import com.atlassian.jira.testkit.beans.LoginInfoBean;
import com.atlassian.jira.testkit.beans.UserDTO;

import com.sun.jersey.api.client.WebResource;

import javax.annotation.Nullable;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Users and
 * Groups.
 *
 * See <code>com.atlassian.jira.testkit.plugin.UsersAndGroupsBackdoor</code> in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
@SuppressWarnings("unused")
public class UsersAndGroupsControl extends BackdoorControl<UsersAndGroupsControl>
{
    public UsersAndGroupsControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public UsersAndGroupsControl addUser(String username)
    {
        addUser(username, username, username, username + "@example.com");
        return this;
    }

    public UsersAndGroupsControl addUser(String username, String password, String displayName, String email) {
        addUser(username, password, displayName, email, false);
        return this;
    }

    public UsersAndGroupsControl addUser(String username, String password, String displayName, String email, boolean sendEmail)
    {
        get(createResource().path("user").path("add")
                .queryParam("userName", username)
                .queryParam("password", password)
                .queryParam("displayName", displayName)
                .queryParam("email", email)
                .queryParam("sendEmail", "" + sendEmail)
        );
        return this;
    }

    public UsersAndGroupsControl addUserEvenIfUserExists(String username)
    {
        addUserEvenIfUserExists(username, username, username, username + "@example.com");
        return this;
    }

    public UsersAndGroupsControl addUserEvenIfUserExists(String username, String password, String displayName, String email) {
        addUserEvenIfUserExists(username, password, displayName, email, false);
        return this;
    }

    public UsersAndGroupsControl addUserEvenIfUserExists(String username, String password, String displayName, String email, boolean sendEmail)
    {
        get(createResource().path("user").path("addEvenIfUserExists")
                .queryParam("userName", username)
                .queryParam("password", password)
                .queryParam("displayName", displayName)
                .queryParam("email", email)
                .queryParam("sendEmail", "" + sendEmail)
        );
        return this;
    }

    /**
     * Makes a lot of new users, fast, and adds them to jira-users.
     *
     * The created user will have a password matching their username and their email address will be:
     *
     *   {usernamePrefix} + index + "@example.com"
     *
     * @param usernamePrefix prefix before each new username, e.g. "testuser" becomes "testuser0"
     * @param displayNamePrefix prefix before each new username, e.g. "Test User " becomes "Test User 0"
     * @param numberOfNewUsers number of users to add
     * @return this control
     */
    public UsersAndGroupsControl addUsers(String usernamePrefix, String displayNamePrefix, int numberOfNewUsers)
    {
        get(createResource().path("user").path("addMany")
                .queryParam("usernamePrefix", usernamePrefix)
                .queryParam("displayNamePrefix", displayNamePrefix)
                .queryParam("numberOfNewUsers", "" + numberOfNewUsers)
        );
        return this;
    }

    /**
     * Makes a lot of new users, fast, and adds them to the group specified (as well as jira-users).
     *
     * The created user will have a password matching their username and their email address will be:
     *
     *   {usernamePrefix} + index + "@example.com"
     *
     * @param usernamePrefix prefix before each new username, e.g. "testuser" becomes "testuser0"
     * @param displayNamePrefix prefix before each new username, e.g. "Test User " becomes "Test User 0"
     * @param numberOfNewUsers number of users to add
     * @param groupName name of group to add all users to
     */
    public void addUsersWithGroup(String usernamePrefix, String displayNamePrefix, int numberOfNewUsers, String groupName)
    {
        get(createResource().path("user").path("addMany")
                .queryParam("usernamePrefix", usernamePrefix)
                .queryParam("displayNamePrefix", displayNamePrefix)
                .queryParam("numberOfNewUsers", "" + numberOfNewUsers)
                .queryParam("groupName", groupName)
        );
    }

    public void deleteUser(String username)
    {
        get(createResource().path("user").path("delete").queryParam("userName", username));
    }

    public boolean userExists(String username)
    {
        return createResource().path("user").path("exists").queryParam("userName", username).get(Boolean.class);
    }

    public boolean isUserInGroup(String username, String groupName)
    {
        return createResource().path("group").path("includes").queryParam("userName", username).queryParam("groupName", groupName).get(Boolean.class);
    }

    public void addUserToGroup(String username, String groupName)
    {
        get(createResource().path("user").path("addToGroup").queryParam("userName", username).queryParam("groupName", groupName));
    }

    public void removeUserFromGroup(String username, String groupName)
    {
        get(createResource().path("user").path("removeFromGroup").queryParam("userName", username).queryParam("groupName", groupName));
    }

    public void addGroup(String groupName)
    {
        get(createResource().path("group").path("add").queryParam("groupName", groupName));
    }

    public boolean groupExists(String groupName)
    {
        return Boolean.parseBoolean(get(createResource().path("group").path("exists").queryParam("groupName", groupName)));
    }
    
    public UsersAndGroupsControl deleteGroup(String groupName)
    {
        get(createResource().path("group").path("delete").queryParam("groupName", groupName));
        return this;
    }

    public void addGroupToGroup(String childGroupName, String parentGroupName)
    {
        get(createResource().path("group").path("addToGroup").queryParam("groupName", childGroupName).queryParam("parentGroupName", parentGroupName));
    }

    /**
     * Makes a lot of new groups, fast, and optionally adds them to a parent group.
     * Also allows creation of a number of new users for each created group.
     *
     * Any created users have auto-generated names of the form [groupName]-user[index], e.g. testgroup-user0.
     *
     * @param groupNamePrefix prefix before each new group name, e.g. "testgroup" becomes "testgroup0"
     * @param numberOfNewGroups number of users to add
     * @param parentGroupName the parent group name (requires nested groups to be enabled)
     * @param numberOfNewUsersPerGroup number of new users to create and add to each group
     * @return this control
     */
    public UsersAndGroupsControl addGroups(String groupNamePrefix, int numberOfNewGroups, @Nullable String parentGroupName, int numberOfNewUsersPerGroup)
    {
        get(createResource().path("group").path("addMany")
                        .queryParam("groupNamePrefix", groupNamePrefix)
                        .queryParam("numberOfNewGroups", "" + numberOfNewGroups)
                        .queryParam("parentGroupName", parentGroupName)
                        .queryParam("numberOfNewUsersPerGroup", "" + numberOfNewUsersPerGroup)
        );
        return this;
    }

    public UsersAndGroupsControl resetLoginCount(String username)
    {
        get(createResource().path("user").path("resetLoginCount").queryParam("user", username));
        return this;
    }

    public long getNumberOfUsers()
    {
        return Long.parseLong(createResource().path("user").path("count").get(String.class));
    }

    public long getNumberOfGroups()
    {
        return Long.parseLong(createResource().path("group").path("count").get(String.class));
    }

    public LoginInfoBean getLoginInfo(String username)
    {
        return createResource().path("user").path("loginInfo").queryParam("userName", username).get(LoginInfoBean.class);
    }

    public List<UserDTO> getAllUsers()
    {
        return Arrays.asList(createResource().path("user").path("all").get(UserDTO[].class));
    }

    public UserDTO getUserByName(final String username)
    {
        return createResource().path("user").path("byName").queryParam("userName", username).get(UserDTO.class);
    }

    public UserDTO getUserByNameEvenWhenUnknown(final String username)
    {
        return createResource().path("user").path("byNameEvenWhenUnknown").queryParam("userName", username).get(UserDTO.class);
    }

    public void updateUser(final UserDTO user)
    {
        createResource().path("user").path("byName").entity(user, APPLICATION_JSON_TYPE).post();
    }

    public List<DirectoryDTO> getAllDirectories()
    {
        return Arrays.asList(createResource().path("directory").get(DirectoryDTO[].class));
    }

    public DirectoryDTO getDirectory(final long id)
    {
        return createResource().path("directory").path(String.valueOf(id)).get(DirectoryDTO.class);
    }

    @Override
    protected WebResource createResource()
    {
        return super.createResource().path("usersAndGroups");
    }
}
