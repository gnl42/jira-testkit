package com.atlassian.jira.testkit.client.restclient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.map.annotate.JsonSerialize;


@JsonSerialize (include = JsonSerialize.Inclusion.NON_NULL)
public class GroupAndUserSuggestions
{
    public GroupSuggestions groups;
    public UserSuggestions users;


    public GroupAndUserSuggestions groups(final GroupSuggestions groupSuggestions)
    {
        this.groups = groupSuggestions;
        return this;
    }

    public GroupAndUserSuggestions users(final UserSuggestions userSuggestions)
    {
        this.users = userSuggestions;
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

