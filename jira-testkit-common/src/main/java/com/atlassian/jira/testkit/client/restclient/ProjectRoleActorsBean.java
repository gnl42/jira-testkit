package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;

public class ProjectRoleActorsBean
{
    private Collection<ProjectRole.Actor> actors;

    public ProjectRoleActorsBean() {}

    public ProjectRoleActorsBean(final Collection<ProjectRole.Actor> actors)
    {
        this.actors = actors;
    }

    @JsonProperty
    public Collection<ProjectRole.Actor> getActors()
    {
        return actors;
    }

}
