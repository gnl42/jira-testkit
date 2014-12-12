/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.security.plugin.ProjectPermissionKey;

import java.lang.Long;
import java.lang.String;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Permission Schemes.
 *
 * See com.atlassian.jira.testkit.plugin.PermissionSchemesBackdoor in jira-testkit-plugin for backend.
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
     * @deprecated Use {@link PermissionSchemesControl#addGroupPermission(Long, ProjectPermissionKey, String)} instead
     */
    public void addGroupPermission(Long schemeId, int permission, String groupName)
    {
        addPermission(schemeId, permission, "group", groupName);
    }

    public void addGroupPermission(Long schemeId, ProjectPermissionKey permission, String groupName)
    {
        addPermission(schemeId, permission, "group", groupName);
    }

    /**
     * @deprecated Use {@link PermissionSchemesControl#removeGroupPermission(long, ProjectPermissionKey, String)} instead
     */
    public void removeGroupPermission(long schemeId, int permission, String groupName)
    {
        removePermission(schemeId, permission, "group", groupName);
    }

    public void removeGroupPermission(long schemeId, ProjectPermissionKey permission, String groupName)
    {
        removePermission(schemeId, permission, "group", groupName);
    }

    /**
     * @deprecated Use {@link PermissionSchemesControl#addProjectRolePermission(long, ProjectPermissionKey, long)} instead
     */
    public void addProjectRolePermission(long schemeId, int permission, long projectRoleId)
    {
        addPermission(schemeId, permission, "projectrole", Long.toString(projectRoleId));
    }

    public void addProjectRolePermission(long schemeId, ProjectPermissionKey permission, long projectRoleId)
    {
        addPermission(schemeId, permission, "projectrole", Long.toString(projectRoleId));
    }

    /**
     * @deprecated Use {@link PermissionSchemesControl#removeProjectRolePermission(long, ProjectPermissionKey, long)} instead
     */
    public void removeProjectRolePermission(long schemeId, int permission, long projectRoleId)
    {
        removePermission(schemeId, permission, "projectrole", Long.toString(projectRoleId));
    }

    public void removeProjectRolePermission(long schemeId, ProjectPermissionKey permission, long projectRoleId)
    {
        removePermission(schemeId, permission, "projectrole", Long.toString(projectRoleId));
    }

    /**
     * @deprecated Use {@link PermissionSchemesControl#addUserPermission(long, ProjectPermissionKey, String)} instead
     */
    public void addUserPermission(long schemeId, int permission, String userName)
    {
        addPermission(schemeId, permission, "user", userName);
    }

    public void addUserPermission(long schemeId, ProjectPermissionKey permission, String userName)
    {
        addPermission(schemeId, permission, "user", userName);
    }

    /**
     * @deprecated Use {@link PermissionSchemesControl#removeUserPermission(long, ProjectPermissionKey, String)} instead
     */
    public void removeUserPermission(long schemeId, int permission, String userName)
    {
        removePermission(schemeId, permission, "user", userName);
    }

    public void removeUserPermission(long schemeId, ProjectPermissionKey permission, String userName)
    {
        removePermission(schemeId, permission, "user", userName);
    }

    /**
     * Removes any matching permission scheme entities for the given permission and adds an entity for the passed group.
     * @deprecated Use {@link PermissionSchemesControl#replaceGroupPermissions(long, ProjectPermissionKey, String)} instead
     */
    public void replaceGroupPermissions(long schemeId, int permission, String groupName)
    {
        replacePermissions(schemeId, permission, "group", groupName);
    }

    public void replaceGroupPermissions(long schemeId, ProjectPermissionKey permission, String groupName)
    {
        replacePermissions(schemeId, permission, "group", groupName);
    }

    /**
     * Adds the given permission to the anonymous group ("anyone").
     */
    public void addEveryonePermission(Long schemeId, ProjectPermissionKey permission)
    {
        addPermission(schemeId, permission, "group");
    }

    /**
     * Removes only the given permission from the anonymous group ("anyone"). Other groups with this permission
     * will not be removed.
     */
    public void removeEveryonePermission(Long schemeId, ProjectPermissionKey permission)
    {
        get(createResource().path("permissionSchemes/entity/remove")
                .queryParam("schemeId", schemeId.toString())
                .queryParam("permission", permission.toString())
                .queryParam("type", "group"));
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
}
