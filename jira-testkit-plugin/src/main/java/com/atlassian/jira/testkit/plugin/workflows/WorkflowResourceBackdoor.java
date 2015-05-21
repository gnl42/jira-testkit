/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin.workflows;

import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;

/**
 * Used to query workflows during the functional tests.
 *
 * @since v5.1
 */
@AnonymousAllowed
@Produces ({MediaType.APPLICATION_JSON})
@Consumes ({MediaType.APPLICATION_JSON})
@Path ("workflow")
public class WorkflowResourceBackdoor
{
    private final WorkflowManager workflowManager;

    public WorkflowResourceBackdoor(WorkflowManager workflowManager)
    {
        this.workflowManager = workflowManager;
    }

    @GET
    public Response getWorkflows()
    {
        Collection<JiraWorkflow> workflows = workflowManager.getWorkflows();
        List<String> str = Lists.newArrayListWithCapacity(workflows.size());
        for (JiraWorkflow workflow : workflows)
        {
            str.add(workflow.getName());
        }
        return Response.ok(str).cacheControl(never()).build();
    }

    @POST
    @Path ("cloneWorkflow")
    public Response cloneWorkflow(
            @QueryParam ("sourceName") String sourceName,
            @QueryParam ("resultName") String resultName)
    {
        JiraWorkflow sourceWorkflow = workflowManager.getWorkflow(sourceName);
        JiraWorkflow newWorkflow = workflowManager.copyWorkflow("admin", resultName, "", sourceWorkflow);
        workflowManager.createWorkflow("admin", newWorkflow);
        return Response.ok().build();
    }
}
