package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;

public class ConfigurationClient extends RestApiClient<ConfigurationClient>
{
    public ConfigurationClient(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public ConfigurationBean getConfiguration()
    {
        return createResource().path("configuration").get(ConfigurationBean.class);
    }

    public Response<ConfigurationBean> getConfigurationResponse()
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("configuration").get(ClientResponse.class);
            }
        }, ConfigurationBean.class);
    }
}