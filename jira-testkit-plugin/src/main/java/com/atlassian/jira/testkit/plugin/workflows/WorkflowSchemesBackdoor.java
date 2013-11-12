/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin.workflows;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.testkit.beans.WorkflowSchemeData;
import com.atlassian.jira.testkit.plugin.util.CacheControl;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.ApplicationUsers;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.DraftWorkflowScheme;
import com.atlassian.jira.workflow.WorkflowScheme;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Iterables;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;
import static org.apache.commons.lang.StringUtils.stripToNull;

/**
 * Used to manipulate workflows during functional tests.
 *
 * @since v5.1
 */
@AnonymousAllowed
@Produces ({MediaType.APPLICATION_JSON})
@Consumes ({MediaType.APPLICATION_JSON})
@Path ("workflowscheme")
public class WorkflowSchemesBackdoor
{
    private final WorkflowSchemeManager workflowSchemeManager;
    private final ProjectManager projectManager;
    private final JiraAuthenticationContext context;
    private final WorkflowSchemeDataFactoryImpl dataFactory;

    public WorkflowSchemesBackdoor(WorkflowSchemeManager workflowSchemeManager,
                                   ProjectManager projectManager,
                                   JiraAuthenticationContext context,
                                   WorkflowSchemeDataFactoryImpl dataFactory)
    {
        this.workflowSchemeManager = workflowSchemeManager;
        this.projectManager = projectManager;
        this.context = context;
        this.dataFactory = dataFactory;
    }

    @GET
    @Path("copy")
    public Response copyWorkflowScheme(@QueryParam("schemeName") String schemeName, @QueryParam("newSchemeName") String newSchemeName)
    {
        final Scheme defaultScheme = workflowSchemeManager.getSchemeObject(schemeName);
        final Scheme copyScheme = workflowSchemeManager.copyScheme(defaultScheme);

        // TODO - create new scheme blah blah immutable blah
        copyScheme.setName(newSchemeName);
        workflowSchemeManager.updateScheme(copyScheme);

        return Response.ok(copyScheme.getId()).build();
    }

    @GET
    public Response getWorkflowScheme(@QueryParam("schemeName") String schemeName,
           @QueryParam ("projectKey") String projectKey, @QueryParam ("projectName") String projectName,
           @QueryParam ("draft") boolean getDraft)
    {
        schemeName = stripToNull(schemeName);
        if (schemeName != null)
        {
            AssignableWorkflowScheme workflowSchemeObj = workflowSchemeManager.getWorkflowSchemeObj(schemeName);
            if (workflowSchemeObj != null)
            {
                return ok(workflowSchemeObj);
            }
            else
            {
                return fourOhfour();
            }
        }

        projectKey = stripToNull(projectKey);
        if (projectKey == null)
        {
            projectName = stripToNull(projectName);
            if (projectName == null)
            {
                return getAllSchemes();
            }
            else
            {
                return schemeForProject(projectManager.getProjectObjByName(projectName), getDraft);
            }
        }
        else
        {
            return schemeForProject(projectManager.getProjectObjByKey(projectKey), getDraft);
        }
    }

    @Path("{id}")
    @GET
    public Response getWorkflowScheme(@PathParam("id") long id)
    {
        final AssignableWorkflowScheme workflowSchemeObj = workflowSchemeManager.getWorkflowSchemeObj(id);
        if (workflowSchemeObj == null)
        {
            return fourOhfour();
        }
        else
        {
            return Response.ok(dataFactory.toData(workflowSchemeObj)).cacheControl(never()).build();
        }
    }

    @PUT
    @Path("{id}")
    public Response updateWorkflowScheme(WorkflowSchemeData data, @PathParam("id") long id)
    {
        final AssignableWorkflowScheme workflowSchemeObj = workflowSchemeManager.getWorkflowSchemeObj(id);
        if (workflowSchemeObj == null)
        {
            return fourOhfour();
        }
        else
        {
            AssignableWorkflowScheme scheme = workflowSchemeManager.updateWorkflowScheme(dataFactory.schemeFromData(data, workflowSchemeObj.builder()));
            return Response.ok(dataFactory.toData(scheme)).cacheControl(never()).build();
        }
    }

    @Path("{id}/draft")
    @GET
    public Response getDraftWorkflowScheme(@PathParam("id") long id)
    {
        final AssignableWorkflowScheme workflowSchemeObj = workflowSchemeManager.getWorkflowSchemeObj(id);
        if (workflowSchemeObj == null)
        {
            return fourOhfour();
        }
        else
        {
            final DraftWorkflowScheme draftForParent = workflowSchemeManager.getDraftForParent(workflowSchemeObj);
            if (draftForParent == null)
            {
                return fourOhfour();
            }
            else
            {
                return Response.ok(dataFactory.toData(draftForParent)).cacheControl(never()).build();
            }
        }
    }

