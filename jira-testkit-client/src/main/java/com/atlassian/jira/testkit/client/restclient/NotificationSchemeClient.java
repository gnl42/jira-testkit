package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;

public class NotificationSchemeClient extends RestApiClient<NotificationSchemeClient>
{
    public NotificationSchemeClient(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public NotificationSchemeBean getNotificationScheme(Long notificationSchemeId)
    {
        return createResource()
                .path("notificationscheme")
                .path(notificationSchemeId.toString())
                .get(NotificationSchemeBean.class);
    }
}
