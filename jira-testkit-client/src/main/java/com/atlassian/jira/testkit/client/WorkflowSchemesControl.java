/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.beans.WorkflowSchemeData;
import com.atlassian.jira.util.Function;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import java.util.List;

/**
 * Used to query workflow schemes during the functional tests.
 *
 * @since v5.1
 */
public class WorkflowSchemesControl extends BackdoorControl<WorkflowSchemesControl>
{
    public WorkflowSchemesControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public WorkflowSchemeData getWorkflowSchemeByProjectKey(String projectKey)
    {
        final WebResource workflowSchemeResource = createWorkflowSchemeResource();
        return workflowSchemeResource.queryParam("projectKey", projectKey).get(WorkflowSchemeData.class);
    }

    public WorkflowSchemeData getWorkflowSchemeByProjectName(String projectName)
    {
        WebResource workflowSchemeResource = createWorkflowSchemeResource();
        return workflowSchemeResource.queryParam("projectName", projectName).get(WorkflowSchemeData.class);
    }

    public WorkflowSchemeData getWorkflowSchemeByName(String schemeName)
    {
        final WebResource workflowSchemeResource = createWorkflowSchemeResource();
        return workflowSchemeResource.queryParam("schemeName", schemeName).get(WorkflowSchemeData.class);
    }

    public WorkflowSchemeData getWorkflowSchemeByNameNullIfNotFound(final String schemeName)
    {
        return nullIfNotFound(new Function<Void, WorkflowSchemeData>()
        {
            @Override
            public WorkflowSchemeData get(Void input)
            {
                return getWorkflowSchemeByName(schemeName);
            }
        });
    }

    public WorkflowSchemeData getWorkflowSchemeDraftByProjectNameNullIfNotFound(final String projectName)
    {
        return nullIfNotFound(new Function<Void, WorkflowSchemeData>()
        {
            @Override
            public WorkflowSchemeData get(Void input)
            {
                return getWorkflowSchemeDraftByProjectName(projectName);
            }
        });
    }

    public WorkflowSchemeData getWorkflowSchemeDraftByProjectName(String projectName)
    {
        WebResource workflowSchemeResource = createWorkflowSchemeResource();
        return workflowSchemeResource.queryParam("projectName", projectName).queryParam("draft", "true").get(WorkflowSchemeData.class);
    }

    public WorkflowSchemeData getWorkflowSchemeDraftByProjectKey(String projectKey)
    {
        WebResource workflowSchemeResource = createWorkflowSchemeResource();
        return workflowSchemeResource.queryParam("projectKey", projectKey).queryParam("draft", "true").get(WorkflowSchemeData.class);
    }

    public List<WorkflowSchemeData> getWorkflowSchemes()
    {
        return createWorkflowSchemeResource().get(WorkflowSchemeData.LIST);
    }

    public WorkflowSchemeData getWorkflowScheme(long id)
    {
        return createWorkflowSchemeResource(id).get(WorkflowSchemeData.class);
    }

    public WorkflowSchemeData getWorkflowSchemeForParent(long id)
    {
        return createDraftWorkflowSchemeResource(id).get(WorkflowSchemeData.class);
    }

    public WorkflowSchemeData getWorkflowSchemeForParentNullIfNotFound(final long id)
    {
        return nullIfNotFound(new Function<Void, WorkflowSchemeData>()
        {
            @Override
            public WorkflowSchemeData get(Void aVoid)
            {
                return getWorkflowSchemeForParent(id);
            }
        });
    }

    public WorkflowSchemeData createDraft(WorkflowSchemeData scheme)
    {
        final WebResource workflowSchemeResource = createWorkflowSchemeResource(scheme.getId());
        return workflowSchemeResource.path("draft").put(WorkflowSchemeData.class, scheme.getId());
    }

    public WorkflowSchemeData createScheme(WorkflowSchemeData scheme)
    {
        final WebResource workflowSchemeResource = createWorkflowSchemeResource();
        return workflowSchemeResource.put(WorkflowSchemeData.class, scheme);
    }

    public WorkflowSchemeData updateScheme(WorkflowSchemeData scheme)
    {
        final WebResource workflowSchemeResource = createWorkflowSchemeResource(scheme.getId());
        return workflowSchemeResource.put(WorkflowSchemeData.class, scheme);
    }

    public void deleteScheme(long id)
    {
        final WebResource workflowSchemeResource = createWorkflowSchemeResource(id);
        workflowSchemeResource.delete();
    }

    public WorkflowSchemeData createDraftScheme(long parentId)
    {
        final WebResource workflowSchemeResource = createDraftWorkflowSchemeResource(parentId);
        return workflowSchemeResource.put(WorkflowSchemeData.class, parentId);
    }

    public WorkflowSchemeData updateDraftScheme(long parentId, WorkflowSchemeData data)
    {
        final WebResource workflowSchemeResource = createDraftWorkflowSchemeResource(parentId);
        return workflowSchemeResource.post(WorkflowSchemeData.class, data);
    }

    public void discardDraftScheme(long parentId)
    {
        final WebResource workflowSchemeResource = createDraftWorkflowSchemeResource(parentId);
        workflowSchemeResource.delete();
    }

    private WebResource createWorkflowSchemeResource(long id)
    {
        return createWorkflowSchemeResource().path(String.valueOf(id));
    }

    private WebResource createDraftWorkflowSchemeResource(long id)
    {
        return createWorkflowSchemeResource(id).path("draft");
    }

    private WebResource createWorkflowSchemeResource()
    {
        return createResource().path("workflowscheme");
    }

    private WorkflowSchemeData nullIfNotFound(Function<Void, WorkflowSchemeData> function)
    {
        try
        {
            return function.get(null);
        }
        catch (UniformInterfaceException e)
        {
            if (ClientResponse.Status.NOT_FOUND.getStatusCode() == e.getResponse().getStatus())
            {
                return null;
            }

            throw e;
        }
    }
}
