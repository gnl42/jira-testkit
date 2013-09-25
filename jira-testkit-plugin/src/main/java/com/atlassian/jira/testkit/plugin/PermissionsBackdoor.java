/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraPermission;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;

/**
 * Use this backdoor to manipulate Permissions as part of setup for tests.
 *
 * This class should only be called by the {@link com.atlassian.jira.testkit.client.PermissionsControl}.
 *
 * @since v5.0
 */
@Path ("permissions")
public class PermissionsBackdoor
{
    private final GlobalPermissionManager globalPermissionManager;

    public PermissionsBackdoor(GlobalPermissionManager globalPermissionManager)
    {
        this.globalPermissionManager = globalPermissionManager;
    }

    @GET
    @AnonymousAllowed
    @Path("global/add")
    public Response addGlobalPermission(@QueryParam ("type") int permissionType, @QueryParam ("group") String group)
    {
        try
        {
            globalPermissionManager.addPermission(permissionType, group);
        }
        catch (CreateException e)
        {
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("global/remove")
    public Response removeGlobalPermission(@QueryParam ("type") int permissionType, @QueryParam ("group") String group)
    {
        try
        {
            globalPermissionManager.removePermission(permissionType, group);
        }
        catch (RemoveException e)
        {
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }
    
    @GET
    @AnonymousAllowed
    @Produces ({ MediaType.APPLICATION_JSON })
    @Path ("global/getgroups")
    public Response getGlobalPermissionGroups(@QueryParam ("type") int permissionType)
    {
        Collection<String> groupNames = new ArrayList<String>();
        // Use this method instead getGroupNames as it will not retrun "anyone"
        // group which means null for group name.
        for (JiraPermission jiraPermission : globalPermissionManager.getPermissions(permissionType))
        {
            groupNames.add(jiraPermission.getGroup());
        }
        List<String> str = Lists.newArrayListWithCapacity(groupNames.size());
        str.addAll(groupNames);
        
        return Response.ok(str).cacheControl(never()).build();
    }

}
