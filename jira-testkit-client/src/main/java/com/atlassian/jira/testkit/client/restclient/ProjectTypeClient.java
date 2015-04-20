package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ProjectTypeClient extends RestApiClient<ProjectTypeClient>
{
    public ProjectTypeClient(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }
    public ClientResponse getAllProjectTypes()
    {
        return projectTypes().get(ClientResponse.class);
    }

    public ClientResponse getByKey(String projectTypeKey)
    {
        return projectTypes().path(projectTypeKey).get(ClientResponse.class);
    }

    public ClientResponse getAccessibleProjectTypeByKey(String projectTypeKey)
    {
        return projectTypes().path(projectTypeKey).path("accessible").get(ClientResponse.class);
    }

    protected WebResource projectTypes()
    {
        return createResource().path("project/type");
    }
}
