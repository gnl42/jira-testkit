package com.atlassian.jira.testkit.beans;

import com.sun.jersey.api.client.GenericType;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@JsonIgnoreProperties (ignoreUnknown = true)
public class ProjectTypeBean
{
    public static final GenericType<List<ProjectTypeBean>> LIST_TYPE = new GenericType<List<ProjectTypeBean>>(){};

    @JsonProperty
    private String key;
    @JsonProperty
    private String descriptionI18nKey;
    @JsonProperty
    private String icon;
    @JsonProperty
    private String color;

    public String getKey()
    {
        return key;
    }

    public String getDescriptionI18nKey()
    {
        return descriptionI18nKey;
    }

    public String getIcon()
    {
        return icon;
    }

    public String getColor()
    {
        return color;
    }
}
