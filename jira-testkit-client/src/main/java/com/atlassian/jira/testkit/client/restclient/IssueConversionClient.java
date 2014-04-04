package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.BackdoorControl;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 */
public class IssueConversionClient extends BackdoorControl<IssueConversionClient>
{

    public IssueConversionClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    private WebResource createIssueTypeResource()
    {
        return createResource().path("issueConversion");
    }

    public void changeSubtaskToIssue(String subtaskKey, String issueTypeId)
    {
        createIssueTypeResource().path("changeSubtaskToIssue").queryParam("subtaskKey", subtaskKey).
                queryParam("issueTypeId", issueTypeId).put(ClientResponse.class);
    }

    public void changeIssueToSubtask(String issueKey, String newParentIssueKey, String issueTypeId)
    {
        createIssueTypeResource().path("changeIssueToSubtask").queryParam("issueKey", issueKey).
                queryParam("newParentIssueKey", newParentIssueKey).queryParam("issueTypeId", issueTypeId).
                put(ClientResponse.class);

    }

    public void changeSubtaskParent(String subtaskKey, String newParentKey)
    {
        createIssueTypeResource().path("changeSubtaskParent").queryParam("subtaskKey", subtaskKey).
                queryParam("newParentKey", newParentKey).put(ClientResponse.class);
    }

}

