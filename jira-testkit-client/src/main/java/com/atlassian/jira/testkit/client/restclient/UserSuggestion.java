package com.atlassian.jira.testkit.client.restclient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.net.URI;

@JsonSerialize (include = JsonSerialize.Inclusion.NON_NULL)
public class UserSuggestion
{
    public String name;
    public String key;
    public String html;
    public String displayName;
    public URI avatarUrl;

    public UserSuggestion name(final String name)
    {
        this.name = name;
        return this;
    }

    public UserSuggestion key(final String key)
    {
        this.key = key;
        return this;
    }

    public UserSuggestion displayName(final String displayName)
    {
        this.displayName = displayName;
        return this;
    }

    public UserSuggestion html(final String html)
    {
        this.html = html;
        return this;
    }

    public UserSuggestion avatarUrl(final URI avatarUrl)
    {
        this.avatarUrl = avatarUrl;
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
