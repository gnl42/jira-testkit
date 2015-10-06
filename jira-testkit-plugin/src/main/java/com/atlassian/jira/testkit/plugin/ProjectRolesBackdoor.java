/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.bc.projectroles.ProjectRoleService;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path ("projectRoles")
@AnonymousAllowed
public class ProjectRolesBackdoor
{
    private final ProjectRoleService projectRoleService;

    public ProjectRolesBackdoor(ProjectRoleService projectRoleService)
    {
        this.projectRoleService = projectRoleService;
    }

    @DELETE
    @Path("{roleName}")
    public Response deleteProjectRole(@PathParam("roleName") String roleName)
    {
        final ErrorCollection errorCollection = new SimpleErrorCollection();
        final ProjectRole role = projectRoleService.getProjectRoleByName(roleName, errorCollection);
        if (role != null && !errorCollection.hasAnyErrors()) {
            projectRoleService.deleteProjectRole(role, errorCollection);
            return Response.ok().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
