package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import java.util.List;

/**
 * Hooks into the WorkflowResource within the func-test plugin.
 *
 * See {@link com.atlassian.jira.testkit.plugin.WorkflowResourceBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.1
 */
public class WorkflowsControl extends BackdoorControl<WorkflowsControl>
{
    private static final GenericType<List<String>> LIST_GENERIC_TYPE = new GenericType<List<String>>(){};

    public WorkflowsControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public List<String> getWorkflows()
    {
        return createWorkflowResource().get(LIST_GENERIC_TYPE);
    }

    private WebResource createWorkflowResource()
    {
        return createResource().path("workflow");
    }
}
