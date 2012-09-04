package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import java.util.List;
import java.util.Map;

/**
 * Used to query workflows schemes during the functional tests.
 *
 * @since v5.1
 */
public class WorkflowSchemesControl extends BackdoorControl<WorkflowSchemesControl>
{
    public WorkflowSchemesControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public List<WorkflowScheme> getWorkflowSchemes()
    {
        return get(createWorkflowSchemeResource(), WorkflowScheme.LIST);
    }

    public WorkflowScheme getWorkflowSchemeByProjectKey(String projectKey)
    {
        final WebResource workflowSchemeResource = createWorkflowSchemeResource();
        return get(workflowSchemeResource.queryParam("projectKey", projectKey), WorkflowScheme.class);
    }

    public WorkflowScheme getWorkflowSchemeByProjectName(String projectName)
    {
        WebResource workflowSchemeResource = createWorkflowSchemeResource();
        return get(workflowSchemeResource.queryParam("projectName", projectName), WorkflowScheme.class);
    }

    public WorkflowScheme getWorkflowSchemeDraftByProjectName(String projectName)
    {
        WebResource workflowSchemeResource = createWorkflowSchemeResource();
        return get(workflowSchemeResource.queryParam("projectName", projectName).queryParam("draft", "true"), WorkflowScheme.class);
    }

    public WorkflowScheme getWorkflowSchemeDraftByProjectKey(String projectKey)
    {
        WebResource workflowSchemeResource = createWorkflowSchemeResource();
        return get(workflowSchemeResource.queryParam("projectKey", projectKey).queryParam("draft", "true"), WorkflowScheme.class);
    }

    private WebResource createWorkflowSchemeResource()
    {
        return createResource().path("workflowschemes");
    }

    public static class WorkflowScheme
    {
        private static final GenericType<List<WorkflowScheme>> LIST = new GenericType<List<WorkflowScheme>>(){};

        private Long id;
        private String name;
        private String defaultWorkflow;
        private Map<String, String> mapping;

        public WorkflowScheme()
        {
        }

        public WorkflowScheme(String name, Map<String, String> mapping, String defaultWorkflow)
        {
            this.name = name;
            this.mapping = mapping;
            this.defaultWorkflow = defaultWorkflow;
        }

        public Long getId()
        {
            return id;
        }

        public void setId(Long id)
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getDefaultWorkflow()
        {
            return defaultWorkflow;
        }

        public void setDefaultWorkflow(String defaultWorkflow)
        {
            this.defaultWorkflow = defaultWorkflow;
        }

        public Map<String, String> getMapping()
        {
            return mapping;
        }

        public void setMapping(Map<String, String> mapping)
        {
            this.mapping = mapping;
        }
    }
}
