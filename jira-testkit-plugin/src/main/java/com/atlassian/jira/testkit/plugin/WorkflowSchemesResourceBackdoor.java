package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.scheme.SchemeEntity;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowScheme;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;

/**
 * Used to manipulate workflows during functional tests.
 *
 * @since v5.1
 */
@AnonymousAllowed
@Produces ({MediaType.APPLICATION_JSON})
@Consumes ({MediaType.APPLICATION_JSON})
@Path ("workflowschemes")
public class WorkflowSchemesResourceBackdoor
{
    private final WorkflowSchemeManager workflowSchemeManager;
    private final ConstantsManager constantsManager;
    private final ProjectManager projectManager;

    public WorkflowSchemesResourceBackdoor(WorkflowSchemeManager workflowSchemeManager,
            ConstantsManager constantsManager, ProjectManager projectManager)
    {
        this.workflowSchemeManager = workflowSchemeManager;
        this.constantsManager = constantsManager;
        this.projectManager = projectManager;
    }

    @GET
    public Response getWorkflowScheme(@QueryParam ("projectKey") String projectKey,
            @QueryParam ("projectName") String projectName, @QueryParam ("draft") boolean getDraft)
    {
        projectKey = StringUtils.stripToNull(projectKey);
        if (projectKey == null)
        {
            projectName = StringUtils.stripToNull(projectName);
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

    private static Response fourOhfour()
    {
        return Response.status(Response.Status.NOT_FOUND).cacheControl(never()).build();
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

        final Map<String, String> workflowMap = new HashMap<String, String>(scheme.getMappings());
        String defaultWorkflow = workflowMap.remove(null);
        if (defaultWorkflow == null)
        {
            defaultWorkflow = JiraWorkflow.DEFAULT_WORKFLOW_NAME;
        }

        return Response.ok(new WorkflowSchemeData(scheme.getId(), scheme.getName(), workflowMap, defaultWorkflow))
                .cacheControl(never()).build();
    }

    private Response getAllSchemes()
    {
        List<Scheme> schemeObjects = workflowSchemeManager.getSchemeObjects();
        List<WorkflowSchemeData> str = Lists.newArrayListWithCapacity(schemeObjects.size());

        for (Scheme scheme : schemeObjects)
        {
            String name = scheme.getName();
            Map<String, String> mappings = Maps.newHashMap();
            Collection<SchemeEntity> entities = scheme.getEntities();
            String defaultWorkflow =null;
            for (SchemeEntity entity : entities)
            {
                String parameter = entity.getParameter();
                if (parameter == null || "0".equals(parameter))
                {
                    defaultWorkflow = entity.getEntityTypeId().toString();
                }
                else
                {
                    IssueType object = constantsManager.getIssueTypeObject(parameter);
                    mappings.put(object.getName(), entity.getEntityTypeId().toString());
                }
            }
            if (defaultWorkflow == null)
            {
                defaultWorkflow = JiraWorkflow.DEFAULT_WORKFLOW_NAME;
            }

            str.add(new WorkflowSchemeData(scheme.getId(), name, mappings, defaultWorkflow));
        }
        return Response.ok(str).cacheControl(never()).build();
    }

    @JsonAutoDetect
    private static class WorkflowSchemeData
    {
        @JsonProperty
        private Long id;

        @JsonProperty
        private String name;

        @JsonProperty
        private String defaultWorkflow;

        @JsonProperty
        private Map<String, String> mapping;

        private WorkflowSchemeData(Long id, String name, Map<String, String> mapping, String defaultWorkflow)
        {
            this.id = id;
            this.name = name;
            this.mapping = mapping;
            this.defaultWorkflow = defaultWorkflow;
        }
    }
}
