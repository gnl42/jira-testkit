/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.bc.project.ProjectCreationData;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager;
import com.atlassian.jira.issue.security.IssueSecuritySchemeManager;
import com.atlassian.jira.notification.NotificationSchemeManager;
import com.atlassian.jira.permission.PermissionSchemeManager;
import com.atlassian.jira.project.AssigneeTypes;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectAssigneeTypes;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.type.ProjectTypeKey;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.testkit.plugin.util.CacheControl;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.apache.log4j.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Use this backdoor to manipulate Projects as part of setup for tests.
 *
 * This class should only be called by the testkit-client..
 *
 * @since v5.0
 */
@Path ("project")
@AnonymousAllowed
public class ProjectBackdoor
{
    private final Logger log = Logger.getLogger(ProjectBackdoor.class);
    private final ProjectService projectService;
    private final PermissionSchemeManager permissionSchemeManager;
    private final UserUtil userUtil;
    private final IssueTypeSchemeManager issueTypeSchemeManager;
    private final IssueTypeScreenSchemeManager issueTypeScreenSchemeManager;
    private final NotificationSchemeManager notificationSchemeManager;
    private final IssueSecuritySchemeManager issueSecuritySchemeManager;
    private final FieldLayoutManager fieldLayoutManager;
    private final ProjectManager projectManager;
    private final UserManager userManager;

    public ProjectBackdoor(ProjectService projectService, PermissionSchemeManager permissionSchemeManager,
            UserUtil userUtil, IssueTypeSchemeManager issueTypeSchemeManager,
            IssueTypeScreenSchemeManager issueTypeScreenSchemeManager,
            NotificationSchemeManager notificationSchemeManager,
            IssueSecuritySchemeManager issueSecuritySchemeManager, FieldLayoutManager fieldLayoutManager,
            ProjectManager projectManager, UserManager userManager) {
        this.projectService = projectService;
        this.permissionSchemeManager = permissionSchemeManager;
        this.userUtil = userUtil;
        this.issueTypeSchemeManager = issueTypeSchemeManager;
        this.issueTypeScreenSchemeManager = issueTypeScreenSchemeManager;
        this.notificationSchemeManager = notificationSchemeManager;
        this.issueSecuritySchemeManager = issueSecuritySchemeManager;
        this.fieldLayoutManager = fieldLayoutManager;
        this.projectManager = projectManager;
        this.userManager = userManager;
    }

    /**
     * Adds a project, or if a project with that name exists, does almost nothing.
     * Choose a project name that will not clash with operational links on the page
     * such as "View Projects" or "Add".
     *
     * @param name the name of the project.
     * @param key  the project key.
     * @param lead the username of the project lead.
     * @return an OK response
     */
    @GET
    @Path("add")
    public Response addProject(@QueryParam ("name") String name,
                               @QueryParam ("key") String key,
                               @QueryParam ("lead") String lead,
                               @QueryParam ("type") String projectType)
    {
        // Create the project
        ErrorCollection errorCollection = new EmptyErrorCollection();
        ProjectService.CreateProjectValidationResult result = projectService.validateCreateProject(
                getUserWithAdminPermission(),
                new ProjectCreationData.Builder()
                    .withName(name)
                    .withKey(key)
                    .withDescription("This project is awesome")
                    .withLead(userManager.getUserByName(lead))
                    .withAssigneeType(AssigneeTypes.PROJECT_LEAD)
                    .withType(projectType)
                    .build()
        );
        if (!result.isValid())
        {
            log.error(String.format("Unable to create a project '%s': %s", name, result.getErrorCollection().toString()));
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        }

        Project project = projectService.createProject(result);

        // Add the schemes
        Long permissionSchemeId = permissionSchemeManager.getDefaultSchemeObject().getId();
        ProjectService.UpdateProjectSchemesValidationResult schemesResult = new ProjectService.UpdateProjectSchemesValidationResult(
                errorCollection, permissionSchemeId, null, null);
        projectService.updateProjectSchemes(schemesResult, project);

        return Response.ok(project.getId().toString()).build();
    }

