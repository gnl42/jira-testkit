package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Set;

@JsonIgnoreProperties (ignoreUnknown = true)
public class ApplicationRole
{
    @JsonProperty
    public String key;

    @JsonProperty
    public Set<String> groups;

    @JsonProperty
    public String name;

    @JsonProperty
    public Set<String> defaultGroups;

    @JsonProperty
    public Boolean selectedByDefault;

    @JsonProperty
    public Boolean defined;

    @JsonProperty
    public Integer numberOfSeats;

    @JsonProperty
    public Integer remainingSeats;

    @JsonProperty
    public Integer userCount;

    @JsonProperty
    public String userCountDescription;

    @JsonProperty
    public Boolean hasUnlimitedSeats;
}