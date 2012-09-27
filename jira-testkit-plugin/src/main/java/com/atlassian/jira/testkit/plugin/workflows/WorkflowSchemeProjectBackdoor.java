package com.atlassian.jira.testkit.plugin.workflows;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;

/**
 *
 * @since v5.2
 */
@AnonymousAllowed
@Produces ({ MediaType.APPLICATION_JSON})
@Consumes ({MediaType.APPLICATION_JSON})
@Path("project/{projectKey}/workflowscheme")
public class WorkflowSchemeProjectBackdoor
{
    private final WorkflowSchemeManager workflowSchemeManager;
    private final WorkflowSchemeDataFactory dataFactory;
    private final ProjectManager projectManager;

    public WorkflowSchemeProjectBackdoor(WorkflowSchemeManager workflowSchemeManager,
                                         WorkflowSchemeDataFactory dataFactory,
                                         ProjectManager projectManager)
    {
        this.workflowSchemeManager = workflowSchemeManager;
        this.dataFactory = dataFactory;
        this.projectManager = projectManager;
    }

    @GET
    public Response get(@PathParam("projectKey") String projectKey)
    {
        Project project = getProject(projectKey);
        if (project == null)
        {
            return fourOhFour();
        }
        final AssignableWorkflowScheme workflowSchemeObj = workflowSchemeManager.getWorkflowSchemeObj(project);
        return ok(workflowSchemeObj);
    }

    protected final Response ok(AssignableWorkflowScheme workflowSchemeObj)
    {
        return Response.ok(dataFactory.toData(workflowSchemeObj)).cacheControl(never()).build();
    }

    protected final Project getProject(String projectKey)
    {
        return projectManager.getProjectObjByKey(projectKey);
    }

    private static Response fourOhFour()
    {
        return Response.status(Response.Status.NOT_FOUND).cacheControl(never()).build();
    }
}
