package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class PageBean<T>
{
    @JsonProperty
    private int maxResults;
    @JsonProperty
    private long startAt;
    @JsonProperty
    private long total;
    @JsonProperty
    private List<T> values;

    public PageBean()
    {
    }

    public PageBean(final List<T> values, final long total, final int maxResults, final long startAt)
    {
        this.values = values;
        this.total = total;
        this.maxResults = maxResults;
        this.startAt = startAt;
    }

    public long getStartAt()
    {
        return startAt;
    }

    public int getMaxResults()
    {
        return maxResults;
    }

    public long getTotal()
    {
        return total;
    }

    public List<T> getValues()
    {
        return values;
    }
}
