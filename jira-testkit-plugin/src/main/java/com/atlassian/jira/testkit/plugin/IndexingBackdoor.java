package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.issue.index.IssueIndexManager;
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

    public IndexingBackdoor(IssueIndexManager issueIndexManager)
    {
        this.issueIndexManager = issueIndexManager;
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
}