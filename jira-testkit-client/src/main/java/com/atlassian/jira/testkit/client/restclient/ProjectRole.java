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

/**
 * @since v4.4
 */
public class ProjectRole
{
    public String self;
    public String name;
    public Long id;
    public String description;
    public List<Actor> actors;

    public ProjectRole self(String self)
    {
        this.self = self;
        return this;
    }

    public ProjectRole name(String name)
    {
        this.name = name;
        return this;
    }

    public ProjectRole id(Long id)
    {
        this.id = id;
        return this;
    }

    public ProjectRole description(String description)
    {
        this.description = description;
        return this;
    }

    public static class Actor
    {
        public Long id;
        public String displayName;
        public String type;
        public String name;
        public String avatarUrl;
    }
}
