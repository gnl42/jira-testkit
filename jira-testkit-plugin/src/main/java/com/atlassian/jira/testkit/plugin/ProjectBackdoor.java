package com.atlassian.jira.testkit.plugin;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.compatibility.bridge.project.ProjectCreationData;
import com.atlassian.jira.compatibility.bridge.project.ProjectServiceBridge;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager;
import com.atlassian.jira.permission.PermissionSchemeManager;
import com.atlassian.jira.project.AssigneeTypes;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectAssigneeTypes;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Use this backdoor to manipulate Projects as part of setup for tests.
 *
 * This class should only be called by the {com.atlassian.jira.functest.framework.backdoor.ProjectControl}.
 *
 * @since v5.0
 */
@Path ("project")
public class ProjectBackdoor
{
    private final ProjectService projectService;
    private final ProjectServiceBridge projectServiceBridge;
    private final PermissionSchemeManager permissionSchemeManager;
    private final UserUtil userUtil;
    private final IssueTypeSchemeManager issueTypeSchemeManager;
    private final IssueTypeScreenSchemeManager issueTypeScreenSchemeManager;

    public ProjectBackdoor(ProjectService projectService, ProjectServiceBridge projectServiceBridge, PermissionSchemeManager permissionSchemeManager,
            UserUtil userUtil, IssueTypeSchemeManager issueTypeSchemeManager,
            IssueTypeScreenSchemeManager issueTypeScreenSchemeManager)
    {
        this.projectService = projectService;
        this.projectServiceBridge = projectServiceBridge;
        this.permissionSchemeManager = permissionSchemeManager;
        this.userUtil = userUtil;
        this.issueTypeSchemeManager = issueTypeSchemeManager;
        this.issueTypeScreenSchemeManager = issueTypeScreenSchemeManager;
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
    @AnonymousAllowed
    @Path("add")
    public Response addProject(@QueryParam ("name") String name,
                               @QueryParam ("key") String key,
                               @QueryParam ("lead") String lead,
                               @QueryParam ("type") String type)
    {
        ApplicationUser admin = userUtil.getUserByName("admin");

        // Create the project
        ErrorCollection errorCollection = new EmptyErrorCollection();
        ProjectCreationData projectCreationData = new ProjectCreationData.Builder()
                .withName(name)
                .withKey(key)
                .withLead(userUtil.getUserByName(lead))
                .withDescription("This project is awesome")
                .withAssigneeType(AssigneeTypes.PROJECT_LEAD)
                .withType(type)
                .build();
        
        ProjectService.CreateProjectValidationResult result = projectServiceBridge.validateCreateProject(admin, projectCreationData);
        Project project = projectService.createProject(result);

        // Add the schemes
        Long permissionSchemeId = permissionSchemeManager.getDefaultSchemeObject().getId();
        ProjectService.UpdateProjectSchemesValidationResult schemesResult = new ProjectService.UpdateProjectSchemesValidationResult(
                errorCollection, permissionSchemeId, null, null);
        projectService.updateProjectSchemes(schemesResult, project);

        return Response.ok(project.getId().toString()).build();
    }

    @GET
    @AnonymousAllowed
    @Path("permissionScheme/set")
    public Response setPermissionScheme(@QueryParam ("project") long projectId,
            @QueryParam ("scheme") long schemeId)
    {
        ApplicationUser admin = userUtil.getUserByName("admin");
        Scheme scheme = permissionSchemeManager.getSchemeObject(schemeId);
        Project project = projectServiceBridge.getProjectById(admin, projectId).getProject();

        permissionSchemeManager.removeSchemesFromProject(project);
        permissionSchemeManager.addSchemeToProject(project, scheme);

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("defaultIssueType/set")
    public Response setDefaultIssueType(@QueryParam ("project") long projectId,
            @QueryParam ("issueTypeId") String issueTypeId)
    {
        User admin = userUtil.getUser("admin");

        Project project = projectService.getProjectById(admin, projectId).getProject();
        final FieldConfigScheme issueTypeScheme = issueTypeSchemeManager.getConfigScheme(project);
        issueTypeSchemeManager.setDefaultValue(issueTypeScheme.getOneAndOnlyConfig(), issueTypeId);

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("issueTypeScreenScheme/set")
    public Response setIssueTypeScreenScheme(@QueryParam ("project") long projectId,
            @QueryParam ("issueTypeScreenScheme") long issueTypeScreenSchemeId)
    {
        User admin = userUtil.getUser("admin");

        Project project = projectService.getProjectById(admin, projectId).getProject();
        IssueTypeScreenScheme issueTypeScreenScheme = issueTypeScreenSchemeManager.getIssueTypeScreenScheme(issueTypeScreenSchemeId);

        issueTypeScreenSchemeManager.addSchemeAssociation(project.getGenericValue(), issueTypeScreenScheme);

        return Response.ok(null).build();
    }
    
    @GET
    @AnonymousAllowed
    @Path("projectLead/set")
    public Response setAutomaticAssignee(@QueryParam ("project") long projectId,
            @QueryParam ("username") final String username)
    {
        User admin = userUtil.getUser("admin");
        User newProjectLead = userUtil.getUser(username);

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
    @AnonymousAllowed
    @Path("defaultAssignee/set")
    public Response setAutomaticAssignee(@QueryParam ("project") long projectId,
            @QueryParam ("setToProjectLead") final boolean setToProjectLead)
    {
        User admin = userUtil.getUser("admin");

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
    @Path("defaultAssignee/get")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAutomaticAssignee(@QueryParam ("project") String projectKey)
    {
        ApplicationUser admin = userUtil.getUserByName("admin");
        ProjectService.GetProjectResult projectResult = projectServiceBridge.getProjectByKey(admin, projectKey);
        
        if (!projectResult.isValid())
        {
            return Response.ok(projectResult.getErrorCollection().getErrorMessages()).status(Response.Status.NOT_FOUND).build();
        }
        
        long assigneeType = projectResult.getProject().getAssigneeType();
        return Response.ok(Long.toString(assigneeType)).build();
    }
    
	@GET
	@AnonymousAllowed
	@Path("delete")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteProject(@QueryParam ("key") String key)
	{
		ApplicationUser admin = userUtil.getUserByName("admin");
		Project project = projectService.getProjectByKey(admin, key).getProject();
		if (project != null) {
			ErrorCollection errorCollection = new EmptyErrorCollection();
			ProjectService.DeleteProjectValidationResult result = new ProjectService.DeleteProjectValidationResult(errorCollection, project);
			ProjectService.DeleteProjectResult projectResult = projectServiceBridge.deleteProject(admin, result);
			return Response.ok(projectResult.isValid()).build();
		}
		return Response.ok(false).build();
	}

}
