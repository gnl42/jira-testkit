/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import java.util.List;
import java.util.Map;

import com.atlassian.jira.util.json.JSONObject;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Representation of a comment in the JIRA REST API.
 *
 * @since v4.3
 */
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class    Comment
{
    public String self;
    public String id;
    public String created;
    public String updated;
    public String body;
    public String renderedBody;
    public UserJson author;
    public UserJson updateAuthor;
    public Visibility visibility;
    public List<CommentProperty> properties;

    public Comment()
    {
    }
    
    public Comment(String body, String roleLevel)
    {
        this.body = body;
        this.visibility = new Visibility("ROLE", roleLevel);
    }

    public void setProperties(List<Map<String, Object>> properties)
    {
        this.properties = Lists.transform(properties, new Function<Map<String, Object>, CommentProperty>()
        {
            @Override
            public CommentProperty apply(final Map<String, Object> commentProperty)
            {
                String key = (String) commentProperty.get("key");
                JSONObject value = new JSONObject((Map<String, Object>) commentProperty.get("value"));
                return new CommentProperty(key, value.toString());
            }
        });
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommentProperty
    {
        @JsonProperty
        public String key;
        @JsonProperty
        public String value;

        public CommentProperty()
        {
        }

        public CommentProperty(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        public String getKey()
        {
            return key;
        }

        public String getValue()
        {
            return value;
        }
    }
}
