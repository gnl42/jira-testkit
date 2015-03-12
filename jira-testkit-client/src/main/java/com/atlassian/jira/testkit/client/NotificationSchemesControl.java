/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing notifications and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.notification.type.NotificationType;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Notification Schemes.
 *
 * See com.atlassian.jira.testkit.plugin.notificationSchemesBackdoor in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class NotificationSchemesControl extends BackdoorControl<NotificationSchemesControl>
{
    public NotificationSchemesControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * Makes a copy of the Default Notification Scheme and returns the id of the new scheme.
     * @param schemeName the name of the new scheme
     * @return {Long} the schemeId of the created scheme
     */
    public Long copyDefaultScheme(String schemeName)
    {
        return Long.parseLong(createResource().path("notificationSchemes/copyDefault")
                .queryParam("schemeName", schemeName).get(String.class));
    }

    /**
     * Makes a copy of the Default Notification Scheme and returns the id of the new scheme.
     * @param schemeName the name of the new scheme
     * @param description can be null
     * @return {Long} the schemeId of the created scheme
     */
    public Long createScheme(String schemeName, String description)
    {
        return Long.parseLong(createResource().path("notificationSchemes/create")
                .queryParam("schemeName", schemeName).queryParam("schemeDescription", description).get(String.class));
    }

    public void deleteScheme(long schemeId)
    {
        createResource().path("notificationSchemes").path(String.valueOf(schemeId)).delete();
    }

    public void addGroupNotification(Long schemeId, long eventTypeId, String groupName)
    {
        addNotification(schemeId, eventTypeId, NotificationType.GROUP.dbCode(), groupName);
    }

    public void removeGroupNotification(long schemeId, long eventTypeId, String groupName)
    {
        removeNotification(schemeId, eventTypeId, NotificationType.GROUP.dbCode(), groupName);
    }

    public void addProjectRoleNotification(long schemeId, long eventTypeId, long projectRoleId)
    {
        addNotification(schemeId, eventTypeId, NotificationType.PROJECT_ROLE.dbCode(), Long.toString(projectRoleId));
    }

    public void removeProjectRoleNotification(long schemeId, long eventTypeId, long projectRoleId)
    {
        removeNotification(schemeId, eventTypeId, NotificationType.PROJECT_ROLE.dbCode(), Long.toString(projectRoleId));
    }

    public void addUserNotification(long schemeId, long eventTypeId, String userName)
    {
        addNotification(schemeId, eventTypeId, NotificationType.SINGLE_USER.dbCode(), userName);
    }

    public void removeUserNotification(long schemeId, long eventTypeId, String userName)
    {
        removeNotification(schemeId, eventTypeId, NotificationType.SINGLE_USER.dbCode(), userName);
    }

    public void addEmailNotification(long schemeId, long eventTypeId, String email)
    {
        addNotification(schemeId, eventTypeId, NotificationType.SINGLE_EMAIL_ADDRESS.dbCode(), email);
    }

    public void removeEmailNotification(long schemeId, long eventTypeId, String email)
    {
        removeNotification(schemeId, eventTypeId, NotificationType.SINGLE_EMAIL_ADDRESS.dbCode(), email);
    }

    public void addUserCustomField(long schemeId, long eventTypeId, String customFieldId)
    {
        addNotification(schemeId, eventTypeId, NotificationType.USER_CUSTOM_FIELD_VALUE.dbCode(), customFieldId);
    }

    public void removeUserCustomField(long schemeId, long eventTypeId, String customFieldId)
    {
        removeNotification(schemeId, eventTypeId, NotificationType.USER_CUSTOM_FIELD_VALUE.dbCode(), customFieldId);
    }
    /**
     * Removes any matching notification scheme entities for the given notification and adds an entity for the passed group.
     */
    public void replaceGroupNotifications(long schemeId, long eventTypeId, String groupName)
    {
        replaceNotifications(schemeId, eventTypeId, NotificationType.GROUP.dbCode(), groupName);
    }

    private void addNotification(long schemeId, long eventTypeId, String type, String parameter)
    {
        get(createResource().path("notificationSchemes/entity/add")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("eventTypeId", "" + eventTypeId)
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    private void removeNotification(long schemeId, long eventTypeId, String type, String parameter)
    {
        get(createResource().path("notificationSchemes/entity/remove")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("eventTypeId", "" + eventTypeId)
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }

    private void replaceNotifications(long schemeId, long eventTypeId, String type, String parameter)
    {
        get(createResource().path("notificationSchemes/entity/replace")
                .queryParam("schemeId", "" + schemeId)
                .queryParam("eventTypeId", "" + eventTypeId)
                .queryParam("type", type)
                .queryParam("parameter", parameter));
    }
}
