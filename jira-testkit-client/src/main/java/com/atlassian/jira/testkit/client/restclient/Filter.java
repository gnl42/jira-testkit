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
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;
import java.util.Objects;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Representation of a filter in the JIRA REST API.
 *
 * @since v4.3
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class Filter
{
    public static final GenericType<List<Filter>> FILTER_TYPE = new GenericType<List<Filter>>(){};

    public String self;
    public String id;
    public String name;
    public String description;
    public User owner;
    public String jql;
    public String viewUrl;
    public String searchUrl;
    public boolean favourite;
    public List<FilterPermission> sharePermissions;
    public Expando<User> sharedUsers;
    public Expando<FilterSubscription> subscriptions;
    public boolean editable;

    public static enum Expand
    {
        subscriptions,
        sharedUsers
    }

    public static class FilterPermission
    {
        public Long id;
        public String type;
        public Project project;
        public ProjectRole role;
        public User user;
        public Group group;
        public boolean view;
        public boolean edit;

        public FilterPermission()
        {
        }

        public FilterPermission id(Long id)
        {
            this.id = id;
            return this;
        }

        public FilterPermission type(String type)
        {
            this.type = type;
            return this;
        }

        public FilterPermission project(Project project)
        {
            this.project = project;
            return this;
        }

        public FilterPermission role(ProjectRole role)
        {
            this.role = role;
            return this;
        }

        public FilterPermission group(Group group)
        {
            this.group = group;
            return this;
        }
        public FilterPermission user(User user)
        {
            this.user = user;
            return this;
        }

        public FilterPermission view(boolean view)
        {
            this.view = view;
            return this;
        }

        public FilterPermission edit(boolean edit)
        {
            this.edit = edit;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FilterPermission that = (FilterPermission) o;

            if (group != null ? !group.equals(that.group) : that.group != null) return false;
            if (id != null ? !id.equals(that.id) : that.id != null) return false;
            if (project != null ? !project.equals(that.project) : that.project != null) return false;
            if (role != null ? !role.equals(that.role) : that.role != null) return false;
            if (type != null ? !type.equals(that.type) : that.type != null) return false;
            if (user != null ? !user.equals(that.user) : that.user != null) return false;
            if (view != that.view) return false;
            if (view != that.view) return false;
            return true;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(id, type, project, role, user, group, view, edit);
        }
    }

    public static class FilterSubscription
    {
        public Long id;
        public User user;
        public Group group;
    }

}
