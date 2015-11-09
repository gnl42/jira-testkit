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
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.WebResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.Long;
import java.lang.String;
import java.util.List;
import java.util.Map;

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
     * Adds a user permission.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param userName the username
     * @deprecated Use {@link PermissionSchemesControl#addUserPermission(long, ProjectPermissionKey, String)} instead
     */
    @Deprecated
    public void addUserPermission(long schemeId, int permission, String userName)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.USER.getKey(), userName);
    }

    public void addUserPermission(long schemeId, ProjectPermissionKey permission, String userName)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.USER.getKey(), userName);
    }

    /**
     * Removes a user permission.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param userName the username
     * @deprecated Use {@link PermissionSchemesControl#removeUserPermission(long, ProjectPermissionKey, String)} instead
     */
    @Deprecated
    public void removeUserPermission(long schemeId, int permission, String userName)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.USER.getKey(), userName);
    }

    public void removeUserPermission(long schemeId, ProjectPermissionKey permission, String userName)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.USER.getKey(), userName);
    }

    /**
     * Replace all of the current permissions and replace with the following user permission
     * @param schemeId of the permission scheme
     * @param permission to replace
     * @param userName to replace with
     */
    public void replaceUserPermission(long schemeId, ProjectPermissionKey permission, String userName)
    {
        replacePermissions(schemeId, permission, JiraPermissionHolderType.USER.getKey(), userName);
    }

    /**
     * Add a new user custom field to a given permission
     * @param schemeId of the permission scheme
     * @param permission to add to
     * @param customFieldName to add permission to
     */
    public void addUserCustomFieldPermission(long schemeId, ProjectPermissionKey permission, String customFieldName)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.USER_CUSTOM_FIELD.getKey(), customFieldName);
    }

    /**
     * Remove a give user custom field from a permission
     * @param schemeId of the permission scheme
     * @param permission to replace
     * @param customFieldName to replace permission for
     */
    public void removeUserCustomFieldPermission(long schemeId, ProjectPermissionKey permission, String customFieldName)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.USER_CUSTOM_FIELD.getKey(), customFieldName);
    }

    /**
     * Replace all of the current permissions and replace with the given custom field.
     * @param schemeId of the permission scheme
     * @param permission to replace
     * @param customFieldName to replace permission for
     */
    public void replaceUserCustomFieldPermissions(long schemeId, ProjectPermissionKey permission, String customFieldName)
    {
        replacePermissions(schemeId, permission, JiraPermissionHolderType.USER_CUSTOM_FIELD.getKey(), customFieldName);
    }


    /**
     * Add the current assignee to the given scheme permission
     * @param schemeId for permission scheme to add to
     * @param permission to add current assignee permission to
     */
    public void addCurrentAssigneePermission(final long schemeId, @Nonnull final ProjectPermissionKey permission)
    {
        addPermission(schemeId, permission, IssueFieldConstants.ASSIGNEE);
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
        addPermission(schemeId, permission, "applicationRole", applicationRole);
    }

    /**
     * Add the reporter to the given scheme permission
     * @param schemeId for permission scheme to add to
     * @param permission to add current assignee permission to
     */
    public void addReporterPermission(final long schemeId, @Nonnull final ProjectPermissionKey permission)
    {
        addPermission(schemeId, permission, IssueFieldConstants.REPORTER);
    }

    /**
     * Adds a group permission.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param groupName the group name
     * @deprecated Use {@link PermissionSchemesControl#addGroupPermission(Long, ProjectPermissionKey, String)} instead
     */
    @Deprecated
    public void addGroupPermission(Long schemeId, int permission, String groupName)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.GROUP.getKey(), groupName);
    }

    public void addGroupPermission(Long schemeId, ProjectPermissionKey permission, String groupName)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.GROUP.getKey(), groupName);
    }

    /**
     * Removes a group permission.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param groupName the group name
     * @deprecated Use {@link PermissionSchemesControl#removeGroupPermission(long, ProjectPermissionKey, String)} instead
     */
    @Deprecated
    public void removeGroupPermission(long schemeId, int permission, String groupName)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.GROUP.getKey(), groupName);
    }

    public void removeGroupPermission(long schemeId, ProjectPermissionKey permission, String groupName)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.GROUP.getKey(), groupName);
    }

    /**
     * Removes any matching permission scheme entities for the given permission and adds an entity for the passed group.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param groupName the group name
     * @deprecated Use {@link PermissionSchemesControl#replaceGroupPermissions(long, ProjectPermissionKey, String)} instead
     */
    @Deprecated
    public void replaceGroupPermissions(long schemeId, int permission, String groupName)
    {
        replacePermissions(schemeId, permission, JiraPermissionHolderType.GROUP.getKey(), groupName);
    }

    public void replaceGroupPermissions(long schemeId, ProjectPermissionKey permission, String groupName)
    {
        replacePermissions(schemeId, permission, JiraPermissionHolderType.GROUP.getKey(), groupName);
    }

    /**
     * Adds a project role permission.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param projectRoleId the project role ID
     * @deprecated Use {@link PermissionSchemesControl#addProjectRolePermission(long, ProjectPermissionKey, long)} instead
     */
    @Deprecated
    public void addProjectRolePermission(long schemeId, int permission, long projectRoleId)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.PROJECT_ROLE.getKey(), Long.toString(projectRoleId));
    }

    public void addProjectRolePermission(long schemeId, ProjectPermissionKey permission, long projectRoleId)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.PROJECT_ROLE.getKey(), Long.toString(projectRoleId));
    }

    /**
     * Removes a project role permission.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param projectRoleId the project role ID
     * @deprecated Use {@link PermissionSchemesControl#removeProjectRolePermission(long, ProjectPermissionKey, long)} instead
     */
    @Deprecated
    public void removeProjectRolePermission(long schemeId, int permission, long projectRoleId)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.PROJECT_ROLE.getKey(), Long.toString(projectRoleId));
    }

    public void removeProjectRolePermission(long schemeId, ProjectPermissionKey permission, long projectRoleId)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.PROJECT_ROLE.getKey(), Long.toString(projectRoleId));
    }

    /**
     * Replace all of the current project role permissions with the given role
     * @param schemeId of the permission scheme
     * @param permission to be edited
     * @param projectRoleId to set as permission
     */
    public void replaceProjectRolePermission(long schemeId, ProjectPermissionKey permission, long projectRoleId)
    {
        replacePermissions(schemeId, permission, JiraPermissionHolderType.PROJECT_ROLE.getKey(), Long.toString(projectRoleId));
    }

    /**
     * Add the project lead permission type to a permission
     * @param schemeId of the permission scheme
     * @param permission to add project lead permission to
     */
    public void addProjectLeadPermission(long schemeId, ProjectPermissionKey permission)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.PROJECT_LEAD.getKey());
    }

    /**
     * Add the project lead permission type to a permission
     * @param schemeId of the permission scheme
     * @param permission to remove project lead permission from
     */
    public void removeProjectLeadPermission(long schemeId, ProjectPermissionKey permission)
    {
        removePermission(schemeId, permission, JiraPermissionHolderType.PROJECT_LEAD.getKey());
    }
    /**
     * Remove all current permission types and add the project lead permission type to a permission
     * @param schemeId of the permission scheme
     * @param permission to replace project lead permission with
     */
    public void replaceProjectLeadPermission(long schemeId, ProjectPermissionKey permission)
    {
        replacePermissions(schemeId, permission, JiraPermissionHolderType.PROJECT_LEAD.getKey());
    }

    /**
     * Adds the given permission to the anonymous group ("anyone").
     *
     * @param schemeId the scheme ID
     * @param permission the permission to add
     */
    public void addEveryonePermission(Long schemeId, ProjectPermissionKey permission)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.GROUP.getKey());
    }

    public void addUserCustomFieldPermission(Long schemeId, ProjectPermissionKey permission, String fieldId)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.USER_CUSTOM_FIELD.getKey(), fieldId);
    }

    public void addGroupCustomFieldPermission(Long schemeId, ProjectPermissionKey permission, String fieldId)
    {
        addPermission(schemeId, permission, JiraPermissionHolderType.GROUP_CUSTOM_FIELD.getKey(), fieldId);
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
        get(createResource().path("permissionSchemes/entity/remove")
                .queryParam("schemeId", schemeId.toString())
                .queryParam("permission", permission.toString())
                .queryParam("type", JiraPermissionHolderType.GROUP.getKey()));
    }


    /**
     * See {@link PermissionSchemesControl#getPermissionEntries(java.lang.Long, com.atlassian.jira.security.plugin.ProjectPermissionKey, java.lang.String, java.lang.String)}
     */
    public PermissionAssignments getPermissionEntries(@Nonnull final Long schemeId)
    {
        return getPermissionEntries(schemeId, null, null, null);
    }

    /**
     * See {@link PermissionSchemesControl#getPermissionEntries(java.lang.Long, com.atlassian.jira.security.plugin.ProjectPermissionKey, java.lang.String, java.lang.String)}
     */
    public PermissionAssignments getPermissionEntries(@Nonnull final Long schemeId, @Nonnull final String type)
    {
        return getPermissionEntries(schemeId, null, type, null);
    }


    /**
     * See {@link PermissionSchemesControl#getPermissionEntries(java.lang.Long, com.atlassian.jira.security.plugin.ProjectPermissionKey, java.lang.String, java.lang.String)}
     */
    public PermissionAssignments getPermissionEntries(@Nonnull final Long schemeId, @Nonnull final ProjectPermissionKey permission,
                                           @Nonnull final String type)
    {
        return getPermissionEntries(schemeId, permission, type, null);
    }


    /**
     * See {@link PermissionSchemesControl#getPermissionEntries(java.lang.Long, com.atlassian.jira.security.plugin.ProjectPermissionKey, java.lang.String, java.lang.String)}
     */
    public PermissionAssignments getPermissionEntries(@Nonnull final Long schemeId, @Nonnull final String type,
                                           @Nonnull final String parameter)
    {
        return getPermissionEntries(schemeId, null, type, parameter);
    }

    /**
     * Get all the permission entries for a given scheme.
     * See {com.atlassian.jira.testkit.plugin.PermissionSchemesBackdoor#getCurrentlyAssigned(long, java.lang.String, java.lang.String, java.lang.String)}
     * for more details
     * @param schemeId of the permission scheme
     * @param permission key to find assigned permissions for
     * @param type of permission, e.g. user, group
     * @param parameter any specific value for a type, e.g. jira-developers
     * @return JSONObject representing permissions
     */
    public PermissionAssignments getPermissionEntries(@Nonnull final Long schemeId, @Nullable final ProjectPermissionKey permission,
                                           @Nullable final String type, @Nullable final String parameter)
    {
        String result = get(buildGetPermissionEntriesRequest(schemeId, permission, type, parameter));
        return parseGetPermissionEntriesResult(result);
    }

    private WebResource buildGetPermissionEntriesRequest(@Nonnull Long schemeId, @Nullable final ProjectPermissionKey permission,
                                                         @Nullable final String type, @Nullable final String parameter)
    {
        WebResource resource = createResource().path("permissionSchemes/entity/assigned");
        resource = resource.queryParam("schemeId", "" + schemeId);
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

    private PermissionAssignments parseGetPermissionEntriesResult(String result)
    {
        try {
            return new PermissionAssignments(new JSONObject(result));
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }


    private void addPermission(long schemeId, int permission, String type, String parameter)
    {
        get(createResource().path("permissionSchemes/legacy/entity/add")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission)
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    private void addPermission(long schemeId, ProjectPermissionKey permission, String type, String parameter)
    {
        get(createResource().path("permissionSchemes/entity/add")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission.permissionKey())
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    private void addPermission(long schemeId, ProjectPermissionKey permission, String type)
    {
        get(createResource().path("permissionSchemes/entity/add")
                    .queryParam("schemeId", "" + schemeId)
                    .queryParam("permission", "" + permission.permissionKey())
                    .queryParam("type", type)
        );
    }

    private void removePermission(long schemeId, ProjectPermissionKey permission, String type)
    {
        get(createResource().path("permissionSchemes/legacy/entity/remove")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission.permissionKey())
                .queryParam("type", type));
    }

    private void removePermission(long schemeId, int permission, String type, String parameter)
    {
        get(createResource().path("permissionSchemes/legacy/entity/remove")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission)
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    private void removePermission(long schemeId, ProjectPermissionKey permission, String type, String parameter)
    {
        get(createResource().path("permissionSchemes/entity/remove")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission.permissionKey())
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    private void replacePermissions(long schemeId, ProjectPermissionKey permission, String type)
    {
        get(createResource().path("permissionSchemes/entity/replace")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission.permissionKey())
                .queryParam("type", type));
    }

    private void replacePermissions(long schemeId, int permission, String type, String parameter)
    {
        get(createResource().path("permissionSchemes/legacy/entity/replace")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission)
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    private void replacePermissions(long schemeId, ProjectPermissionKey permission, String type, String parameter)
    {
        get(createResource().path("permissionSchemes/entity/replace")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission.permissionKey())
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    public class PermissionAssignments
    {
        private final Map<String, Map<String, List<String>>> assignments;

        public PermissionAssignments(JSONObject permissionJson)
        {
            assignments = Maps.newHashMap();
            permissionJson.keys().forEachRemaining(permission -> {
                try {

                    JSONObject permissionTypes = permissionJson.getJSONObject(permission);
                    assignments.put(permission, createTypeAssignments(permissionTypes));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });
        }

        /**
         * Given a JAVA style format for a specific JSON of format:
         * {
         *     BROWSE_USERS: ["admin", "dev"],
         *     ASSIGN_ISSUES: ["admin"]
         * }
         *
         * @param typeAssignmentsJson to create java mapping from
         * @return java mapping of json
         */
        private Map<String, List<String>> createTypeAssignments(JSONObject typeAssignmentsJson) {
            Map<String, List<String>> typeAssignments = Maps.newHashMap();

            typeAssignmentsJson.keys().forEachRemaining(type -> {
                try {
                    JSONArray assignmentsArray = typeAssignmentsJson.getJSONArray(type);
                    typeAssignments.put(type, createSingleTypeAssignment(assignmentsArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException("error generating permission assignments", e);
                }
            });
            return typeAssignments;
        }

        /**
         * Given a JAVA style format for a specific JSON array of strings, e.g.
         * ["admin", "dev"]
         *
         * @param singleTypeAssignments to create java array list from
         * @return java list from json array
         */
        private List<String> createSingleTypeAssignment(JSONArray singleTypeAssignments) {
            if (singleTypeAssignments.length() > 0) {
                List<String> permissionTypeAssignmentList = Lists.newArrayList();
                for (int i = 0; i < singleTypeAssignments.length(); ++i) {
                    try {
                        permissionTypeAssignmentList.add(singleTypeAssignments.getString(i));
                    } catch (JSONException e) {
                        throw new RuntimeException("error generating permission assignments", e);
                    }
                }
                return permissionTypeAssignmentList;
            }
            return Lists.newArrayList();
        }

        /**
         * Determine if the permission scheme has a given permission
         * @return if specified permission is found
         */
        public boolean hasPermission(final String permission)
        {
            return assignments.containsKey(permission);
        }

        /**
         * Find whether a given type is found in the permission
         * @param permission to look in
         * @param type to find, e.g. "users"
         * @return if type is found in permission.
         */
        public boolean hasType(final String permission, final String type) {
            return hasPermission(permission) && assignments.get(permission).containsKey(type);
        }

        /**
         * Determine if a given permission assignment of type for a permission is present
         * @param permission that assignment is in
         * @param type of assignment
         * @param parameter value for assignment
         * @return if found
         */
        public boolean hasPermissionAssignment(final String permission, final String type, final String parameter)
        {
            return hasType(permission, type) && assignments.get(permission).get(type).contains(parameter);
        }

        public int length() {
            return assignments.size();
        }
    }
}
