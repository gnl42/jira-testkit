package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.config.IndexTaskContext;
import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.atlassian.jira.task.TaskDescriptor;
import com.atlassian.jira.task.TaskManager;
import com.atlassian.jira.testkit.plugin.util.CacheControl;
import com.atlassian.jira.web.action.admin.index.IndexCommandResult;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A backdoor for indexing
 *
 * @since v5.1
 */
@Path("indexing")
@Produces ({MediaType.APPLICATION_JSON})
@Consumes ({MediaType.APPLICATION_JSON})
public class IndexingBackdoor
{
    private final IssueIndexManager issueIndexManager;
    private final TaskManager taskManager;

    public IndexingBackdoor(IssueIndexManager issueIndexManager, TaskManager taskManager)
    {
        this.issueIndexManager = issueIndexManager;
        this.taskManager = taskManager;
    }

    @POST
    @AnonymousAllowed
    @Path("reindexAll")
    public Response reindexAll()
    {
        try {
            issueIndexManager.reIndexAll();
        } catch (IndexException e) {
            throw new RuntimeException(e);
        }
        return Response.ok().build();
    }

    @GET
    @AnonymousAllowed
    public Response getIsIndexingInProgress()
    {
        TaskDescriptor<IndexCommandResult> task = getIndexingTask();

        boolean result = task != null && !task.isFinished();

        return Response.ok(result).cacheControl(CacheControl.never()).build();
    }

    @GET
    @Path("started")
    public boolean isIndexingStarted()
    {
        TaskDescriptor<IndexCommandResult> task = getIndexingTask();

        return task != null && task.getStartedTimestamp() != null;
    }

    private TaskDescriptor<IndexCommandResult> getIndexingTask()
    {
        return taskManager.getLiveTask(new IndexTaskContext());
    }
}