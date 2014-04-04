package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.JiraException;
import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.subtask.conversion.IssueToSubTaskConversionService;
import com.atlassian.jira.bc.subtask.conversion.SubTaskToIssueConversionService;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.util.IssueUpdateBean;
import com.atlassian.jira.issue.util.IssueUpdater;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.apache.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST end point for conversion of issues.
 */
@Path("issueConversion")
@AnonymousAllowed
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class IssueConversionBackdoorResource
{

    private IssueManager issueManager;
    private SubTaskToIssueConversionService subTaskToIssueConversionService;
    private IssueToSubTaskConversionService issueToSubTaskConversionService;
    private IssueFactory issueFactory;
    private SubTaskManager subTaskManager;
    private IssueUpdater issueUpdater;
    private final Logger log = Logger.getLogger(this.getClass());
    private JiraAuthenticationContext jiraAuthenticationContext;

    public IssueConversionBackdoorResource(IssueManager issueManager,
                                           SubTaskToIssueConversionService subTaskToIssueConversionService,
                                           IssueToSubTaskConversionService issueToSubTaskConversionService,
                                           IssueFactory issueFactory, SubTaskManager subTaskManager,
                                           IssueUpdater issueUpdater,
                                           JiraAuthenticationContext jiraAuthenticationContext)
    {
        this.issueManager = issueManager;
        this.subTaskToIssueConversionService = subTaskToIssueConversionService;
        this.issueToSubTaskConversionService = issueToSubTaskConversionService;
        this.issueFactory = issueFactory;
        this.subTaskManager = subTaskManager;
        this.issueUpdater = issueUpdater;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

    @PUT
    @Path("changeSubtaskToIssue")
    public Response changeSubtaskToIssue(@QueryParam("subtaskKey") String subtaskKey,
                                         @QueryParam("issueTypeId") String issueTypeId)
    {
        MutableIssue mutableIssue = issueManager.getIssueObject(subtaskKey);
        JiraServiceContext context = new JiraServiceContextImpl(jiraAuthenticationContext.getLoggedInUser());
        if (!subTaskToIssueConversionService.canConvertIssue(context, mutableIssue)) {
            return Response.status(Response.Status.FORBIDDEN).entity(context.getErrorCollection().toString()).build();
        }
        MutableIssue targetIssue = issueFactory.cloneIssueNoParent(mutableIssue);
        targetIssue.setIssueTypeId(issueTypeId);
        targetIssue.setParentObject(null);
        subTaskToIssueConversionService.convertIssue(context, mutableIssue, targetIssue);
        if (context.getErrorCollection().hasAnyErrors()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(context.getErrorCollection().toString()).build();
        }
        return Response.ok(new ConvertedIssue(subtaskKey, null, issueTypeId)).build();
    }

    @PUT
    @Path("changeIssueToSubtask")
    public Response changeIssueToSubtask(@QueryParam("issueKey") String issueKey,
                                         @QueryParam("newParentIssueKey") String newParentIssueKey,
                                         @QueryParam("issueTypeId") String issueTypeId) {
        MutableIssue originalIssue = issueManager.getIssueObject(issueKey);
        JiraServiceContext context = new JiraServiceContextImpl(jiraAuthenticationContext.getLoggedInUser());
        if (!issueToSubTaskConversionService.canConvertIssue(context, originalIssue)) {
            return Response.status(Response.Status.FORBIDDEN).entity(context.getErrorCollection().toString()).build();
        }
        MutableIssue updatedIssue = issueFactory.cloneIssueNoParent(originalIssue);
        updatedIssue.setParentId(issueManager.getIssueObject(newParentIssueKey).getId());
        updatedIssue.setIssueTypeId(issueTypeId);
        issueToSubTaskConversionService.convertIssue(context, originalIssue, updatedIssue);
        return Response.ok(new ConvertedIssue(issueKey, newParentIssueKey, issueTypeId)).build();
    }

    @PUT
    @Path("changeSubtaskParent")
    public Response changeSubtaskParent(@QueryParam("subtaskKey") String subtaskKey,
                                        @QueryParam("newParentKey") String newParentKey) {
        try
        {
            Issue subTask = issueManager.getIssueObject(subtaskKey);
            Issue newParentIssue = issueManager.getIssueObject(newParentKey);
            IssueUpdateBean iub = subTaskManager.changeParent(subTask, newParentIssue,
                    jiraAuthenticationContext.getLoggedInUser());
            issueUpdater.doUpdate(iub, true);
        }
        catch (JiraException exception)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(exception).build();
        }
        return Response.ok(new ConvertedIssue(subtaskKey, newParentKey, null)).build();
    }

    private static class ConvertedIssue
    {
        public String issueKey;
        public String parentKey;
        public String issueTypeId;

        public ConvertedIssue(String issueKey, String parentKey, String issueTypeId) {
            this.issueKey = issueKey;
            this.parentKey = parentKey;
            this.issueTypeId = issueTypeId;
        }
    }

}
