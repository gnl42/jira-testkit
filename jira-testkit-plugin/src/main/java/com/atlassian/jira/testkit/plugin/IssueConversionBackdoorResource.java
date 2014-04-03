package com.atlassian.jira.testkit.plugin;

import com.atlassian.crowd.embedded.api.User;
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
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 */
@Path("issueConversion")
@AnonymousAllowed
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class IssueConversionBackdoorResource
{
    private IssueManager issueManager;
    private UserManager userManager;
    private SubTaskToIssueConversionService subTaskToIssueConversionService;
    private IssueToSubTaskConversionService issueToSubTaskConversionService;
    private IssueFactory issueFactory;
    private SubTaskManager subTaskManager;
    private IssueUpdater issueUpdater;

    public IssueConversionBackdoorResource(IssueManager issueManager, UserManager userManager,
                                           SubTaskToIssueConversionService subTaskToIssueConversionService,
                                           IssueToSubTaskConversionService issueToSubTaskConversionService,
                                           IssueFactory issueFactory, SubTaskManager subTaskManager,
                                           IssueUpdater issueUpdater) {
        this.issueManager = issueManager;
        this.userManager = userManager;
        this.subTaskToIssueConversionService = subTaskToIssueConversionService;
        this.issueToSubTaskConversionService = issueToSubTaskConversionService;
        this.issueFactory = issueFactory;
        this.subTaskManager = subTaskManager;
        this.issueUpdater = issueUpdater;
    }

    private User getUser()
    {
        return userManager.getUserObject("admin");
    }

    @GET
    @Path("changeSubtaskToIssue")
    public Response changeSubtaskToIssue(@QueryParam("subtaskKey") String subtaskKey,
                                         @QueryParam("issueTypeId") String issueTypeId)
    {
        MutableIssue mutableIssue = issueManager.getIssueObject(subtaskKey);
        JiraServiceContext context = new JiraServiceContextImpl(getUser());
        if (!subTaskToIssueConversionService.canConvertIssue(context, mutableIssue))
        {
            throw new RuntimeException("can't convert this issue");
        }
        MutableIssue targetIssue = issueFactory.cloneIssueNoParent(mutableIssue);
        targetIssue.setIssueTypeId(issueTypeId);
        targetIssue.setParentObject(null);
        subTaskToIssueConversionService.convertIssue(context, mutableIssue, targetIssue);
        if (context.getErrorCollection().hasAnyErrors())
        {
            throw new RuntimeException("failed to convert issue due to "+context.getErrorCollection().toString());
        }
        return Response.ok(null).build();
    }


    @GET
    @Path("changeIssueToSubtask")
    public Response changeIssueToSubtask(@QueryParam("issueKey")String issueKey,
                                         @QueryParam("newParentIssueKey")String newParentIssueKey,
                                         @QueryParam("issueTypeId")String issueTypeId)
    {
        MutableIssue originalIssue = issueManager.getIssueObject(issueKey);
        JiraServiceContext context = new JiraServiceContextImpl(getUser());
        if (!issueToSubTaskConversionService.canConvertIssue(context, originalIssue))
        {
            throw new RuntimeException("can't convert this issue");
        }
        MutableIssue updatedIssue = issueFactory.cloneIssueNoParent(originalIssue);
        updatedIssue.setParentId(issueManager.getIssueObject(newParentIssueKey).getId());
        updatedIssue.setIssueTypeId(issueTypeId);
        issueToSubTaskConversionService.convertIssue(context, originalIssue, updatedIssue);
        if (context.getErrorCollection().hasAnyErrors())
        {
            throw new RuntimeException("failed to convert issue due to "+context.getErrorCollection().toString());
        }
        return Response.ok(null).build();
    }

    @GET
    @Path("moveSubtask")
    public Response moveSubtask(@QueryParam("subtaskKey")String subtaskKey,
                                @QueryParam("newParentKey") String newParentKey)
    {
        try
        {
            Issue subTask = issueManager.getIssueObject(subtaskKey);
            Issue newParentIssue = issueManager.getIssueObject(newParentKey);
            IssueUpdateBean iub = subTaskManager.changeParent(subTask, newParentIssue, getUser());
            issueUpdater.doUpdate(iub, true);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return Response.ok(null).build();
    }

}
