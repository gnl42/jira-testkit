/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import java.util.List;

/**
 * Hooks into the WorkflowResource within the func-test plugin.
 *
 * See <code>com.atlassian.jira.testkit.plugin.WorkflowResourceBackdoor</code> in jira-testkit-plugin for backend.
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
