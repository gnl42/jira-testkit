package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.BackdoorControl;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Client for issue conversion.
 *
 * @since 5.0.34
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
     * Convert sub-task to an issue
     * @param subtaskKey is the subtask that is to be converted
     * @param issueTypeId is the issueTypeId
     */
    public void changeSubtaskToIssue(String subtaskKey, String issueTypeId)
    {
        IssueConversionRequest request = new IssueConversionRequest();
        request.issueKey = subtaskKey;
        request.issueTypeId = issueTypeId;
        createIssueTypeResource().path("changeSubtaskToIssue").post(ClientResponse.class, request);
    }

    /**
     * Convert an issue into a subtask
     * @param issueKey is the key of the issue that is to be converted
     * @param newParentIssueKey is the new parent issue
     * @param issueTypeId is the issueTypeId
     */
    public void changeIssueToSubtask(String issueKey, String newParentIssueKey, String issueTypeId)
    {
        IssueConversionRequest request = new IssueConversionRequest();
        request.issueKey = issueKey;
        request.parentKey = newParentIssueKey;
        request.issueTypeId = issueTypeId;
        createIssueTypeResource().path("changeIssueToSubtask").post(ClientResponse.class, request);
    }

    /**
     * Change subtask's parent.
     */
    public void changeSubtaskParent(String subtaskKey, String newParentKey)
    {
        IssueConversionRequest request = new IssueConversionRequest();
        request.issueKey = subtaskKey;
        request.parentKey = newParentKey;
        createIssueTypeResource().path("changeSubtaskParent").post(ClientResponse.class, request);
    }

    public static class IssueConversionRequest
    {
        @JsonProperty
        public String issueKey;
        @JsonProperty
        public String parentKey;
        @JsonProperty
        public String issueTypeId;

        public IssueConversionRequest(String issueKey, String parentKey, String issueTypeId) {
            this.issueKey = issueKey;
            this.parentKey = parentKey;
            this.issueTypeId = issueTypeId;
        }

        public IssueConversionRequest()
        {
        }

        @Override
        public String toString()
        {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

}

