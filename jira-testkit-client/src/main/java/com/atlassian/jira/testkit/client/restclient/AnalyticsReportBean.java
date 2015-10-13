package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@JsonSerialize
public class AnalyticsReportBean
{
    @JsonProperty
    public Boolean capturing;

    @JsonProperty
    public List<EventBean> events;

    @JsonSerialize
    public static class EventBean
    {
        @JsonProperty
        public String name;

        @JsonProperty
        public String time;

        @JsonProperty
        public String user;

        @JsonProperty
        public Map<String, String> properties;

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


