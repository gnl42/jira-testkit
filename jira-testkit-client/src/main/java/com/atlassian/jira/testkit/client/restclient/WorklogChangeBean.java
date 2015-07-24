package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class WorklogChangeBean
{
    private Long worklogId;
    private Long updatedTime;

    public WorklogChangeBean()
    {
    }

    public WorklogChangeBean(final Long worklogId, final Long updatedTime)
    {
        this.worklogId = worklogId;
        this.updatedTime = updatedTime;
    }

    public Long getWorklogId()
    {
        return worklogId;
    }

    public Long getUpdatedTime()
    {
        return updatedTime;
    }
}
