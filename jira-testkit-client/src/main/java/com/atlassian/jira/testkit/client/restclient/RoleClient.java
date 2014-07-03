package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import java.util.List;

/**
 * @since 7.0
 */
public class RoleClient extends RestApiClient<RoleClient>
{
    public RoleClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public ProjectRole get(String roleKey)
    {
        return roles().path(roleKey).get(ProjectRole.class);
    }

    public List<ProjectRole> get()
    {
        return roles().get(new GenericType<List<ProjectRole>>(){});
    }

    protected WebResource roles()
    {
        return createResource().path("role");
    }
}
