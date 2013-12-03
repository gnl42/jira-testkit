package com.atlassian.jira.testkit.client.restclient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

/**
 * Representation of an issue properties keys list in the JIRA REST API.
 * @since v6.2
 */
public class IssuePropertyKeys
{
    public List<IssuePropertyKey> keys;

    public IssuePropertyKeys keys(List<IssuePropertyKey> keys)
    {
        this.keys = keys;
        return this;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public static class IssuePropertyKey
    {
        public String self;
        public String key;

        IssuePropertyKey self(String self)
        {
            this.self = self;
            return this;
        }

        IssuePropertyKey key(String key)
        {
            this.key = key;
            return this;
        }

        @Override
        public String toString()
        {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }

        @Override
        public boolean equals(Object obj)
        {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        @Override
        public int hashCode()
        {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }
}
