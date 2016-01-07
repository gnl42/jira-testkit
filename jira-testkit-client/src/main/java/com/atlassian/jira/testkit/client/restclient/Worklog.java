/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Representation of a worklog in the JIRA REST API.
 *
 * @since v4.3
 */
@JsonSerialize
public class Worklog
{
    public String id;
    public String self;
    public String issueId;
    public UserJson author;
    public UserJson updateAuthor;
    public String comment;
    public String created;
    public String updated;
    public String started;
    public String timeSpent;
    public Long timeSpentSeconds;
    public Visibility visibility;

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        final Worklog worklog = (Worklog) o;

        if (id != null ? !id.equals(worklog.id) : worklog.id != null) { return false; }
        if (self != null ? !self.equals(worklog.self) : worklog.self != null) { return false; }
        if (issueId != null ? !issueId.equals(worklog.issueId) : worklog.issueId != null) { return false; }
        if (author != null ? !author.equals(worklog.author) : worklog.author != null) { return false; }
        if (updateAuthor != null ? !updateAuthor.equals(worklog.updateAuthor) : worklog.updateAuthor != null)
        {
            return false;
        }
        if (comment != null ? !comment.equals(worklog.comment) : worklog.comment != null) { return false; }
        if (created != null ? !created.equals(worklog.created) : worklog.created != null) { return false; }
        if (updated != null ? !updated.equals(worklog.updated) : worklog.updated != null) { return false; }
        if (started != null ? !started.equals(worklog.started) : worklog.started != null) { return false; }
        if (timeSpent != null ? !timeSpent.equals(worklog.timeSpent) : worklog.timeSpent != null) { return false; }
        if (timeSpentSeconds != null ? !timeSpentSeconds.equals(worklog.timeSpentSeconds) : worklog.timeSpentSeconds != null)
        { return false; }
        return visibility != null ? visibility.equals(worklog.visibility) : worklog.visibility == null;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (self != null ? self.hashCode() : 0);
        result = 31 * result + (issueId != null ? issueId.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (updateAuthor != null ? updateAuthor.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        result = 31 * result + (started != null ? started.hashCode() : 0);
        result = 31 * result + (timeSpent != null ? timeSpent.hashCode() : 0);
        result = 31 * result + (timeSpentSeconds != null ? timeSpentSeconds.hashCode() : 0);
        result = 31 * result + (visibility != null ? visibility.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("self", self)
                .append("issueId", issueId)
                .append("authorKey", author.key)
                .append("updateAuthorKey", updateAuthor.key)
                .append("comment", comment)
                .append("created", created)
                .append("updated", updated)
                .append("started", started)
                .append("timeSpent", timeSpent)
                .append("timeSpentSeconds", timeSpentSeconds)
                .append("visibility", visibility)
                .toString();
    }
}
