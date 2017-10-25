package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class PrioritySchemeGetAllResponseBean {
    @JsonProperty
    private List<PrioritySchemeBean> schemes;

    public PrioritySchemeGetAllResponseBean() {
    }

    public PrioritySchemeGetAllResponseBean(List<PrioritySchemeBean> schemes) {
        this.schemes = schemes;
    }

    public List<PrioritySchemeBean> getSchemes() {
        return schemes;
    }
}

