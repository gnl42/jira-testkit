package com.atlassian.jira.testkit.client.restclient;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.net.URI;
import java.util.List;

@JsonAutoDetect
public class PageBean<T>
{
    private URI self;
    private URI nextPage;
    private int maxResults;
    private long startAt;
    private long total;
    private Boolean isLast;
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

    public PageBean(final URI self, final URI nextPage, final int maxResults, final long startAt, final long total, final Boolean isLast, final List<T> values)
    {
        this.self = self;
        this.nextPage = nextPage;
        this.maxResults = maxResults;
        this.startAt = startAt;
        this.total = total;
        this.isLast = isLast;
        this.values = values;
    }

    public URI getSelf()
    {
        return self;
    }

    public void setSelf(final URI self)
    {
        this.self = self;
    }

    public URI getNextPage()
    {
        return nextPage;
    }

    public void setNextPage(final URI nextPage)
    {
        this.nextPage = nextPage;
    }

    public int getMaxResults()
    {
        return maxResults;
    }

    public void setMaxResults(final int maxResults)
    {
        this.maxResults = maxResults;
    }

    public long getStartAt()
    {
        return startAt;
    }

    public void setStartAt(final long startAt)
    {
        this.startAt = startAt;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(final long total)
    {
        this.total = total;
    }

    public Boolean getIsLast()
    {
        return isLast;
    }

    public void setIsLast(final Boolean isLast)
    {
        this.isLast = isLast;
    }

    public List<T> getValues()
    {
        return values;
    }

    public void setValues(final List<T> values)
    {
        this.values = values;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PageBean that = (PageBean) o;

        return Objects.equal(this.self, that.self) &&
                Objects.equal(this.nextPage, that.nextPage) &&
                Objects.equal(this.maxResults, that.maxResults) &&
                Objects.equal(this.startAt, that.startAt) &&
                Objects.equal(this.total, that.total) &&
                Objects.equal(this.isLast, that.isLast) &&
                Objects.equal(this.values, that.values);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(self, nextPage, maxResults, startAt, total, isLast,
                values);
    }
}
