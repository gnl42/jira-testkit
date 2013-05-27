/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import java.util.Arrays;
import java.util.List;

/**
 * Representation of a search request.
 *
 * @since v4.3
 */
public class SearchRequest
{
    public String jql = "";
    public Integer startAt;
    public Integer maxResults;
    public Boolean validateQuery;
    public List<String> fields;
    public List<String> expand;

    public SearchRequest()
    {
    }

    public SearchRequest jql(String jql)
    {
        this.jql = jql;
        return this;
    }

    public SearchRequest startAt(Integer startAt)
    {
        this.startAt = startAt;
        return this;
    }

    public SearchRequest maxResults(Integer maxResults)
    {
        this.maxResults = maxResults;
        return this;
    }

    public SearchRequest validateQuery(boolean validateQuery)
    {
        this.validateQuery = validateQuery;
        return this;
    }

    public SearchRequest fields(String... fields)
    {
        this.fields = fields != null ? Arrays.asList(fields) : null;
        return this;
    }

    public SearchRequest fields(List<String> fields)
    {
        this.fields = fields;
        return this;
    }

    public SearchRequest expand(String... expand)
    {
        this.expand = expand != null ? Arrays.asList(expand) : null;
        return this;
    }

    public SearchRequest expand(List<String> expand)
    {
        this.expand = expand;
        return this;
    }
}
