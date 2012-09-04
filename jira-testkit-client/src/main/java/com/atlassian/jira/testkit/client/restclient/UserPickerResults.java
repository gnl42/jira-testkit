package com.atlassian.jira.testkit.client.restclient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

@JsonSerialize (include = JsonSerialize.Inclusion.NON_NULL)
public class UserPickerResults
{
    public String header;
    public List<UserPickerUser> users;
    public Integer total;

    public UserPickerResults header(final String header)
    {
        this.header = header;
        return this;
    }

    public UserPickerResults total(final Integer total)
    {
        this.total = total;
        return this;
    }

    public UserPickerResults users(final List<UserPickerUser> users)
    {
        this.users = users;
        return this;
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

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
