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

/**
 * Representation in the JIRA REST API.
 *
 * @since v4.3
 */
public class IssueType
{
    public String self;
    public String id;
    public String name;
    public boolean subtask;
    public String description;
    public String iconUrl;
    public Long avatarId;

    public IssueType self(final String self)
    {
        this.self = self;
        return this;
    }

    public IssueType id(final String id)
    {
        this.id = id;
        return this;
    }

    public IssueType name(final String name)
    {
        this.name = name;
        return this;
    }

    public IssueType subtask(final boolean subtask)
    {
        this.subtask = subtask;
        return this;
    }

    public IssueType description(final String description)
    {
        this.description = description;
        return this;
    }

    public IssueType iconUrl(final String iconUrl)
    {
        this.iconUrl = iconUrl;
        return this;
    }

    public IssueType avatarId(final Long avatarId)
    {
        this.avatarId = avatarId;
        return this;
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
