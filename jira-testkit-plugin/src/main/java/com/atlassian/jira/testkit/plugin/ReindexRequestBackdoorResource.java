package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.index.request.ReindexRequestManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("reindexRequest")
@AnonymousAllowed
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ReindexRequestBackdoorResource
{
    private final ReindexRequestManager reindexRequestManager;

    public ReindexRequestBackdoorResource(ReindexRequestManager reindexRequestManager)
    {
        this.reindexRequestManager = reindexRequestManager;
    }

    @GET
    @Path("/allDone")
    public Response getReindexInProgress()
    {

        if (reindexRequestManager.isReindexInProgress() || reindexRequestManager.isReindexRequested())
        {
            return Response.ok().entity(false).build();
        }

        return Response.ok().entity(true).build();
    }
}
