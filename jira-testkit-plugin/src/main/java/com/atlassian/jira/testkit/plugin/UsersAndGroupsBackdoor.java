/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.exception.InvalidMembershipException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.embedded.InvalidGroupException;
import com.atlassian.crowd.exception.runtime.UserNotFoundException;
import com.atlassian.jira.bc.security.login.LoginInfo;
import com.atlassian.jira.bc.security.login.LoginService;
import com.atlassian.jira.bc.user.UserService;
import com.atlassian.jira.exception.AddException;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.testkit.beans.DirectoryDTO;
import com.atlassian.jira.testkit.beans.LoginInfoBean;
import com.atlassian.jira.testkit.beans.UserDTO;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status;

/**
 * Use this backdoor to manipulate Users and Groups as part of setup for tests
 * NOT specifically testing the Admin UI.
 *
 * This class should only be called by the com.atlassian.jira.testkit.client.UsersAndGroupsControl in jira-testkit-client.
 *
 * @since v5.0
 */
@Path ("usersAndGroups")
public class UsersAndGroupsBackdoor
{
    private static final Logger log = Logger.getLogger(UsersAndGroupsBackdoor.class);
    private static final Response NOT_FOUND = Response.status(Status.NOT_FOUND).build();
    private static final Response OK = Response.ok().build();

    private final CrowdService crowdService;
    private final LoginService loginService;
    private final UserUtil userUtil;
    private final GroupManager groupManager;
    private final UserManager userManager;
    private final CrowdDirectoryService crowdDirectoryService;
    private final UserService userService;

    public UsersAndGroupsBackdoor(final UserUtil userUtil, final CrowdService crowdService,
            final LoginService loginService, final GroupManager groupManager, final UserManager userManager,
            final CrowdDirectoryService crowdDirectoryService, final UserService userService)
    {
        this.crowdService = crowdService;
        this.userUtil = userUtil;
        this.loginService = loginService;
        this.groupManager = groupManager;
        this.userManager = userManager;
        this.crowdDirectoryService = crowdDirectoryService;
        this.userService = userService;
    }

    @GET
    @AnonymousAllowed
    @Path("user/addEvenIfUserExists")
    public Response addEvenIfuserExists(
            @QueryParam ("userName") String username,
            @QueryParam ("password") String password,
            @QueryParam ("email") String email,
            @QueryParam ("displayName") String displayName,
            @QueryParam ("sendEmail") boolean sendEmail)
    {
        if (userUtil.userExists(username))
        {
            deleteUser(username);
        }
        return addUser(username, password, email, displayName, sendEmail);
    }

