package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Objects;

public class PrioritySchemeUpdateBean {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private String defaultOptionId;
    @JsonProperty
    private List<String> optionIds;

    public PrioritySchemeUpdateBean() {
    }

    public PrioritySchemeUpdateBean(Long id, String name, String description, String defaultOptionId, List<String> optionIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultOptionId = defaultOptionId;
        this.optionIds = optionIds;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultOptionId() {
        return defaultOptionId;
    }

    public List<String> getOptionIds() {
        return optionIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrioritySchemeUpdateBean that = (PrioritySchemeUpdateBean) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(defaultOptionId, that.defaultOptionId) &&
                Objects.equals(optionIds, that.optionIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, defaultOptionId, optionIds);
    }
}
