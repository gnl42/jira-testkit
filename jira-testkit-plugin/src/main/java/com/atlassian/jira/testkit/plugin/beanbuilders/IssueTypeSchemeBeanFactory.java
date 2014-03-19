package com.atlassian.jira.testkit.plugin.beanbuilders;

import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.testkit.beans.IssueTypeSchemeBean;

/**
 * Builds a {@link com.atlassian.jira.testkit.beans.IssueTypeSchemeBean}. Has some smarts around when built with a null scheme.
 *
 * @since 6.3
 */
public class IssueTypeSchemeBeanFactory
{
    private IssueTypeSchemeBeanFactory()
    {
    }

    public static IssueTypeSchemeBean toIssueTypeSchemeBean(FieldConfigScheme issueTypeScheme)
    {
        Long id;
        String name;
        if (issueTypeScheme == null)
        {
            id = null;
            name = "Default Issue Type Scheme";
        }
        else
        {
            id = issueTypeScheme.getId();
            name = issueTypeScheme.getName();
        }
        return new IssueTypeSchemeBean(id, name);
    }
}
