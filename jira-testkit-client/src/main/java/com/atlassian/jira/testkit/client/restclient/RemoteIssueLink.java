/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.sun.jersey.api.client.GenericType;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

/**
 * Representation of a remote issue link in the JIRA REST API.
 *
 * @since v5.0
 */
public class RemoteIssueLink
{
    public static final GenericType<List<RemoteIssueLink>> REMOTE_ISSUE_LINKS_TYPE = new GenericType<List<RemoteIssueLink>>(){};

    public String self;
    public Long id;
    public String globalId;
    public Application application;
    public String relationship;
    public RemoteObject object;

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

    public static class Application
    {
        public String type;
        public String name;
    }

    public static class RemoteObject
    {
        public String url;
        public String title;
        public String summary;
        public Icon icon;
        public Status status;

        public static class Icon
        {
            public String url16x16;
            public String title;
            public String link;
        }

        public static class Status
        {
            public Boolean resolved;
            public Icon icon;
        }
    }
}
