package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.beans.IssueSecuritySchemeBean;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class IssueSecuritySchemeClient extends RestApiClient<IssueSecuritySchemeClient>{

    public IssueSecuritySchemeClient(JIRAEnvironmentData environmentData) {
        super(environmentData);
    }

    public Response<IssueSecuritySchemes> getAllSecuritySchemes() throws UniformInterfaceException
    {
        return toResponse(new Method() {
            @Override
            public ClientResponse call() {
                return resource().get(ClientResponse.class);
            }
        }, IssueSecuritySchemes.class);
    }

    public Response<IssueSecuritySchemeBean> get(final long schemeId) throws UniformInterfaceException
    {
        return toResponse(new Method() {
            @Override
            public ClientResponse call() {
                return issueSecuritySchemeWithID(schemeId).get(ClientResponse.class);
            }
        }, IssueSecuritySchemeBean.class);
    }

    private WebResource issueSecuritySchemeWithID(long schemeId)
    {
        return resource().path(String.valueOf(schemeId));
    }

    private WebResource resource()
    {
        return createResource().path("issuesecurityschemes");
    }
}
