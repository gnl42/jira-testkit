package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class NotificationSchemeEventBean
{
    @JsonProperty
    private NotificationEventBean event;
    @JsonProperty
    private List<NotificationBean> notifications;

    public NotificationSchemeEventBean()
    {
    }

    public NotificationSchemeEventBean(final NotificationEventBean event, final List<NotificationBean> notifications)
    {
        this.event = event;
        this.notifications = notifications;
    }

    public NotificationEventBean getEvent()
    {
        return event;
    }

    public List<NotificationBean> getNotifications()
    {
        return notifications;
    }
}