    @DELETE
    @Path("{projectKey}")
    public Response delete(@PathParam("projectKey") String key)
    {
        final ApplicationUser adminUser = getUserWithAdminPermission();
        final ProjectService.DeleteProjectValidationResult deleteProjectValidationResult = projectService.validateDeleteProject(adminUser, key);
        if (!deleteProjectValidationResult.isValid())
        {
            return Response.serverError().cacheControl(CacheControl.never()).build();
        }
        projectService.deleteProject(adminUser, deleteProjectValidationResult);
        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    @GET
    @Path("permissionScheme/set")
    public Response setPermissionScheme(@QueryParam ("project") long projectId,
            @QueryParam ("scheme") long schemeId)
    {
        ApplicationUser admin = getUserWithAdminPermission();
        Scheme scheme = permissionSchemeManager.getSchemeObject(schemeId);
        Project project = projectService.getProjectById(admin, projectId).getProject();

        permissionSchemeManager.removeSchemesFromProject(project);
        permissionSchemeManager.addSchemeToProject(project, scheme);

        return Response.ok().build();
    }

    @GET
    @Path("notificationScheme/set")
    public Response setNotificationScheme(@QueryParam ("project") long projectId,
            @QueryParam ("scheme") Long schemeId)
    {
        ApplicationUser admin = getUserWithAdminPermission();
        Project project = projectService.getProjectById(admin, projectId).getProject();

        notificationSchemeManager.removeSchemesFromProject(project);

        if (schemeId != null)
        {
            Scheme scheme = notificationSchemeManager.getSchemeObject(schemeId);
            notificationSchemeManager.addSchemeToProject(project, scheme);
        }

        return Response.ok().build();
    }

    @GET
    @Path("issueSecurityScheme/set")
    public Response setIssueSecurityScheme(@QueryParam ("project") long projectId,
            @QueryParam ("scheme") Long schemeId)
    {
        ApplicationUser admin = getUserWithAdminPermission();
        Project project = projectService.getProjectById(admin, projectId).getProject();

        issueSecuritySchemeManager.removeSchemesFromProject(project);

        if (schemeId != null)
        {
            Scheme scheme = issueSecuritySchemeManager.getSchemeObject(schemeId);
            issueSecuritySchemeManager.addSchemeToProject(project, scheme);
        }

        return Response.ok().build();
    }

    @GET
    @Path("fieldConfigurationScheme/add")
    public Response addFieldConfigurationScheme(@QueryParam ("project") long projectId,
            @QueryParam ("scheme") Long schemeId)
    {
        ApplicationUser admin = getUserWithAdminPermission();
        Project project = projectService.getProjectById(admin, projectId).getProject();

        fieldLayoutManager.addSchemeAssociation(project, schemeId);

        return Response.ok().build();
    }

    @GET
    @Path("fieldConfigurationScheme/remove")
    public Response deleteFieldConfigurationScheme(@QueryParam ("project") long projectId,
            @QueryParam ("scheme") Long schemeId)
    {
        ApplicationUser admin = getUserWithAdminPermission();
        Project project = projectService.getProjectById(admin, projectId).getProject();

        fieldLayoutManager.removeSchemeAssociation(project, schemeId);

        return Response.ok().build();
    }

    @GET
    @Path("projectCategory/set")
    public Response setProjectCategory(@QueryParam ("project") long projectId,
            @QueryParam ("projectCategoryId") Long projectCategoryId)
    {
        ApplicationUser admin = getUserWithAdminPermission();
        Project project = projectService.getProjectById(admin, projectId).getProject();

        final ProjectCategory projectCategory = projectManager.getProjectCategoryObject(projectCategoryId);
        projectManager.setProjectCategory(project, projectCategory);

        return Response.ok().build();
    }

    private ApplicationUser getUserWithAdminPermission() {return userUtil.getUser("admin");}

    @GET
    @Path("defaultIssueType/set")
    public Response setDefaultIssueType(@QueryParam ("project") long projectId,
            @QueryParam ("issueTypeId") String issueTypeId)
    {
        ApplicationUser admin = getUserWithAdminPermission();

        Project project = projectService.getProjectById(admin, projectId).getProject();
        final FieldConfigScheme issueTypeScheme = issueTypeSchemeManager.getConfigScheme(project);
        issueTypeSchemeManager.setDefaultValue(issueTypeScheme.getOneAndOnlyConfig(), issueTypeId);

        return Response.ok(null).build();
    }

    @GET
    @Path("issueTypeScreenScheme/set")
    public Response setIssueTypeScreenScheme(@QueryParam ("project") long projectId,
            @QueryParam ("issueTypeScreenScheme") long issueTypeScreenSchemeId)
    {
        ApplicationUser admin = getUserWithAdminPermission();

        Project project = projectService.getProjectById(admin, projectId).getProject();
        IssueTypeScreenScheme issueTypeScreenScheme = issueTypeScreenSchemeManager.getIssueTypeScreenScheme(issueTypeScreenSchemeId);

        issueTypeScreenSchemeManager.addSchemeAssociation(project.getGenericValue(), issueTypeScreenScheme);

        return Response.ok(null).build();
    }
    
    @GET
    @Path("projectLead/set")
    public Response setAutomaticAssignee(@QueryParam ("project") long projectId,
            @QueryParam ("username") final String username)
    {
        ApplicationUser admin = getUserWithAdminPermission();
        ApplicationUser newProjectLead = userUtil.getUser(username);

        Project project = projectService.getProjectById(admin, projectId).getProject();
        ProjectService.UpdateProjectValidationResult result = projectService.validateUpdateProject(admin,
                project.getName(), project.getKey(), project.getDescription(),
                newProjectLead.getName(), project.getUrl(), project.getAssigneeType(), project.getAvatar().getId());
        
        if (!result.isValid())
        {
            return Response.serverError().build();
        }
        
        projectService.updateProject(result);
        
        return Response.ok(null).build();
    }
    
    
    @GET
    @Path("defaultAssignee/set")
    public Response setAutomaticAssignee(@QueryParam ("project") long projectId,
            @QueryParam ("setToProjectLead") final boolean setToProjectLead)
    {
        ApplicationUser admin = getUserWithAdminPermission();

        long assignee = ProjectAssigneeTypes.PROJECT_LEAD;
        if (!setToProjectLead)
        {
            assignee = ProjectAssigneeTypes.UNASSIGNED;
        }

        Project project = projectService.getProjectById(admin, projectId).getProject();
        ProjectService.UpdateProjectValidationResult result = projectService.validateUpdateProject(admin, 
                project.getName(), project.getKey(), project.getDescription(),
                project.getLead().getName(), project.getUrl(), assignee, project.getAvatar().getId());
        
        if (!result.isValid())
        {
            return Response.serverError().build();
        }
        
        projectService.updateProject(result);
        
        return Response.ok(null).build();
    }

	@GET
	@AnonymousAllowed
	@Path("delete")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteProject(@QueryParam ("key") String key)
	{
		ApplicationUser admin = userUtil.getUser("admin");
		Project project = projectService.getProjectByKey(admin, key).getProject();
		if (project != null) {
			ErrorCollection errorCollection = new EmptyErrorCollection();
			ProjectService.DeleteProjectValidationResult result = new ProjectService.DeleteProjectValidationResult(errorCollection, project);
			ProjectService.DeleteProjectResult projectResult = projectService.deleteProject(admin, result);
			return Response.ok(projectResult.isValid()).build();
		}
		return Response.ok(false).build();
	}

    @GET
    @Path("{projectId}/type")
    public Response getProjectType(@PathParam ("projectId") long projectId)
    {
        Project project = projectManager.getProjectObj(projectId);
        if (project == null)
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(project.getProjectTypeKey().getKey()).build();
    }

    @PUT
    @Path("{projectId}/type/{newType}")
    public Response updateProjectType(@PathParam ("projectId") long projectId, @PathParam ("newType") String newProjectType)
    {
        Project project = projectManager.getProjectObj(projectId);
        if (project == null)
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        projectService.updateProjectType(getUserWithAdminPermission(), project, new ProjectTypeKey(newProjectType));
        return Response.ok().build();
    }
}
