/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Representation of a status in the JIRA REST API.
 *
 * @since v4.3
 */
public class Status
{
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String self;

    @JsonProperty
    private String description;

    @JsonProperty
    private String iconUrl;

    @JsonProperty
    private StatusCategory statusCategory;

    public Status()
    {
    }

    public Status(String id, String name, String self, String description, String iconUrl, StatusCategory statusCategory)
    {
        this.id = id;
        this.name = name;
        this.self = self;
        this.description = description;
        this.iconUrl = iconUrl;
        this.statusCategory = statusCategory;
    }

    public String id()
    {
        return this.id;
    }

    public Status id(String id)
    {
        return new Status(id, name, self, description, iconUrl, statusCategory);
    }

    public String name()
    {
        return name;
    }

    public Status name(String name)
    {
        return new Status(id, name, self, description, iconUrl, statusCategory);
    }

    public String self()
    {
        return this.self;
    }

    public Status self(String self)
    {
        return new Status(id, name, self, description, iconUrl, statusCategory);
    }

    public String description()
    {
        return this.description;
    }

    public Status description(String description)
    {
        return new Status(id, name, self, description, iconUrl, statusCategory);
    }

    public String iconUrl()
    {
        return iconUrl;
    }

    public Status iconUrl(String iconUrl)
    {
        return new Status(id, name, self, description, iconUrl, statusCategory);
    }

    public StatusCategory statusCategory()
    {
        return statusCategory;
    }

    public Status statusCategory(final StatusCategory statusCategory)
    {
        return new Status(id, name, self, description, iconUrl, statusCategory);
    }

    @Override
    public boolean equals(Object o)
    {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
