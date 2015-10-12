package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@JsonSerialize
public class AnalyticsReportBean
{
    public Boolean capturing;
    public List<EventBean> events;

    public AnalyticsReportBean(final Boolean capturing, final List<EventBean> events)
    {
        this.capturing = capturing;
        this.events = events;
    }

    @JsonSerialize
    public static class EventBean
    {
        public String name;
        public String time;
        public String user;
        public Map<String, String> properties;

        public EventBean() {}

        public EventBean(final String name, final String time, final String user, final Map<String, String> properties)
        {
            this.name = name;
            this.time = time;
            this.user = user;
            this.properties = properties;
        }


        @Override
        public String toString() {

            final StringBuilder sb = new StringBuilder();

            for (final Entry<String, String> e : properties.entrySet())
            {
                sb.append(String.format("%s -> %s, ", e.getKey(), e.getValue()));
            }

            return "EventBean{" +
                    "name='" + name + '\'' +
                    ", user='" + user + '\'' +
                    ", properties=" + sb.toString() +
                    '}';
        }
    }
}


