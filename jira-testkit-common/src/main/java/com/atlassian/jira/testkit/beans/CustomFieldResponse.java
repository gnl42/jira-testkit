package com.atlassian.jira.testkit.beans;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@JsonIgnoreProperties (ignoreUnknown = true)
public class CustomFieldResponse implements Named
{
    @JsonProperty
    public String name;

    @JsonProperty
    public String id;

    @JsonProperty
    public String type;

    @JsonProperty
    public String description;

    @JsonProperty
    public String searcher;

    //Don't set this to an empty list by default or old clients wont be compatible with the GET call.
    private List<CustomFieldConfig> config;

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

    @JsonProperty
    public List<CustomFieldConfig> getConfig()
    {
        return config;
    }

    public void setConfig(final List<CustomFieldConfig> config)
    {
        this.config = config;
    }
}