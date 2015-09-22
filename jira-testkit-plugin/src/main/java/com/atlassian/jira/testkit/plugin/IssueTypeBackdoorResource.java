/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.testkit.plugin.util.Errors;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.trimToNull;

/**
 * @since v5.0.1
 */
@Path ("issueType")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class IssueTypeBackdoorResource
{
    private final static String SUBTASK = SubTaskManager.SUB_TASK_ISSUE_TYPE_STYLE; 
    private final static String TASK = ""; 
    
    private final ConstantsManager constantsManager;
    private final IssueTypeManager issueTypeManager;
    private final ProjectManager projectManager;
    private final IssueTypeSchemeManager issueTypeSchemeManager;

    public IssueTypeBackdoorResource(
            final ConstantsManager constantsManager,
            final IssueTypeManager issueTypeManager,
            final ProjectManager projectManager,
            final IssueTypeSchemeManager issueTypeSchemeManager)
    {
        this.constantsManager = constantsManager;
        this.issueTypeManager = issueTypeManager;
        this.projectManager = projectManager;
        this.issueTypeSchemeManager = issueTypeSchemeManager;
    }

    @GET
    @Path("project/{projectIdOrKey}")
    public Response getIssueTypesForProject(@PathParam("projectIdOrKey") String projectIdOrKey)
    {
        Project project;
        if (StringUtils.isNumeric(projectIdOrKey))
        {
            project = projectManager.getProjectObj(Long.valueOf(projectIdOrKey));
        }
        else
        {
            project = projectManager.getProjectObjByKey(projectIdOrKey);
        }

        if (project == null)
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(toBeans(issueTypeSchemeManager.getIssueTypesForProject(project))).cacheControl(never()).build();
    }

    @GET
    public Response getAllTypes()
    {
        return Response.ok(toBeans(constantsManager.getAllIssueTypeObjects())).cacheControl(never()).build();
    }

    private List<IssueTypeBean> toBeans(final Collection<IssueType> issueTypes)
    {
        return issueTypes.stream().map(IssueTypeBean::new).collect(toList());
    }
    
    @POST
    public Response createIssueType(IssueTypeBean bean)
    {
        final String style = bean.subtask ? SUBTASK : TASK;        
        SimpleErrorCollection collections = new SimpleErrorCollection();
        constantsManager.validateCreateIssueType(bean.name, style, bean.description, bean.iconUrl, collections, "name");

        if (collections.hasAnyErrors())
        {
            return Response.status(Response.Status.BAD_REQUEST).cacheControl(never()).entity(Errors.of(collections)).build();
        }
        else
        {
            IssueType type;
            if (bean.subtask)
            {
                type = issueTypeManager.createSubTaskIssueType(bean.name, bean.description, bean.iconUrl);
            }
            else
            {
                type = issueTypeManager.createIssueType(bean.name, bean.description, bean.iconUrl);
            }
            return Response.ok(new IssueTypeBean(type)).cacheControl(never()).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteIssueType(@PathParam("id") long id)
    {
        issueTypeManager.removeIssueType(String.valueOf(id), null);
        return Response.ok().cacheControl(never()).build();
    }

    public static class IssueTypeBean
    {
        @JsonProperty
        private String id;

        @JsonProperty
        private String description;

        @JsonProperty
        private String iconUrl;

        @JsonProperty
        private String name;

        @JsonProperty
        private boolean subtask;

        public IssueTypeBean()
        {
        }

        public IssueTypeBean(IssueType type)
        {
            id = trimToNull(type.getId());
            name = trimToNull(type.getName());
            iconUrl = trimToNull(type.getIconUrl());
            description = trimToNull(type.getDescription());
            subtask = type.isSubTask();
        }
    }
}
