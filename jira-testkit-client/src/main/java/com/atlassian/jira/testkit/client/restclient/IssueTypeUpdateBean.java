package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;

public class IssueTypeUpdateBean
{
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private Long avatarId;

    public IssueTypeUpdateBean(final String name, final String description, final Long avatarId)
    {
        this.name = name;
        this.description = description;
        this.avatarId = avatarId;
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

    public Long getAvatarId()
    {
        return avatarId;
    }

    public void setAvatarId(final Long avatarId)
    {
        this.avatarId = avatarId;
    }
}
