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
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Representations for the transitions resource in the JIRA REST API.
 *
 * @since v4.3
 */
public class IssueTransitionsMeta
{
    public String expand;
    public List<Transition> transitions;

    @JsonIgnoreProperties (ignoreUnknown = true)
    public static class Transition
    {
        public String expand;
        public int id;
        public Status to;
        public String name;
        public Map<String, TransitionField> fields;

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

    @JsonIgnoreProperties (ignoreUnknown = true)
    public static class TransitionField
    {
        public Boolean required;
        public JsonType schema;
        public String autoCompleteUrl;
        public List<String> operations;
        public List<Object> allowedValues;

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

    @JsonIgnoreProperties (ignoreUnknown = true)
    public static class JsonType
    {
        @JsonProperty
        public String type;
        @JsonProperty
        public String items;
        @JsonProperty
        public String system;
        @JsonProperty
        public String custom;
        @JsonProperty
        public Long customId;

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

    public static enum Expand
    {
        fields("transitions.fields");

        private final String actualString;

        Expand(final String actualString)
        {
            this.actualString = actualString;
        }

        @Override
        public String toString()
        {
            return actualString;
        }

    }
}
