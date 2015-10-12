package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class AnalyticsReportConfig
{
    public Boolean capturing;

    public AnalyticsReportConfig() {}

    public AnalyticsReportConfig(final Boolean capturing)
    {
        this.capturing = capturing;
    }
}

