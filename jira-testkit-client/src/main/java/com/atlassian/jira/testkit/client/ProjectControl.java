/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.project.type.ProjectTypeKey;
import com.atlassian.jira.testkit.beans.ProjectSchemesBean;
import com.atlassian.jira.testkit.beans.WorkflowSchemeData;
import com.atlassian.jira.testkit.beans.EntityList;
import com.sun.jersey.api.client.WebResource;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Projects.
 *
 * See <code>com.atlassian.jira.testkit.plugin.ProjectBackdoor</code> in jira-testkit-plugin for backend.
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
     * Adds a project, or if a project with that name exists, does almost nothing.
     * Choose a project name that will not clash with operational links on the page
     * such as "View Projects" or "Add".
     *
     * The projects created by this method are of the business type.
     *
     * @param name the name of the project
     * @param key  the project key
     * @param lead the username of the project lead
     * @return the project ID
     */
    public long addProject(String name, String key, String lead)
    {
        return addProject(name, key, lead, "business");
    }

    /**
     * Adds a project in the same way as {@link #addProject(String, String, String)}, but allows to specify the project type.
     *
     * @param name the name of the project
     * @param key  the project key
     * @param lead the username of the project lead
     * @param projectType the project type
     * @return the project ID
     */
    public long addProject(String name, String key, String lead, String projectType)
    {
        final String s = createResource().path("project/add")
                .queryParam("name", name)
                .queryParam("key", key)
                .queryParam("lead", lead)
                .queryParam("type", projectType)
                .get(String.class);
        return Long.parseLong(s);
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

    public void deleteProject(String key)
    {
        createResource().path("project").path(key).delete();
    }

    /**
     * Sets the permission scheme for the given Project to be the given Scheme.
     *
     * @param projectId the id of the project
     * @param schemeId  the id of the Permission Scheme
     */
    public void setPermissionScheme(long projectId, long schemeId)
    {
        get(createResource().path("project/permissionScheme/set")
                .queryParam("project", "" + projectId)
                .queryParam("scheme", "" + schemeId));
    }

    /**
     * Sets the notification scheme for the given Project to be the given Scheme.
     *
     * @param projectId the id of the project
     * @param schemeId  the id of the scheme
     */
    public void setNotificationScheme(long projectId, Long schemeId)
    {
        WebResource r = createResource().path("project/notificationScheme/set")
                .queryParam("project", "" + projectId);
        if (schemeId != null)
            r = r.queryParam("scheme", "" + schemeId);
        get(r);
    }

    /**
     * Sets the notification scheme for the given Project to be the given Scheme.
     *
     * @param projectId the id of the project
     * @param schemeId  the id of the scheme
     */
    public void setIssueSecurityScheme(long projectId, Long schemeId)
    {
        WebResource r = createResource().path("project/issueSecurityScheme/set")
                .queryParam("project", "" + projectId);
        if (schemeId != null)
            r = r.queryParam("scheme", "" + schemeId);
        get(r);
    }

    /**
     * Sets the field configuration scheme for the given Project to be the given Scheme.
     *
     * @param projectId the id of the project
     * @param schemeId  the id of the scheme
     */
    public void addFieldConfigurationScheme(long projectId, long schemeId)
    {
        get(createResource().path("project/fieldConfigurationScheme/add")
                .queryParam("project", "" + projectId)
                .queryParam("scheme", "" + schemeId));
    }

    /**
     * Sets the field configuration scheme for the given Project to be the given Scheme.
     *
     * @param projectId the id of the project
     * @param schemeId  the id of the scheme
     */
    public void removeFieldConfigurationScheme(long projectId, long schemeId)
    {
        get(createResource().path("project/fieldConfigurationScheme/remove")
                .queryParam("project", "" + projectId)
                .queryParam("scheme", "" + schemeId));
    }

    /**
     * Sets project category for the given project.
     *
     * @param projectId id of the project
     * @param projectCategoryId id of the project category
     */
    public void setProjectCategory(long projectId, long projectCategoryId)
    {
        get(createResource().path("project/projectCategory/set")
                .queryParam("project", "" + projectId)
                .queryParam("projectCategoryId", "" + projectCategoryId));

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
     * @see com.atlassian.jira.bc.project.ProjectService.UpdateProjectValidationResult
     */
    public void setProjectDefaultAssignee(long projectId, boolean setToProjectLead)
    {
        WebResource resource = createResource().path("project/defaultAssignee/set")
                .queryParam("project", "" + projectId)
                .queryParam("setToProjectLead", "" + setToProjectLead);

        get(resource);
    }

    /**
     * Retrieves the schemes for the specified project.
     *
     * @param projectId The ID of the project. Must not be null.
     * @return the schemes for the specified project
     */
    public ProjectSchemesBean getSchemes(Long projectId)
    {
        return getSchemes(Long.toString(projectId));
    }

    /**
     * Retrieves the schemes for the specified project.
     *
     * @param projectIdOrKey The ID or key of the project. Must not be null.
     * @return the schemes for the specified project
     */
    public ProjectSchemesBean getSchemes(String projectIdOrKey)
    {
        return createProjectSchemesResource(projectIdOrKey).get(ProjectSchemesBean.class);
    }

    /**
     * Gets the type key of a project, given its identifier.
     *
     * @param projectId The identifier of the project
     * @return The project type key
     */
    public ProjectTypeKey getProjectType(Long projectId)
    {
        final String type = createResource().path("project").path(String.valueOf(projectId)).path("type").get(String.class);
        return new ProjectTypeKey(type);
    }

    /**
     * Updates the type of a project.
     *
     * @param projectId The identifier of the project
     * @param newProjectType The new project type
     */
    public void updateProjectType(Long projectId, ProjectTypeKey newProjectType)
    {
        createResource().path("project").path(String.valueOf(projectId)).path("type").path(newProjectType.getKey()).put();
    }

    private WebResource createProjectSchemesResource(String projectIdOrKey)
    {
        return createResource().path("project").path(projectIdOrKey).path("schemes");
    }

    public EntityList getEntityLinks(Long projectId)
    {
        return createResource().path("applinks").path("entitylinks").queryParam("projectId", Long.toString(projectId)).get(EntityList.class);
    }
}
