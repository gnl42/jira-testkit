package com.atlassian.jira.testkit.plugin.beanbuilders;

import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.testkit.beans.PermissionSchemeBean;

/**
 * Builds a {@link com.atlassian.jira.testkit.beans.PermissionSchemeBean}.
 *
 * @since 6.3
 */
public class PermissionSchemeBeanFactory
{
    private PermissionSchemeBeanFactory()
    {
    }

    public static PermissionSchemeBean toPermissionSchemeBean(Scheme notificationScheme)
    {
        if (notificationScheme != null)
        {
            return new PermissionSchemeBean(notificationScheme.getId(), notificationScheme.getName());
        }
        return null;
    }
}
