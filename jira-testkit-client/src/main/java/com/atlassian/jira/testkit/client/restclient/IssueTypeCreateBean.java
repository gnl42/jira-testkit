package com.atlassian.jira.testkit.client.restclient;


import org.codehaus.jackson.annotate.JsonProperty;

public class IssueTypeCreateBean
{
    public enum Type { subtask, standard }

    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private Type type;

    public IssueTypeCreateBean(final String name, final String description, final Type type)
    {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(final Type type)
    {
        this.type = type;
    }
}
