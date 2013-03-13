/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode;

/**
 * Representation of a progress in the JIRA REST API.
 *
 * @since v5.0
 */
public class Progress
{
    @JsonProperty
    private Long progress;

    @JsonProperty
    private Long total;

    @JsonProperty
    private Long percent;


    public Progress()
    {
    }

    public Progress(Long progress, Long total, Long percent)
    {
        this.progress = progress;
        this.total = total;
        this.percent = percent;
    }

    public Long progress()
    {
        return this.progress;
    }

    public Progress progress(Long progress)
    {
        return new Progress(progress, total, percent);
    }

    public Long total()
    {
        return this.total;
    }

    public Progress total(Long total)
    {
        return new Progress(progress, total, percent);
    }

    public Long percent()
    {
        return this.percent;
    }

    public Progress percent(Long percent)
    {
        return new Progress(progress, total, percent);
    }


    @Override
    public boolean equals(Object o) { return reflectionEquals(this, o); }

    @Override
    public int hashCode() { return reflectionHashCode(this); }
}
