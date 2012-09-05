package com.atlassian.jira.testkit.client.restclient;

import com.sun.jersey.api.client.GenericType;

import java.util.List;

/**
 * Representation of a filter in the JIRA REST API.
 *
 * @since v4.3
 */
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

    public static class FilterPermission
    {
        public Long id;
        public String type;
        public Project project;
        public ProjectRole role;
        public Group group;

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
    }

}