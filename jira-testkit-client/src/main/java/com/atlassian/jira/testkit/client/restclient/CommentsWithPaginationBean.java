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
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * @since v5.0
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class CommentsWithPaginationBean
{
    @JsonProperty
    private Integer startAt;

    @JsonProperty
    private Integer maxResults;

    @JsonProperty
    private Integer total;

    @JsonProperty
    private List<Comment> comments;

    public CommentsWithPaginationBean()
    {
    }

    public Integer getStartAt()
    {
        return startAt;
    }

    public void setStartAt(Integer startAt)
    {
        this.startAt = startAt;
    }

    public Integer getMaxResults()
    {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults)
    {
        this.maxResults = maxResults;
    }

    public Integer getTotal()
    {
        return total;
    }

    public void setTotal(Integer total)
    {
        this.total = total;
    }

    public List<Comment> getComments()
    {
        return comments;
    }

    public void setComments(List<Comment> comments)
    {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
