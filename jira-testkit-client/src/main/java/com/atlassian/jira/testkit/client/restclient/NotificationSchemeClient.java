package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.WebResource;

public class NotificationSchemeClient extends RestApiClient<NotificationSchemeClient>
{
    public NotificationSchemeClient(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public NotificationSchemeBean getNotificationScheme(Long notificationSchemeId, final String expand)
    {
        WebResource webResource = createResource()
                .path("notificationscheme")
                .path(notificationSchemeId.toString());
        if (expand != null)
        {
            webResource = webResource.queryParam("expand", expand);
        }
        return webResource.get(NotificationSchemeBean.class);
    }
}
