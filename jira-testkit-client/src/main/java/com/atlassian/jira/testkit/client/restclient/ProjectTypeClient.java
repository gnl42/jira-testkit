package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.beans.ProjectTypeBean;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.List;

public class ProjectTypeClient extends RestApiClient<ProjectTypeClient>
{
    public ProjectTypeClient(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public Response<List<ProjectTypeBean>> getAllProjectTypes()
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return projectTypes().get(ClientResponse.class);
            }
        }, ProjectTypeBean.LIST_TYPE);
    }

    public Response<ProjectTypeBean> getByKey(final String projectTypeKey)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return projectTypes().path(projectTypeKey).get(ClientResponse.class);
            }
        }, ProjectTypeBean.class);
    }

    public Response<ProjectTypeBean> getAccessibleProjectTypeByKey(final String projectTypeKey)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return projectTypes().path(projectTypeKey).path("accessible").get(ClientResponse.class);
            }
        }, ProjectTypeBean.class);
    }

    protected WebResource projectTypes()
    {
        return createResource().path("project/type");
    }
}