    @GET
    @AnonymousAllowed
    @Path("user/add")
    public Response addUser(
            @QueryParam ("userName") String username,
            @QueryParam ("password") String password,
            @QueryParam ("email") String email,
            @QueryParam ("displayName") String displayName,
            @QueryParam ("sendEmail") boolean sendEmail)
    {
        try
        {
            doAddUser(username, password, email, displayName, sendEmail);
        }
        catch (PermissionException e)
        {
            log.warn("PermissionException adding user", e);
            throw new RuntimeException(e);
        }
        catch (CreateException e)
        {
            log.warn("CreateException adding user", e);
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

    private void doAddUser(final @QueryParam ("userName") String username, final @QueryParam ("password") String password, final @QueryParam ("email") String email, final @QueryParam ("displayName") String displayName, final @QueryParam ("sendEmail") boolean sendEmail)
            throws PermissionException, CreateException
    {
        UserService.CreateUserRequest createUserRequest = UserService.CreateUserRequest
            .withUserDetails(null, username, password, email, displayName)
            .sendNotification(sendEmail)
            .skipValidation();
        userService.createUser(userService.validateCreateUser(createUserRequest));
    }

    @GET
    @AnonymousAllowed
    @Path("user/delete")
    public Response deleteUser(@QueryParam ("userName") String username)
    {
        ApplicationUser admin = userUtil.getUserByName("admin");     // shouldn't have to pass admin in for permissions at this level...
        ApplicationUser userToRemove = userUtil.getUserByName(username);

        userUtil.removeUser(admin, userToRemove);

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("user/addToGroup")
    public Response userToGroup(
            @QueryParam ("userName") String userName,
            @QueryParam ("groupName") String groupName)
    {
        Group group = crowdService.getGroup(groupName);
        ApplicationUser userToAdd = userUtil.getUserByName(userName);

        try
        {
            userUtil.addUserToGroup(group, userToAdd);
        }
        catch (PermissionException e)
        {
            log.warn("PermissionException adding user to group", e);
            throw new RuntimeException(e);
        }
        catch (AddException e)
        {
            log.warn("PermissionException adding user to group", e);
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("user/removeFromGroup")
    public Response removeUserFromGroup(
            @QueryParam ("userName") String userName,
            @QueryParam ("groupName") String groupName)
    {
        Group group = crowdService.getGroup(groupName);
        ApplicationUser userToRemove = userUtil.getUserByName(userName);

        try
        {
            userUtil.removeUserFromGroup(group, userToRemove);
        }
        catch (PermissionException e)
        {
            log.warn("PermissionException removing user from group", e);
            throw new RuntimeException(e);
        }
        catch (RemoveException e)
        {
            log.warn("RemoveExceptionNotPermittedException removing user from group", e);
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("group/add")
    public Response addGroup(@QueryParam ("groupName") final String groupName)
    {
        try
        {
            crowdService.addGroup(new GroupTemplate(groupName));
        }
        catch (InvalidGroupException e)
        {
            log.warn("InvalidGroupException adding group", e);
            throw new RuntimeException(e);
        }
        catch (OperationNotPermittedException e)
        {
            log.warn("OperationNotPermittedException adding group", e);
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

	@GET
	@AnonymousAllowed
	@Path("group/delete")
	public Response deleteGroup(@QueryParam ("groupName") final String groupName)
	{
		try
		{
			crowdService.removeGroup(new GroupTemplate(groupName));
		}
		catch (OperationNotPermittedException e)
		{
			log.warn("OperationNotPermittedException adding group", e);
			throw new RuntimeException(e);
		}

		return Response.ok(null).build();
	}

    @GET
    @AnonymousAllowed
    @Path("group/addToGroup")
    public Response addGroupToGroup(
            @QueryParam ("groupName") String groupName,
            @QueryParam ("parentGroupName") String parentGroupName)
    {
        Group childGroup = crowdService.getGroup(groupName);
        Group parentGroup = crowdService.getGroup(parentGroupName);

        try
        {
            crowdService.addGroupToGroup(childGroup, parentGroup);
        }
        catch (OperationNotPermittedException | InvalidMembershipException e)
        {
            log.warn("Exception adding group to group", e);
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("group/addMany")
    public Response addManyGroups(
            @QueryParam ("groupNamePrefix") String groupNamePrefix,
            @QueryParam ("numberOfNewGroups") int numberOfNewGroups,
            @Nullable @QueryParam ("parentGroupName") String parentGroupName,
            @Nullable @QueryParam ("numberOfNewUsersPerGroup") Integer numberOfNewUsers)
    {
        Group parentGroup = null;
        if (StringUtils.isNotBlank(parentGroupName))
        {
            parentGroup = crowdService.getGroup(parentGroupName);
        }
        try
        {
            for (int i = 0; i < numberOfNewGroups; i++)
            {
                String groupName = groupNamePrefix + i;
                Group newGroup = crowdService.addGroup(new GroupTemplate(groupName));
                if (parentGroup != null)
                {
                    crowdService.addGroupToGroup(newGroup, parentGroup);
                }

                if (numberOfNewUsers != null && numberOfNewUsers > 0)
                {
                    String prefix = groupName + "-user";
                    addManyUsers(prefix, prefix, numberOfNewUsers, newGroup.getName());
                }
            }
        }
        catch (InvalidGroupException | OperationNotPermittedException | InvalidMembershipException e)
        {
            log.warn("Exception adding groups", e);
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

	@GET
    @AnonymousAllowed
    @Path("user/addMany")
    public Response addManyUsers(
            @QueryParam ("usernamePrefix") String usernamePrefix,
            @QueryParam ("displayNamePrefix") String displayNamePrefix,
            @QueryParam ("numberOfNewUsers") int numberOfNewUsers,
            @javax.annotation.Nullable @QueryParam ("groupName") String groupName)
    {
        Group group = null;
        if (StringUtils.isNotBlank(groupName))
        {
            group = crowdService.getGroup(groupName);
        }
        try
        {
            for (int i = 0; i < numberOfNewUsers; i++)
            {
                String username = usernamePrefix + i;
                userUtil.createUserNoNotification(username,
                        username,
                        "e" + username + "@example.com",     // make the email slightly different to the username
                        displayNamePrefix + i);

                if (group != null)
                {
                    ApplicationUser user = userUtil.getUserByName(username);
                    userUtil.addUserToGroup(group, user);
                }
            }
        }
        catch (PermissionException e)
        {
            log.warn("Permission exception adding users", e);
            throw new RuntimeException(e);
        }
        catch (CreateException e)
        {
            log.warn("Create exception adding users", e);
            throw new RuntimeException(e);
        }
        catch (AddException e)
        {
            log.warn("Add exception adding users", e);
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("user/resetLoginCount")
    public Response resetLoginCount(@QueryParam("user") String username)
    {
        ApplicationUser user = userUtil.getUserByName(username);
        loginService.resetFailedLoginCount(user);
        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("user/count")
    public Response numberOfUsers()
    {
        String count = Long.toString(userUtil.getTotalUserCount());
        
        return Response.ok(count).build();
    }

    @GET
    @AnonymousAllowed
    @Path("group/count")
    public Response numberOfGroups()
    {
        int count = groupManager.getAllGroups().size();
        String stringCount = Long.toString(count);

        return Response.ok(stringCount).build();
    }

	@GET
	@AnonymousAllowed
	@Path("group/includes")
	@Produces({MediaType.APPLICATION_JSON})
	public Response groupIncludes(@QueryParam ("groupName") String groupName, @QueryParam("userName") String userName) {
		return Response.ok(userUtil.getGroupNamesForUser(userName).contains(groupName)).build();
	}

	@GET
	@AnonymousAllowed
	@Path("user/exists")
	@Produces ({MediaType.APPLICATION_JSON})
	public Response userExists(@QueryParam("userName") String userName) {
		return Response.ok(userUtil.userExists(userName)).build();
	}

    @GET
    @AnonymousAllowed
    @Path("user/loginInfo")
    @Produces ({MediaType.APPLICATION_JSON})
    public Response loginInfo(@QueryParam("userName") String userName) {
        LoginInfo info = loginService.getLoginInfo(userName);
        if (info == null)
        {
            return Response.status(Status.BAD_REQUEST).entity("No user '" + userName + "' found").build();
        }
        final LoginInfoBean bean = new LoginInfoBean();
        bean.setLoginCount(nullToZero(info.getLoginCount()));
        bean.setCurrentFailedLoginCount(nullToZero(info.getCurrentFailedLoginCount()));
        bean.setTotalFailedLoginCount(nullToZero(info.getTotalFailedLoginCount()));
        return Response.ok(bean).build();
    }

    @GET
    @AnonymousAllowed
    @Path("user/byName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByName(@QueryParam("userName") String userName)
    {
        final ApplicationUser user = userUtil.getUserByName(userName);
        if (user == null)
        {
            return NOT_FOUND;
        }
        return Response.ok(new UserDTO(user)).build();
    }

    @GET
    @AnonymousAllowed
    @Path("user/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers()
    {
        return Response.ok(Collections2.transform(userManager.getAllApplicationUsers(), user -> new UserDTO(user))).build();
    }

    @POST
    @AnonymousAllowed
    @Path("user/byName")
    @XsrfProtectionExcluded // Only available during testing.
    public Response updateUser(final UserDTO user)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Updating user with: " + user);
        }
        final ApplicationUser existingUser = userManager.getUserByKey(user.getKey());
        if (existingUser == null)
        {
            return NOT_FOUND;
        }
        final ApplicationUser updatedUser = user.asApplicationUser(existingUser.getDirectoryUser());
        try
        {
            userManager.updateUser(updatedUser);
        }
        catch (final UserNotFoundException e)
        {
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        }
        return OK;
    }

    @GET
    @AnonymousAllowed
    @Path("directory")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDirectories()
    {
        return Response.ok(Lists.transform(crowdDirectoryService.findAllDirectories(), new Function<Directory, DirectoryDTO>()
        {
            @Override
            public DirectoryDTO apply(final Directory directory)
            {
                return new DirectoryDTO(directory);
            }
        })).build();
    }

    @GET
    @AnonymousAllowed
    @Path("directory/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDirectory(@PathParam("id") final long id)
    {
        return Response.ok(new DirectoryDTO(crowdDirectoryService.findDirectoryById(id))).build();
    }

    private static long nullToZero(Long theLong)
    {
        return theLong != null ? theLong : 0;
    }

    private static class GroupTemplate implements Group
    {
        private final String groupName;

        public GroupTemplate(String groupName) {this.groupName = groupName;}

        @Override
        public String getName()
        {
            return groupName;
        }

        @Override
        public int compareTo(Group o)
        {
            return groupName.compareTo(o.getName());
        }
    }

}
