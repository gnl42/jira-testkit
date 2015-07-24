package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

@JsonSerialize
public class WorklogsBean
{
    public Collection<Worklog> worklogs;
}
