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
