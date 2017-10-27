package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class PrioritySchemeGetAllResponseBean {
    @JsonProperty
    private List<PrioritySchemeBean> schemes;

    @JsonProperty
    private Long startAt;

    @JsonProperty
    private Integer maxResults;

    @JsonProperty
    private Integer total;

    public PrioritySchemeGetAllResponseBean() {
    }

    public PrioritySchemeGetAllResponseBean(List<PrioritySchemeBean> schemes, Long startAt, Integer maxResults, Integer total) {
        this.schemes = schemes;
        this.startAt = startAt;
        this.maxResults = maxResults;
        this.total = total;
    }

    public List<PrioritySchemeBean> getSchemes() {
        return schemes;
    }

    public Long getStartAt() {
        return startAt;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public Integer getTotal() {
        return total;
    }
}

