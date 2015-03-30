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
import com.atlassian.jira.config.LocaleManager;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.issue.IssueConstant;
import com.atlassian.jira.testkit.plugin.util.Errors;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.web.action.admin.translation.TranslationManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;
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
    private final static Map<String, String> TRANSLATION_PREFIXES = new ImmutableMap.Builder<String, String>()
            .put(ConstantsManager.ISSUE_TYPE_CONSTANT_TYPE, "jira.translation.issuetype")
            .put(ConstantsManager.PRIORITY_CONSTANT_TYPE, "jira.translation.priority")
            .put(ConstantsManager.RESOLUTION_CONSTANT_TYPE, "jira.translation.resolution")
            .put(ConstantsManager.STATUS_CONSTANT_TYPE, "jira.translation.status")
            .build();

    private final static String SUBTASK = SubTaskManager.SUB_TASK_ISSUE_TYPE_STYLE; 
    private final static String TASK = ""; 
    
    private final ConstantsManager constantsManager;
    private final IssueTypeManager issueTypeManager;
    private final TranslationManager translationManager;
    private final LocaleManager localeManager;
    
    public IssueTypeBackdoorResource(ConstantsManager constantsManager, IssueTypeManager issueTypeManager, TranslationManager translationManager, LocaleManager localeManager)
    {
        this.constantsManager = constantsManager;
        this.issueTypeManager = issueTypeManager;
        this.translationManager = translationManager;
        this.localeManager = localeManager;
    }

    @GET
    public Response getAllTypes()
    {
        final Collection<IssueType> issueTypes = constantsManager.getAllIssueTypeObjects();
        final List<IssueTypeBean> issueTypeBeans = Lists.newArrayList();
        for (IssueType issueType : issueTypes)
        {
            issueTypeBeans.add(new IssueTypeBean(issueType));
        }
        return Response.ok(issueTypeBeans).cacheControl(never()).build();
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

    @PUT
    @Path ("/translateConstants")
    public Response translateConstants(IssueConstantTranslationBean issueConstantBean)
    {
        if (!TRANSLATION_PREFIXES.containsKey(issueConstantBean.constantType))
        {
            return Response.status(Response.Status.BAD_REQUEST).cacheControl(never()).build();
        }

        if (StringUtils.isBlank(issueConstantBean.name) || StringUtils.isBlank(issueConstantBean.description))
        {
            return Response.status(Response.Status.BAD_REQUEST).cacheControl(never()).build();
        }

        final Locale locale = localeManager.getLocale(issueConstantBean.locale);
        final IssueConstant issueConstant = constantsManager.getIssueConstantByName(issueConstantBean.constantType, issueConstantBean.constantName);

        if (locale == null || issueConstant == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).cacheControl(never()).build();
        }

        translationManager.setIssueConstantTranslation(issueConstant, TRANSLATION_PREFIXES.get(issueConstantBean.constantType), locale, issueConstantBean.name, issueConstantBean.description);

        return Response.ok().build();
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

    public static class IssueConstantTranslationBean
    {
        @JsonProperty
        private String constantName;

        @JsonProperty
        private String locale;

        @JsonProperty
        private String name;

        @JsonProperty
        private String description;

        @JsonProperty
        private String constantType;

        public IssueConstantTranslationBean()
        {
        }
    }
}
