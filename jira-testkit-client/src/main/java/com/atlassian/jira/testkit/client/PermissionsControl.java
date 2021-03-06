/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.GenericType;

import java.util.List;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Permissions, including global permissions
 * and permission schemes.
 *
 * See <code>com.atlassian.jira.testkit.plugin.PermissionsBackdoor</code> in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class PermissionsControl extends BackdoorControl<PermissionsControl>
{
    private static final GenericType<List<String>> LIST_GENERIC_TYPE = new GenericType<List<String>>()
    {
    };

    public PermissionsControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void addGlobalPermission(final int permissionType, final String group)
    {
        get(createResource().path("permissions/global/add")
                .queryParam("type", "" + permissionType)
                .queryParam("group", group));
    }

    public String getPermissions(int permissionType)
    {
        return get(createResource().path("permissions/global").queryParam("type", String.valueOf(permissionType)));
    }

    public void addAnyoneGlobalPermission(final int permissionType)
    {
        get(createResource().path("permissions/global/add").queryParam("type",
                "" + permissionType));
    }

    public void removeGlobalPermission(final int permissionType, final String group)
    {
        get(createResource().path("permissions/global/remove")
                .queryParam("type", "" + permissionType)
                .queryParam("group", group));
    }
    
    public void removeAnyoneGlobalPermission(final int permissionType)
    {
        get(createResource().path("permissions/global/remove")
                .queryParam("type", "" + permissionType));
    }

    public List<String> getGlobalPermissionGroups(final int permissionType)
    {
        return createResource().path("permissions/global/getgroups")
                .queryParam("type", "" + permissionType).get(LIST_GENERIC_TYPE);
    }
}
