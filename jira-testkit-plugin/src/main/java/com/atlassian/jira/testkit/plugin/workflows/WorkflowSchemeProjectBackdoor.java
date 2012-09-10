package com.atlassian.jira.testkit.plugin.workflows;

import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.task.TaskDescriptor;
import com.atlassian.jira.web.action.admin.workflow.WorkflowMigrationResult;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.jira.workflow.migration.AssignableWorkflowMigrationHelper;
import com.atlassian.jira.workflow.migration.MigrationHelperFactory;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.ofbiz.core.entity.GenericEntityException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;

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
    private final MigrationHelperFactory migrationHelperFactory;
    private final WorkflowSchemeDataFactory dataFactory;
    private final ProjectManager projectManager;

    public WorkflowSchemeProjectBackdoor(WorkflowSchemeManager workflowSchemeManager,
                                         MigrationHelperFactory migrationHelperFactory, WorkflowSchemeDataFactory dataFactory,
                                         ProjectManager projectManager)
    {
        this.workflowSchemeManager = workflowSchemeManager;
        this.migrationHelperFactory = migrationHelperFactory;
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

    private Response ok(AssignableWorkflowScheme workflowSchemeObj)
    {
        return Response.ok(dataFactory.toData(workflowSchemeObj)).cacheControl(never()).build();
    }

    @DELETE
    public Response setDefault(@PathParam("projectKey") String projectKey)
    {
        Project project = getProject(projectKey);
        if (project == null)
        {
            return fourOhFour();
        }
        return migrateScheme(workflowSchemeManager.getDefaultWorkflowScheme(), project);
    }

    @POST
    public Response change(@PathParam("projectKey") String projectKey, long id)
    {
        Project project = getProject(projectKey);
        if (project == null)
        {
            return fourOhFour();
        }

        final AssignableWorkflowScheme scheme = workflowSchemeManager.getWorkflowSchemeObj(id);
        if (scheme == null)
        {
            return fourOhFour();
        }

        return migrateScheme(scheme, project);
    }

    private Response migrateScheme(AssignableWorkflowScheme scheme, Project project)
    {
        try
        {
            final AssignableWorkflowMigrationHelper migrationHelper = migrationHelperFactory.createMigrationHelper(project.getGenericValue(), scheme);
            if (!migrationHelper.doQuickMigrate())
            {
                final TaskDescriptor<WorkflowMigrationResult> async = migrationHelper.migrateAsync();
                final WorkflowMigrationResult result = async.getResult();
                if (result.getResult() == WorkflowMigrationResult.SUCCESS && result.getNumberOfFailedIssues() == 0)
                {
                    return ok(scheme);
                }
                else
                {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(String.format("%d issues failed to migrate.", result.getNumberOfFailedIssues()))
                            .cacheControl(never()).build();
                }
            }
            else
            {
                return ok(scheme);
            }
        }
        catch (GenericEntityException e)
        {
            throw new DataAccessException(e);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        catch (ExecutionException e)
        {
            throw new RuntimeException(e.getCause());
        }
    }

    private Project getProject(String projectKey)
    {
        return projectManager.getProjectObjByKey(projectKey);
    }

    private static Response fourOhFour()
    {
        return Response.status(Response.Status.NOT_FOUND).cacheControl(never()).build();
    }
}
