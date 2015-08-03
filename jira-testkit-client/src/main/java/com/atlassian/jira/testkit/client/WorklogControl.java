package com.atlassian.jira.testkit.client;

/**
 * @since v7.0
 */
public class WorklogControl extends BackdoorControl<WorklogControl>
{
    /**
     * Creates a new BackdoorControl.
     *
     * @param environmentData a JIRAEnvironmentData
     */
    public WorklogControl(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void setWorklogUpdatedTimestamp(final Long worklogId, final Long updatedTime)
    {
        createResource().path("worklog")
                .path("updatedtime")
                .queryParam("worklogId", worklogId.toString())
                .queryParam("timestamp", updatedTime.toString())
                .put();
    }

    public void setWorklogDeletedTimestamp(final Long worklogId, final Long updatedTime)
    {
        createResource().path("worklog")
                .path("deletedtime")
                .queryParam("worklogId", worklogId.toString())
                .queryParam("timestamp", updatedTime.toString())
                .put();
    }
}
