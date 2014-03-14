package com.atlassian.jira.testkit.plugin.beanbuilders;

import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.testkit.beans.NotificationSchemeBean;

/**
 * Builds a {@link com.atlassian.jira.testkit.beans.NotificationSchemeBean}.
 *
 * @since 6.3
 */
public class NotificationSchemeBeanFactory
{
    private NotificationSchemeBeanFactory()
    {
    }

    public static NotificationSchemeBean toNotificationSchemeBean(Scheme notificationScheme)
    {
        if (notificationScheme != null)
        {
            return new NotificationSchemeBean(notificationScheme.getId(),notificationScheme.getName());
        }
        return null;
    }
}
