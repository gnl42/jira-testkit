package com.atlassian.jira.testkit.client.restclient;


import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class NotificationSchemeBean
{
    @JsonProperty
    private Long id;
    @JsonProperty
    private String self;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private List<NotificationSchemeEventBean> notificationSchemeEvents;

    public NotificationSchemeBean()
    {
    }

    public NotificationSchemeBean(final Long id,
            final String self,
            final String name,
            final String description,
            final List<NotificationSchemeEventBean> notificationSchemeEvents)
    {
        this.id = id;
        this.self = self;
        this.name = name;
        this.description = description;
        this.notificationSchemeEvents = notificationSchemeEvents;
    }

    public Long getId()
    {
        return id;
    }

    public String getSelf()
    {
        return self;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public List<NotificationSchemeEventBean> getNotificationSchemeEvents()
    {
        return notificationSchemeEvents;
    }
}
