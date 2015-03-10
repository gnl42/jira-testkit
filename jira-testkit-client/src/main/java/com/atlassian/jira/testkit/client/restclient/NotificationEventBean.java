package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nullable;

@JsonIgnoreProperties (ignoreUnknown = true)
public class NotificationEventBean
{
    @JsonProperty
    private Long id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty @Nullable
    private NotificationEventBean templateEvent;

    public NotificationEventBean()
    {
    }

    public NotificationEventBean(final Long id, final String name, final String description, final NotificationEventBean templateEvent)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.templateEvent = templateEvent;
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public NotificationEventBean getTemplateEvent()
    {
        return templateEvent;
    }
}
