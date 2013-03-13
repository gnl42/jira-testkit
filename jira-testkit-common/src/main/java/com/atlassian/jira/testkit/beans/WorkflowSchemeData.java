/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.beans;

import com.google.common.collect.Maps;
import com.sun.jersey.api.client.GenericType;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * @since v5.2
 */
public class WorkflowSchemeData
{
    public static final GenericType<List<WorkflowSchemeData>> LIST = new GenericType<List<WorkflowSchemeData>>()
    {
    };

    @JsonProperty
    private Long id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String defaultWorkflow;

    @JsonProperty
    private Map<String, String> mappings = Maps.newHashMap();

    @JsonProperty
    private boolean active;

    @JsonProperty
    private String description;

    @JsonProperty
    private boolean draft;

    @JsonProperty
    private String lastModified;

    @JsonProperty
    private String lastModifiedUser;

    public WorkflowSchemeData()
    {
    }

    public Long getId()
    {
        return id;
    }

    public WorkflowSchemeData setId(Long id)
    {
        this.id = id;

        return this;
    }

    public String getName()
    {
        return name;
    }

    public WorkflowSchemeData setName(String name)
    {
        this.name = name;

        return this;
    }

    public String getDefaultWorkflow()
    {
        return defaultWorkflow;
    }

    public WorkflowSchemeData setDefaultWorkflow(String defaultWorkflow)
    {
        this.defaultWorkflow = defaultWorkflow;

        return this;
    }

    public Map<String, String> getMappings()
    {
        return mappings;
    }

    public WorkflowSchemeData setMapping(String issueType, String workflow)
    {
        this.mappings.put(issueType, workflow);
        return this;
    }

    public WorkflowSchemeData setMappings(Map<String, String> mapping)
    {
        this.mappings = Maps.newHashMap(mapping);

        return this;
    }

    public WorkflowSchemeData setMappingWithDefault(Map<String, String> mapping)
    {
        this.mappings = Maps.newHashMap(mapping);
        this.defaultWorkflow = this.mappings.remove(null);

        return this;
    }

    public boolean isActive()
    {
        return active;
    }

    public String getDescription()
    {
        return description;
    }

    public WorkflowSchemeData setActive(boolean active)
    {
        this.active = active;
        return this;
    }

    public WorkflowSchemeData setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public boolean isDraft()
    {
        return draft;
    }

    public WorkflowSchemeData setDraft(boolean draft)
    {
        this.draft = draft;
        return this;
    }

    public String getLastModified()
    {
        return lastModified;
    }

    public WorkflowSchemeData setLastModified(String lastModified)
    {
        this.lastModified = lastModified;
        return this;
    }

    public String getLastModifiedUser()
    {
        return lastModifiedUser;
    }

    public WorkflowSchemeData setLastModifiedUser(String lastModifiedUser)
    {
        this.lastModifiedUser = lastModifiedUser;
        return this;
    }
}
