package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.issue.fields.rest.json.beans.WorklogJsonBean;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.net.URI;
import java.util.List;

@JsonSerialize
public class WorklogSincePage
{
    public URI self;
    public URI nextPage;
    public List<WorklogJsonBean> values;
    public Long since;
    public Long until;
    public boolean isLast;
}