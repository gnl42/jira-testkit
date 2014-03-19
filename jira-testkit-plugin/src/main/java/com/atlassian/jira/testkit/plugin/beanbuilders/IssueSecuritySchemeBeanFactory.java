package com.atlassian.jira.testkit.plugin.beanbuilders;

import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.testkit.beans.IssueSecuritySchemeBean;

/**
 * Builds a {@link com.atlassian.jira.testkit.beans.IssueSecuritySchemeBean}.
 *
 * @since 6.3
 */
public class IssueSecuritySchemeBeanFactory
{
    private Long id;
    private String name;

    private IssueSecuritySchemeBeanFactory()
    {
    }

    public static IssueSecuritySchemeBean toIssueSecuritySchemeBean(final Scheme issueSecurityScheme)
    {
        if (issueSecurityScheme != null)
        {
            return new IssueSecuritySchemeBean(issueSecurityScheme.getId(), issueSecurityScheme.getName());
        }
        return null;
    }
}
