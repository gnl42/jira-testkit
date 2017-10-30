package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;

import java.net.URI;
import java.util.List;

public class PrioritySchemeBean {

    @JsonProperty
    private String expand;
    @JsonProperty
    private URI self;
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
    @JsonProperty
    private boolean defaultScheme;
    @JsonProperty
    private List<String> projectKeys;

    public PrioritySchemeBean() {
    }

    public PrioritySchemeBean(URI self, Long id, String name, String description, String defaultOptionId, List<String> optionIds, boolean defaultScheme, List<String> projectKeys) {
        this.self = self;
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultOptionId = defaultOptionId;
        this.optionIds = optionIds;
        this.defaultScheme = defaultScheme;
        this.projectKeys = projectKeys;
    }

    public URI getSelf() {
        return self;
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

    public boolean isDefaultScheme() {
        return defaultScheme;
    }

    public List<String> getProjectKeys() {
        return projectKeys;
    }

    public enum Expand {
        projectKeys
    }
}
