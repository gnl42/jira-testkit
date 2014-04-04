package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.BackdoorControl;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Client for issue conversion.
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

    /**
     * Convert sub-task to an isse
     * @param subtaskKey is the subtask that is to be converted
     * @param issueTypeId is the issueTypeId
     */
    public void changeSubtaskToIssue(String subtaskKey, String issueTypeId)
    {
        createIssueTypeResource().path("changeSubtaskToIssue").queryParam("subtaskKey", subtaskKey).
                queryParam("issueTypeId", issueTypeId).put(ClientResponse.class);
    }

    /**
     * Convert an issue into a subtask
     * @param issueKey is the key of the issue that is to be converted
     * @param newParentIssueKey is the new parent issue
     * @param issueTypeId is the issueTypeId
     */
    public void changeIssueToSubtask(String issueKey, String newParentIssueKey, String issueTypeId)
    {
        createIssueTypeResource().path("changeIssueToSubtask").queryParam("issueKey", issueKey).
                queryParam("newParentIssueKey", newParentIssueKey).queryParam("issueTypeId", issueTypeId).
                put(ClientResponse.class);

    }

    /**
     * Change subtask's parent.
     * @param subtaskKey is the issueKey of the subtask to be changed
     * @param newParentKey is the issueKey of the new parent
     */
    public void changeSubtaskParent(String subtaskKey, String newParentKey)
    {
        createIssueTypeResource().path("changeSubtaskParent").queryParam("subtaskKey", subtaskKey).
                queryParam("newParentKey", newParentKey).put(ClientResponse.class);
    }

}

