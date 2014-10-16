package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;

public class ConfigurationBean
{
    @JsonProperty
    public boolean votingEnabled;
    @JsonProperty
    public boolean watchingEnabled;
    @JsonProperty
    public boolean unassignedIssuesAllowed;
    @JsonProperty
    public boolean subTasksEnabled;
    @JsonProperty
    public boolean issueLinkingEnabled;
    @JsonProperty
    public boolean timeTrackingEnabled;
    @JsonProperty
    public boolean attachmentsEnabled;
    @JsonProperty
    public TimeTrackingConfigurationBean timeTrackingConfiguration;

    public static class TimeTrackingConfigurationBean
    {
        public enum TimeFormat { pretty, days, hours }
        public enum TimeTrackingUnit { minute, hour, day, week }

        @JsonProperty
        public double workingHoursPerDay;
        @JsonProperty
        public double workingDaysPerWeek;
        @JsonProperty
        public TimeFormat timeFormat;
        @JsonProperty
        public TimeTrackingUnit defaultUnit;
    }

}
