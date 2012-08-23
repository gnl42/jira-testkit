package com.atlassian.jira.tests.backdoor;

import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A backdoor for manipulating custom fields.
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

    @GET
    @AnonymousAllowed
    @Path("reIndex")
    public Response indexing()
    {
		try {
			issueIndexManager.reIndexAll();
		} catch (IndexException e) {
			throw new RuntimeException(e);
		}
		return Response.ok().build();
	}
}
