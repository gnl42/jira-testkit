package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Projects.
 *
 * See ProjectBackdoor for the code this plugs into at the back-end.
 *
 * @since v5.0
 */
public class ProjectControl extends BackdoorControl<ProjectControl>
{
    public ProjectControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * Adds a business project, or if a project with that name exists, does almost nothing.
     * Choose a project name that will not clash with operational links on the page
     * such as "View Projects" or "Add".
     *
     * @param name the name of the project.
     * @param key  the project key.
     * @param lead the username of the project lead.
     */
    public void addProject(String name, String key, String lead)
    {
        addProject(name, key, lead, "business");
    }

    /**
     * Adds a project with the specified type, or if a project with that name exists, does almost nothing.
     * Choose a project name that will not clash with operational links on the page
     * such as "View Projects" or "Add".
     *
     * @param name the name of the project.
     * @param key  the project key.
     * @param lead the username of the project lead.
     * @param type the type of the project
     */
    public void addProject(String name, String key, String lead, String type)
    {
        get(createResource().path("project/add")
                .queryParam("name", name)
                .queryParam("key", key)
                .queryParam("lead", lead)
                .queryParam("type", type));
    }

    /**
     * Adds a project in the same way as {@link #addProject(String, String, String)}, but allows to specify the project template to be applied.
     *
     * @param name the name of the project
     * @param key  the project key
     * @param lead the username of the project lead
     * @param projectTemplateKey the key of the project template to be applied
     * @return the project ID
     */
    public long addProjectWithTemplate(String name, String key, String lead, String projectTemplateKey)
    {
        final String s = createResource().path("project/add")
                .queryParam("name", name)
                .queryParam("key", key)
                .queryParam("lead", lead)
                .queryParam("template", projectTemplateKey)
                .get(String.class);
        return Long.parseLong(s);
    }

    /**
     * Sets the permission scheme for the given Project to be the given Scheme.
     *
     * @param projectId the id of the project.
     * @param schemeId  the id of the Permission Scheme.
     */
    public void setPermissionScheme(long projectId, long schemeId)
    {
        get(createResource().path("project/permissionScheme/set")
                .queryParam("project", "" + projectId)
                .queryParam("scheme", "" + schemeId));
    }

    public void setIssueTypeScreenScheme(long projectId, long issueTypeScreenSchemeId)
    {
        get(createResource().path("project/issueTypeScreenScheme/set")
                .queryParam("project", "" + projectId)
                .queryParam("issueTypeScreenScheme", "" + issueTypeScreenSchemeId));
    }

    public void setDefaultIssueType(long projectId, String issueTypeId)
    {
        WebResource resource = createResource().path("project/defaultIssueType/set")
                .queryParam("project", "" + projectId);
        if(issueTypeId != null)
        {
                resource = resource.queryParam("issueTypeId", "" + issueTypeId);
        }

        get(resource);
    }

    /**
     * Sets the project lead to the specified username.
     * 
     * @param projectId the id of the project
     * @param username the username of the user to set as the project lead.
     */
    public void setProjectLead(long projectId, final String username)
    {
        WebResource resource = createResource().path("project/projectLead/set")
                .queryParam("project", "" + projectId)
                .queryParam("username", "" + username);

        get(resource);
    }
    
    /**
     * Sets the project's default assignee (i.e., the "Automatic" assignee) to either the project lead 
     * or to "Unassigned" (should JIRA be configured to allow assigning issues to Unassigned).
     * 
     * Calling this resource may throw an exception should Unassigned not be a valid assignee for the project.
     * 
     * @param projectId the id of the project
     * @param setToProjectLead if true, the assignee will be set to the project lead. If false, it will attempt to
     * set to Unassigned.
     * 
     * @see {@link com.atlassian.jira.bc.project.ProjectService.UpdateProjectValidationResult}
     */
    public void setProjectDefaultAssignee(long projectId, boolean setToProjectLead)
    {
        WebResource resource = createResource().path("project/defaultAssignee/set")
                .queryParam("project", "" + projectId)
                .queryParam("setToProjectLead", "" + setToProjectLead);

        get(resource);
    }

    /**
     * Gets the project's default assignee (i.e., the "Automatic" assignee) 
     *
     * @param projectKey the key of the project
     */
    public ProjectAssigneeType getProjectDefaultAssignee(String projectKey)
    {
        WebResource resource = createResource().path("project/defaultAssignee/get")
                .queryParam("project", projectKey);

        return ProjectAssigneeType.withId(Integer.parseInt(get(resource)));
    }

	public boolean deleteProject(String key)
	{
		return get(createResource().path("project/delete").queryParam("key", key), Boolean.class);
	}

    public static enum ProjectAssigneeType
    {
        PROJECT_DEFAULT(0), COMPONENT_LEAD(1), PROJECT_LEAD(2), UNASSIGNED(3);
        
        final private int id;
        
        ProjectAssigneeType(int id)
        {
            this.id = id;
        }
        
        static ProjectAssigneeType withId(int id)
        {
            for (ProjectAssigneeType type : ProjectAssigneeType.values())
            {
                if (type.id == id)
                {
                    return type;
                }
            }
            throw new IllegalArgumentException(id + " is not a valid ProjectAssigneeType id.");
        }
    }
}