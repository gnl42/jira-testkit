package com.atlassian.jira.testkit.client;

/**
 * @since v6.4
 */
public class UserHistoryControl extends BackdoorControl<UserHistoryControl>
{
    public UserHistoryControl(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void addIssue(final String user, final String issueKey)
    {
        get(createResource().path("userhistory/issue/add")
                .queryParam("user", user)
                .queryParam("key", issueKey));
    }

    public void addJQLQuery(final String user, final String query)
    {
        get(createResource().path("userhistory/jqlquery/add")
                .queryParam("user", user)
                .queryParam("query", query));
    }
}
