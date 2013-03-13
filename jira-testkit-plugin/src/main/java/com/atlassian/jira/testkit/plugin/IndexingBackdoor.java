/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

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