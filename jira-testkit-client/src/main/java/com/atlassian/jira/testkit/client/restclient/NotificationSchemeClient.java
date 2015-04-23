package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.WebResource;

import java.util.List;

public class NotificationSchemeClient extends RestApiClient<NotificationSchemeClient>
{
    public NotificationSchemeClient(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public NotificationSchemeBean getNotificationScheme(Long notificationSchemeId, final String expand)
    {
        WebResource webResource = getWebResource(expand)
                .path(notificationSchemeId.toString());

        return webResource.get(NotificationSchemeBean.class);
    }

    public NotificationSchemePageBean getNotificationSchemes(final Integer startAt,
            final Integer maxResults,
            final String expand)
    {
        WebResource webResource = getWebResource(expand);
        if (startAt != null)
        {
            webResource = webResource.queryParam("startAt", startAt.toString());
        }
        if (maxResults != null)
        {
            webResource = webResource.queryParam("maxResults", maxResults.toString());
        }
        return webResource.get(NotificationSchemePageBean.class);
    }

    private WebResource getWebResource(String expand)
    {
        WebResource webResource = createResource()
                .path("notificationscheme");
        if (expand != null)
        {
            webResource = webResource.queryParam("expand", expand);
        }
        return webResource;
    }
}
