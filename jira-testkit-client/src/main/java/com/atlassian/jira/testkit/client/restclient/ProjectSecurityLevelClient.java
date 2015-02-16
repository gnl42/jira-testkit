package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class ProjectSecurityLevelClient extends RestApiClient<ProjectSecurityLevelClient>
{
    public ProjectSecurityLevelClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    protected WebResource issueSecurityLevelsForProject(String projectKey)
    {
        return createResource().path("project").path(projectKey).path("securitylevel");
    }

    /**
     * GETs the list of issue security levels for the given project key.
     *
     * @param projectKey a String representing the project key
     * @return an IssueSecurityType
     * @throws com.sun.jersey.api.client.UniformInterfaceException if there is a problem getting the issue type
     */
    public IssueSecurityLevels get(String projectKey) throws UniformInterfaceException
    {
        return issueSecurityLevelsForProject(projectKey).get(IssueSecurityLevels.class);
    }

    /**
     * GETs the issue type with the given id, returning a Response.
     *
     * @param projectKey a String representing the project key
     * @return a Response
     */
    public Response getResponse(final String projectKey)
    {
        return issueSecurityLevelsForProject(projectKey).get(Response.class);
    }

}
