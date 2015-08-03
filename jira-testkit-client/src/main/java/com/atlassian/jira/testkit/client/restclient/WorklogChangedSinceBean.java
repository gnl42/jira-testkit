package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.net.URI;
import java.util.List;

@JsonSerialize
public class WorklogChangedSinceBean
{
    @JsonProperty
    public List<WorklogChangeBean> values;

    @JsonProperty
    public Long since;

    @JsonProperty
    public Long until;

    @JsonProperty
    public boolean lastPage;

    @JsonProperty
    public URI self;

    @JsonProperty
    public URI nextPage;
}