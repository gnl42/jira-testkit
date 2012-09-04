package com.atlassian.jira.testkit.client.restclient;

import java.util.List;

public class WorklogWithPaginationBean
{
    public Integer startAt;
    public Integer maxResults;
    public Integer total;
    public List<Worklog> worklogs;
}
