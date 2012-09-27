package com.atlassian.jira.testkit.plugin.workflows;

import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.testkit.beans.WorkflowSchemeData;
import com.atlassian.jira.workflow.WorkflowScheme;

/**
 * @since v5.2
 */
public interface WorkflowSchemeDataFactory
{
    WorkflowSchemeData toData(WorkflowScheme scheme);

    WorkflowSchemeData toData(Scheme scheme);
}
