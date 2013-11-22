package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Permission Schemes.
 * <p/>
 * See PermissionSchemesBackdoor for the code this plugs into at the back-end.
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
        return getId(createResource().path("permissionSchemes/copyDefault")
                .queryParam("schemeName", schemeName));
    }

    public void addGroupPermission(Long schemeId, int permission, String groupName)
    {
        addPermission(schemeId, permission, "group", groupName);
    }

    public void addPermissionForEveryone(long schemeId, int permission)
    {
        addPermission(schemeId, permission, "group", null);
    }

    public void removeGroupPermission(long schemeId, int permission, String groupName)
    {
        removePermission(schemeId, permission, "group", groupName);
    }

    public void addProjectRolePermission(long schemeId, int permission, long projectRoleId)
    {
        addPermission(schemeId, permission, "projectrole", Long.toString(projectRoleId));
    }

    public void removeProjectRolePermission(long schemeId, int permission, long projectRoleId)
    {
        removePermission(schemeId, permission, "projectrole", Long.toString(projectRoleId));
    }

    /**
     * Removes any matching permission scheme entities for the given permission and adds an entity for the passed group.
     */
    public void replaceGroupPermissions(long schemeId, int permission, String groupName)
    {
        replacePermissions(schemeId, permission, "group", groupName);
    }

    private void addPermission(long schemeId, int permission, String type, String parameter)
    {
        get(createResource().path("permissionSchemes/entity/add")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission)
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    private void removePermission(long schemeId, int permission, String type, String parameter)
    {
        get(createResource().path("permissionSchemes/entity/remove")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission)
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    private void replacePermissions(long schemeId, int permission, String type, String parameter)
    {
        get(createResource().path("permissionSchemes/entity/replace")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("permission", "" + permission)
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }
}
