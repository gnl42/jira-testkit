package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonProperty;

public class Resolution
{
    @JsonProperty 
    private String id;

    @JsonProperty 
    private String description;

    @JsonProperty 
    private String name;

    @JsonProperty
    private Long sequence;

    public Resolution()
    {
    }

    public Resolution(String id, String name, String description, Long sequence)
    {
        super();
        this.id = id;
        this.description = description;
        this.name = name;
        this.sequence = sequence;
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
    
    

}