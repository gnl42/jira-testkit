/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.rest.api.issue.ResourceRef;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @since v4.3
 */
public class LinkRequest
{
    public static final String FIELD_NAME = "issuelinks";

    @JsonProperty
    private ResourceRef type;

    @JsonProperty
    private ResourceRef inwardIssue;

    @JsonProperty
    private ResourceRef outwardIssue;

    @JsonProperty
    private Comment comment;

    public LinkRequest()
    {
    }

    public LinkRequest(ResourceRef type, ResourceRef inwardIssue, ResourceRef outwardIssue, Comment comment)
    {
        this.comment = comment;
        this.type = type;
        this.inwardIssue = inwardIssue;
        this.outwardIssue = outwardIssue;
    }

    public ResourceRef type()
    {
        return this.type;
    }

    public LinkRequest type(ResourceRef linkType)
    {
        return new LinkRequest(linkType, inwardIssue, outwardIssue, comment);
    }

    public ResourceRef inwardIssue()
    {
        return this.inwardIssue;
    }

    public LinkRequest inwardIssue(ResourceRef inwardIssue)
    {
        return new LinkRequest(type, inwardIssue, outwardIssue, comment);
    }

    public ResourceRef outwardIssue()
    {
        return this.outwardIssue;
    }

    public LinkRequest outwardIssue(ResourceRef outwardIssue)
    {
        return new LinkRequest(type, inwardIssue, outwardIssue, comment);
    }

    public Comment comment()
    {
        return this.comment;
    }

    public LinkRequest comment(Comment comment)
    {
        return new LinkRequest(type, inwardIssue, outwardIssue, comment);
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
