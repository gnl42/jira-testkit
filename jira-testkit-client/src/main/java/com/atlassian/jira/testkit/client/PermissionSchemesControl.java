/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.permission.JiraPermissionHolderType;
import com.atlassian.jira.security.plugin.ProjectPermissionKey;
import com.atlassian.jira.testkit.beans.PermissionSchemeBean;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.map.ObjectMapper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.Long;
import java.lang.String;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Permission Schemes.
 *
 * See <code>com.atlassian.jira.testkit.plugin.PermissionSchemesBackdoor</code> in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class PermissionSchemesControl extends BackdoorControl<PermissionSchemesControl>
{
    public PermissionSchemesControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * Makes a copy of the Default Permission Scheme and returns the id of the new scheme.
     *
     * @param schemeName the name of the new scheme
     * @return {Long} the schemeId of the created scheme
     */
    public Long copyDefaultScheme(String schemeName)
    {
        return Long.parseLong(createResource().path("permissionSchemes/copyDefault")
                .queryParam("schemeName", schemeName).get(String.class));
    }

    /**
     * Makes a copy of the Default Permission Scheme and returns the id of the new scheme.
     *
     * @param schemeName the name of the new scheme
     * @param description can be null
     * @return {Long} the schemeId of the created scheme
     */
    public Long createScheme(String schemeName, String description)
    {
        return Long.parseLong(createResource().path("permissionSchemes/create")
                .queryParam("schemeName", schemeName).queryParam("schemeDescription", description).get(String.class));
    }

    public void deleteScheme(long schemeId)
    {
        createResource().path("permissionSchemes").path(String.valueOf(schemeId)).delete();
    }

    /**
     * Create a new add permission request for a given permission scheme
     * @param schemeId to add permission assignment to
     * @param permission to add assignment
     * @param type of permission assignment.
     * @return request builder that can request the change
     */
    public PermissionRequestBuilder addPermission(long schemeId, ProjectPermissionKey permission, String type)
    {
        return new PermissionRequestBuilder("entity/add").schemeId(schemeId).permission(permission).type(type);
    }

    /**
     * See {@link PermissionSchemesControl#addPermission(long, ProjectPermissionKey, String)}
     */
    public PermissionRequestBuilder addPermission(long schemeId, ProjectPermissionKey permission, JiraPermissionHolderType type)
    {
        return addPermission(schemeId, permission, type.getKey());
    }

    /**
     * Create a new remove permission request for a given permission scheme
     * @param schemeId to remove permission assignment from
     * @param permission to remove assignment
     * @param type of permission assignment.
     * @return request builder that can request the change
     */
    public PermissionRequestBuilder removePermission(long schemeId, ProjectPermissionKey permission, String type)
    {
        return new PermissionRequestBuilder("entity/remove").schemeId(schemeId).permission(permission).type(type);
    }

    /**
     * See {@link PermissionSchemesControl#removePermission(long, ProjectPermissionKey, String)}
     */
    public PermissionRequestBuilder removePermission(long schemeId, ProjectPermissionKey permission, JiraPermissionHolderType type)
    {
        return removePermission(schemeId, permission, type.getKey());
    }

    /**
     * Remove all current permission assignments and add a new one of a given type
     * @param schemeId to replace permission assignments from
     * @param permission to replace assignment
     * @param type of new permission assignment.
     * @return request builder that can request the change
     */
    public PermissionRequestBuilder replacePermission(long schemeId, ProjectPermissionKey permission, String type)
    {
        return new PermissionRequestBuilder("entity/replace").schemeId(schemeId).permission(permission).type(type);
    }

    /**
     * See {@link PermissionSchemesControl#replacePermission(long, ProjectPermissionKey, String)}
     */
    public PermissionRequestBuilder replacePermission(long schemeId, ProjectPermissionKey permission, JiraPermissionHolderType type)
    {
        return replacePermission(schemeId, permission, type.getKey());
    }

    /**
     * Get all the permission entries for a given scheme.
     *
     * @param schemeId of the permission scheme
     * @return JSONObject representing permissions
     */
    public PermissionSchemeBean getAssignedPermissions(long schemeId)
    {
        String response = new PermissionRequestBuilder(Long.toString(schemeId)).getRequest();

        try
        {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, PermissionSchemeBean.class);

        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * @deprecated Use {@link PermissionSchemesControl#addUserPermission(long, ProjectPermissionKey, String)} instead
     */
    @Deprecated
    public void addUserPermission(long schemeId, int permission, String userName)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.USER.getKey(), userName);
    }

    /**
     * Adds a user permission.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param userName the username
     */
    public void addUserPermission(long schemeId, ProjectPermissionKey permission, String userName)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.USER)
                .parameter(userName)
                .getRequest();
    }


    /**
     * @deprecated Use {@link PermissionSchemesControl#removeUserPermission(long, ProjectPermissionKey, String)} instead
     */
    @Deprecated
    public void removeUserPermission(long schemeId, int permission, String userName)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.USER.getKey(), userName);
    }

    /**
     * Removes a user permission.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param userName the username
     */
    public void removeUserPermission(long schemeId, ProjectPermissionKey permission, String userName)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.USER)
                .parameter(userName)
                .getRequest();
    }

    /**
     * Replace all of the current permissions and replace with the following user permission
     * @param schemeId of the permission scheme
     * @param permission to replace
     * @param userName to replace with
     */
    public void replaceUserPermission(long schemeId, ProjectPermissionKey permission, String userName)
    {
        replacePermission(schemeId, permission, JiraPermissionHolderType.USER)
                .parameter(userName)
                .getRequest();
    }

    /**
     * Add a new user custom field to a given permission
     * @param schemeId of the permission scheme
     * @param permission to add to
     * @param customFieldName to add permission to
     */
    public void addUserCustomFieldPermission(long schemeId, ProjectPermissionKey permission, String customFieldName)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.USER_CUSTOM_FIELD)
                .parameter(customFieldName)
                .getRequest();
    }

    /**
     * Remove a give user custom field from a permission
     * @param schemeId of the permission scheme
     * @param permission to replace
     * @param customFieldName to replace permission for
     */
    public void removeUserCustomFieldPermission(long schemeId, ProjectPermissionKey permission, String customFieldName)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.USER_CUSTOM_FIELD)
                .parameter(customFieldName)
                .getRequest();
    }

    /**
     * Replace all of the current permissions and replace with the given custom field.
     * @param schemeId of the permission scheme
     * @param permission to replace
     * @param customFieldName to replace permission for
     */
    public void replaceUserCustomFieldPermissions(long schemeId, ProjectPermissionKey permission, String customFieldName)
    {
        replacePermission(schemeId, permission, JiraPermissionHolderType.USER_CUSTOM_FIELD)
                .parameter(customFieldName)
                .getRequest();
    }


    /**
     * Add the current assignee to the given scheme permission
     * @param schemeId for permission scheme to add to
     * @param permission to add current assignee permission to
     */
    public void addCurrentAssigneePermission(final long schemeId, @Nonnull final ProjectPermissionKey permission)
    {
        addPermission(schemeId, permission, IssueFieldConstants.ASSIGNEE)
                .getRequest();
    }

    /**
     * Add a given application role to the given scheme permission
     * @param schemeId for permission scheme to add to
     * @param permission to add current assignee permission to
     * @param applicationRole to apply permission
     */
    public void addApplicationRolePermission(final long schemeId, @Nonnull final ProjectPermissionKey permission,
                                             @Nonnull final String applicationRole)
    {
        addPermission(schemeId, permission, "applicationRole")
                .parameter(applicationRole)
                .getRequest();
    }

    /**
     * Add the reporter to the given scheme permission
     * @param schemeId for permission scheme to add to
     * @param permission to add current assignee permission to
     */
    public void addReporterPermission(final long schemeId, @Nonnull final ProjectPermissionKey permission)
    {
        addPermission(schemeId, permission, IssueFieldConstants.REPORTER)
                .getRequest();
    }

    /**
     * @deprecated Use {@link PermissionSchemesControl#addGroupPermission(Long, ProjectPermissionKey, String)} instead
     */
    @Deprecated
    public void addGroupPermission(Long schemeId, int permission, String groupName)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.GROUP.getKey(), groupName);
    }

    /**
     * Adds a group permission.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param groupName the group name
     */
    public void addGroupPermission(Long schemeId, ProjectPermissionKey permission, String groupName)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.GROUP)
            .parameter(groupName)
            .getRequest();
    }

    /**
     * @deprecated Use {@link PermissionSchemesControl#removeGroupPermission(long, ProjectPermissionKey, String)} instead
     */
    @Deprecated
    public void removeGroupPermission(long schemeId, int permission, String groupName)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.GROUP.getKey(), groupName);
    }
    /**
     * Removes a group permission.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param groupName the group name
     */
    public void removeGroupPermission(long schemeId, ProjectPermissionKey permission, String groupName)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.GROUP)
                .parameter(groupName)
                .getRequest();
    }

    /**
     * @deprecated Use {@link PermissionSchemesControl#replaceGroupPermissions(long, ProjectPermissionKey, String)} instead
     */
    @Deprecated
    public void replaceGroupPermissions(long schemeId, int permission, String groupName)
    {
        replacePermissions(schemeId, permission, JiraPermissionHolderType.GROUP.getKey(), groupName);
    }

    /**
     * Removes any matching permission scheme entities for the given permission and adds an entity for the passed group.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param groupName the group name
     */
    public void replaceGroupPermissions(long schemeId, ProjectPermissionKey permission, String groupName)
    {
        replacePermission(schemeId, permission, JiraPermissionHolderType.GROUP)
                .parameter(groupName)
                .getRequest();
    }

    /**
     * @deprecated Use {@link PermissionSchemesControl#addProjectRolePermission(long, ProjectPermissionKey, long)} instead
     */
    @Deprecated
    public void addProjectRolePermission(long schemeId, int permission, long projectRoleId)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.PROJECT_ROLE.getKey(), Long.toString(projectRoleId));
    }
    /**
     * Adds a project role permission.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param projectRoleId the project role ID
     */
    public void addProjectRolePermission(long schemeId, ProjectPermissionKey permission, long projectRoleId)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.PROJECT_ROLE)
                .parameter(Long.toString(projectRoleId))
                .getRequest();
    }

    /**
     * @deprecated Use {@link PermissionSchemesControl#removeProjectRolePermission(long, ProjectPermissionKey, long)} instead
     */
    @Deprecated
    public void removeProjectRolePermission(long schemeId, int permission, long projectRoleId)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.PROJECT_ROLE.getKey(), Long.toString(projectRoleId));
    }

    /**
     * Removes a project role permission.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param projectRoleId the project role ID
     */
    public void removeProjectRolePermission(long schemeId, ProjectPermissionKey permission, long projectRoleId)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.PROJECT_ROLE)
                .parameter(Long.toString(projectRoleId))
                .getRequest();
    }

    /**
     * Replace all of the current project role permissions with the given role
     * @param schemeId of the permission scheme
     * @param permission to be edited
     * @param projectRoleId to set as permission
     */
    public void replaceProjectRolePermission(long schemeId, ProjectPermissionKey permission, long projectRoleId)
    {
        replacePermission(schemeId, permission, JiraPermissionHolderType.PROJECT_ROLE)
                .parameter(Long.toString(projectRoleId))
                .getRequest();
    }

    /**
     * Add the project lead permission type to a permission
     * @param schemeId of the permission scheme
     * @param permission to add project lead permission to
     */
    public void addProjectLeadPermission(long schemeId, ProjectPermissionKey permission)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.PROJECT_LEAD)
                .getRequest();
    }

    /**
     * Add the project lead permission type to a permission
     * @param schemeId of the permission scheme
     * @param permission to remove project lead permission from
     */
    public void removeProjectLeadPermission(long schemeId, ProjectPermissionKey permission)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.PROJECT_LEAD)
                .getRequest();
    }
    /**
     * Remove all current permission types and add the project lead permission type to a permission
     * @param schemeId of the permission scheme
     * @param permission to replace project lead permission with
     */
    public void replaceProjectLeadPermission(long schemeId, ProjectPermissionKey permission)
    {
        replacePermission(schemeId, permission, JiraPermissionHolderType.PROJECT_LEAD)
                .getRequest();
    }

    /**
     * Adds the given permission to the anonymous group ("anyone").
     *
     * @param schemeId the scheme ID
     * @param permission the permission to add
     */
    public void addEveryonePermission(Long schemeId, ProjectPermissionKey permission)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.GROUP)
                .getRequest();
    }

    /**
     * Removes only the given permission from the anonymous group ("anyone"). Other groups with this permission
     * will not be removed.
     *
     * @param schemeId the scheme ID
     * @param permission the permission to remove
     */
    public void removeEveryonePermission(Long schemeId, ProjectPermissionKey permission)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.GROUP)
                .getRequest();
    }


    public void addUserCustomFieldPermission(Long schemeId, ProjectPermissionKey permission, String fieldId)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.USER_CUSTOM_FIELD.getKey())
                .parameter(fieldId)
                .getRequest();
    }

    public void addGroupCustomFieldPermission(Long schemeId, ProjectPermissionKey permission, String fieldId)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.GROUP_CUSTOM_FIELD.getKey())
                .parameter(fieldId)
                .getRequest();
    }

    private void addPermission(long schemeId, int permission, String type, String parameter)
    {
        get(createResource().path("permissionSchemes/legacy/entity/add")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission)
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    private void removePermission(long schemeId, int permission, String type, String parameter)
    {
        get(createResource().path("permissionSchemes/legacy/entity/remove")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission)
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    private void replacePermissions(long schemeId, int permission, String type, String parameter)
    {
        get(createResource().path("permissionSchemes/legacy/entity/replace")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission)
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    /**
     * Requests a request to modify the permission entities for a permission scheme
     */
    public class PermissionRequestBuilder
    {
        private static final String BASE_URL = "permissionSchemes/";

        private final String requestUrl;
        private Long schemeId = null;
        private ProjectPermissionKey permission = null;
        private String type = null;
        private String parameter = null;

        public PermissionRequestBuilder(final String endpoint)
        {
            this.requestUrl = BASE_URL + endpoint;
        }

        public PermissionRequestBuilder schemeId(Long schemeId)
        {
            this.schemeId = schemeId;
            return this;
        }

        public PermissionRequestBuilder permission(ProjectPermissionKey permission)
        {
            this.permission = permission;
            return this;
        }

        public PermissionRequestBuilder type(JiraPermissionHolderType type)
        {
            this.type = type.getKey();
            return this;
        }

        public PermissionRequestBuilder type(String type)
        {
            this.type = type;
            return this;
        }

        public PermissionRequestBuilder parameter(String parameter)
        {
            this.parameter = parameter;
            return this;
        }

        public String getRequest()
        {
            return get(buildRequestResource(requestUrl));
        }

        private WebResource buildRequestResource(final String path)
        {
            WebResource resource = createResource().path(path);
            if (schemeId != null)
            {
                resource = resource.queryParam("schemeId", "" + schemeId);
            }
            if (permission != null)
            {
                resource = resource.queryParam("permission", "" + permission.permissionKey());
            }
            if (type != null)
            {
                resource = resource.queryParam("type", type);
            }
            if (parameter != null)
            {
                resource = resource.queryParam("parameter", parameter);
            }
            return resource;
        }
    }
}
