package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties (ignoreUnknown = true)
public class StatusCategory
{
    @JsonProperty
    private String self;

    @JsonProperty
    private Long id;

    @JsonProperty
    private String key;

    @JsonProperty
    private String colorName;

    @JsonProperty
    private String name;

    public StatusCategory()
    {
    }

    public StatusCategory(String self, Long id, String key, String colorName, final String name)
    {
        this.self = self;
        this.id = id;
        this.key = key;
        this.colorName = colorName;
        this.name = name;
    }

    public String self()
    {
        return self;
    }

    public StatusCategory self(String self)
    {
        return new StatusCategory(self, id, key, colorName, name);
    }

    public Long id()
    {
        return id;
    }

    public StatusCategory id(Long id)
    {
        return new StatusCategory(self, id, key, colorName, name);
    }

    public String key()
    {
        return key;
    }

    public StatusCategory key(String key)
    {
        return new StatusCategory(self, id, key, colorName, name);
    }

    public String colorName()
    {
        return colorName;
    }

    public StatusCategory colorName(String colorName)
    {
        return new StatusCategory(self, id, key, colorName, name);
    }

    public String name()
    {
        return name;
    }

    public StatusCategory name(String name)
    {
        return new StatusCategory(self, id, key, colorName, name);
    }

    @Override
    public boolean equals(Object o)
    {
        return EqualsBuilder.reflectionEquals(this, o);
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