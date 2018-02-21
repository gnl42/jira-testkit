package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.index.IssueIndexingService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.ofbiz.core.entity.GenericValue;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;

@AnonymousAllowed
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("issues")
public class IssuesBackdoorExt {
    private final IssueManager issueManager;
    private final IssueIndexingService issueIndexingService;

    public IssuesBackdoorExt(final IssueManager issueManager,
                             final IssueIndexingService issueIndexingService) {
        this.issueManager = issueManager;
        this.issueIndexingService = issueIndexingService;
    }

    @PUT
    @Path("touch")
    public void touch(@QueryParam("key") final String key) throws Exception {
        changeUpdatedDate(key, System.currentTimeMillis());
    }

    @PUT
    @Path("changeUpdated")
    public void changeUpdatedDate(@QueryParam("key") final String key,
                                  @QueryParam("date") final long millis) throws Exception {
        changeDate(key, "updated", millis);
    }

    @PUT
    @Path("changeCreated")
    public void changeCratedDate(@QueryParam("key") final String key,
                                 @QueryParam("date") final long millis) throws Exception {
        changeDate(key, "created", millis);
    }

    private void changeDate(final String key, final String fieldName, final long millis)  throws Exception{
        final GenericValue issue = issueManager.getIssue(key);
        issue.set(fieldName, new Timestamp(millis));
        issue.store();
        issueIndexingService.reIndex(issueManager.getIssueObject(key));
    }
}
