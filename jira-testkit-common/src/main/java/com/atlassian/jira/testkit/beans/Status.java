package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonProperty;

public class Status
{
    @JsonProperty 
    private String id;

    @JsonProperty 
    private String description;

    @JsonProperty 
    private String iconUrl;

    @JsonProperty 
    private String name;


    public Status()
    {
    }
    
    public Status(String id, String name, String description, String iconUrl)
    {
        this.id = id;
        this.description = description;
        this.iconUrl = iconUrl;
        this.name = name;
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

    public String getIconUrl()
    {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}