    @Path("{id}/draft")
    @PUT
    public Response createDraftScheme(@PathParam("id") long id)
    {
        final AssignableWorkflowScheme workflowSchemeObj = workflowSchemeManager.getWorkflowSchemeObj(id);
        if (workflowSchemeObj == null)
        {
            return fourOhfour();
        }
        else
        {
            final ApplicationUser user = ApplicationUsers.from(context.getLoggedInUser());
            final DraftWorkflowScheme draftForParent = workflowSchemeManager.createDraftOf(user, workflowSchemeObj);
            return Response.ok(dataFactory.toData(draftForParent)).cacheControl(never()).build();
        }
    }

    @Path("{id}/draft")
    @POST
    public Response updateDraftScheme(@PathParam("id") long id, WorkflowSchemeData data)
    {
        final AssignableWorkflowScheme workflowSchemeObj = workflowSchemeManager.getWorkflowSchemeObj(id);
        if (workflowSchemeObj == null)
        {
            return fourOhfour();
        }
        else
        {
            final DraftWorkflowScheme draftForParent = workflowSchemeManager.getDraftForParent(workflowSchemeObj);
            if (draftForParent == null)
            {
                return fourOhfour();
            }

            final ApplicationUser user = ApplicationUsers.from(context.getLoggedInUser());
            DraftWorkflowScheme draftWorkflowScheme
                    = workflowSchemeManager.updateDraftWorkflowScheme(user, dataFactory.draftFromData(data, draftForParent));

            return Response.ok(dataFactory.toData(draftWorkflowScheme)).cacheControl(never()).build();
        }
    }

    @Path("{id}/draft")
    @DELETE
    public Response deleteDraftScheme(@PathParam("id") long id)
    {
        final AssignableWorkflowScheme workflowSchemeObj = workflowSchemeManager.getWorkflowSchemeObj(id);
        if (workflowSchemeObj == null)
        {
            return fourOhfour();
        }
        else
        {
            final DraftWorkflowScheme draftForParent = workflowSchemeManager.getDraftForParent(workflowSchemeObj);
            if (draftForParent == null)
            {
                return fourOhfour();
            }

            if (!workflowSchemeManager.deleteWorkflowScheme(draftForParent))
            {
                return fourOhfour();
            }
            else
            {
                return ok();
            }
        }
    }

    @Path("{id}")
    @DELETE
    public Response deleteWorkflowScheme(@PathParam("id") long id)
    {
        final AssignableWorkflowScheme workflowSchemeObj = workflowSchemeManager.getWorkflowSchemeObj(id);
        if (workflowSchemeObj == null)
        {
            return fourOhfour();
        }
        else
        {
            workflowSchemeManager.deleteWorkflowScheme(workflowSchemeObj);
            return ok();
        }
    }

    @PUT
    public Response createWorkflowScheme(WorkflowSchemeData data)
    {
        AssignableWorkflowScheme scheme = workflowSchemeManager.createScheme(dataFactory.schemeFromData(data, workflowSchemeManager.assignableBuilder()));
        return Response.ok(dataFactory.toData(scheme)).cacheControl(never()).build();
    }

    private static Response fourOhfour()
    {
        return Response.status(Response.Status.NOT_FOUND).cacheControl(CacheControl.never()).build();
    }

    private Response schemeForProject(Project project, boolean getDraft)
    {
        if (project == null)
        {
            return fourOhfour();
        }

        final AssignableWorkflowScheme projectScheme = workflowSchemeManager.getWorkflowSchemeObj(project);
        final WorkflowScheme scheme = getDraft ? workflowSchemeManager.getDraftForParent(projectScheme) : projectScheme;

        if (scheme == null)
        {
            return fourOhfour();
        }

        return ok(scheme);
    }

    private Response ok(WorkflowScheme scheme)
    {
        return Response.ok(dataFactory.toData(scheme))
                .cacheControl(CacheControl.never()).build();
    }

    private static Response ok()
    {
        return Response.ok().cacheControl(never()).build();
    }

    private Response getAllSchemes()
    {
        Iterable<WorkflowSchemeData> schemeObjects = Iterables.transform(workflowSchemeManager.getSchemeObjects(), dataFactory.fromSchemeToDataFunction());
        return Response.ok(schemeObjects).cacheControl(never()).build();
    }
}
