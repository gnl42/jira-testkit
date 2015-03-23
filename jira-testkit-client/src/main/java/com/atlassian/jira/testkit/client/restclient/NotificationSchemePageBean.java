package com.atlassian.jira.testkit.client.restclient;

import java.util.List;

public class NotificationSchemePageBean extends PageBean<NotificationSchemeBean>
{
    public NotificationSchemePageBean()
    {
    }

    public NotificationSchemePageBean(final List<NotificationSchemeBean> values, final long total, final int maxResults, final long startAt)
    {
        super(values, total, maxResults, startAt);
    }
}
