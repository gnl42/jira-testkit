package com.atlassian.jira.testkit.beans;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CustomFieldResponse implements Named
{
    public String name;
    public String id;
    public String type;
    public String description;
    public String searcher;

    public CustomFieldResponse(final String name, final String id, final String type, final String description, final String searcher)
    {
        this.description = description;
        this.id = id;
        this.name = name;
        this.searcher = searcher;
        this.type = type;
    }

    public CustomFieldResponse(String name, String id, String type)
    {
        this(name, id, type, null, null);
    }

    public CustomFieldResponse()
    {
    }

    @Override
    public boolean equals(final Object o)
    {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String getName()
    {
        return name;
    }
}