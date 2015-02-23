package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.beans.IssueSecuritySchemeBean;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class IssueSecuritySchemeClient extends RestApiClient<IssueSecuritySchemeClient>{

    public IssueSecuritySchemeClient(JIRAEnvironmentData environmentData) {
        super(environmentData);
    }

    public IssueSecuritySchemes list() throws UniformInterfaceException
    {
        return createResource().path("issuesecurityschemes").get(IssueSecuritySchemes.class);
    }

    public IssueSecuritySchemeBean get(long schemeId) throws UniformInterfaceException
    {
        return issueSecuritySchemeWithID(schemeId).get(IssueSecuritySchemeBean.class);
    }

    private WebResource issueSecuritySchemeWithID(long schemeId)
    {
        return createResource().path("issuesecurityschemes").path(String.valueOf(schemeId));
    }
}
