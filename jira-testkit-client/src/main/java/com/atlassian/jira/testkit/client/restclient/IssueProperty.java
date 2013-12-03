package com.atlassian.jira.testkit.client.restclient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Representation of an issue property in the JIRA REST API.
 *
 * @since v6.2
 */
public class IssueProperty
{
    public String self;
    public String key;
    public String value;

    public IssueProperty self(String self)
    {
        this.self = self;
        return this;
    }

    public IssueProperty key(String key)
    {
        this.key = key;
        return this;
    }

    public IssueProperty value(String value)
    {
        this.value = value;
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
