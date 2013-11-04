package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonProperty;

public class Priority
{
    @JsonProperty 
    private String id;

    @JsonProperty 
    private String description;

    @JsonProperty 
    private String name;

    @JsonProperty 
    private String color;

    @JsonProperty 
    private String iconUrl;

    @JsonProperty
    private Long sequence;

    public Priority()
    {
    }

    public Priority(String id, String description, String name, Long sequence, String color, String iconUrl) 
    {
        this.id = id;
        this.description = description;
        this.name = name;
        this.sequence = sequence;
        this.color = color;
        this.iconUrl = iconUrl;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Long getSequence()
    {
        return sequence;
    }

    public void setSequence(Long sequence)
    {
        this.sequence = sequence;
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
    }

}