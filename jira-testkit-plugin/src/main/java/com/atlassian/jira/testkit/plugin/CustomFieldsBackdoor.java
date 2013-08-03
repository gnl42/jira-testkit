/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.context.manager.JiraContextTreeManager;
import com.atlassian.jira.issue.customfields.CustomFieldSearcher;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.CustomFieldUtils;
import com.atlassian.jira.issue.customfields.config.item.SettableOptionsConfigItem;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItem;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.testkit.beans.CustomFieldConfig;
import com.atlassian.jira.testkit.beans.CustomFieldOption;
import com.atlassian.jira.testkit.beans.CustomFieldRequest;
import com.atlassian.jira.testkit.beans.CustomFieldResponse;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.ofbiz.core.entity.GenericEntityException;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

/**
 * A backdoor for manipulating custom fields.
 *
 * @since v5.1
 */
@Path("customFields")
@Produces ({ MediaType.APPLICATION_JSON})
@Consumes ({MediaType.APPLICATION_JSON})
public class CustomFieldsBackdoor
{
    private final CustomFieldManager customFieldManager;
    private final ConstantsManager manager;
    private final JiraContextTreeManager treeManager;

    public CustomFieldsBackdoor(CustomFieldManager customFieldManager, ConstantsManager manager)
    {
        this.customFieldManager = customFieldManager;
        this.manager = manager;
        this.treeManager = ComponentAccessor.getComponent(JiraContextTreeManager.class);
    }

    @POST
    @AnonymousAllowed
    @Path("create")
    public Response createCustomField(final CustomFieldRequest field)
    {
        final CustomFieldType<?,?> type = customFieldManager.getCustomFieldType(field.type);
        if (type == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Custom field type with key '" + field.type + "' does not exist").build();
        }
        CustomFieldSearcher searcher = null;
        if (field.searcherKey != null)
        {
            final List<CustomFieldSearcher> searchers = customFieldManager.getCustomFieldSearchers(type);
            try{
                searcher = Iterables.find(searchers, new Predicate<CustomFieldSearcher>()
                {
                    @Override
                    public boolean apply(CustomFieldSearcher customFieldSearcher)
                    {
                        return field.searcherKey.equals(customFieldSearcher.getDescriptor().getCompleteKey());
                    }
                });
            }
            catch (NoSuchElementException e)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("Searcher with key " + field.searcherKey
                        + " not found for type '" + field.type + "'").build();
            }
        }
        // global context
        final List<JiraContextNode> contexts = CustomFieldUtils.buildJiraIssueContexts(true, null, null, treeManager);
        final List<IssueType> allTypes = Collections.singletonList(null);
        try
        {
            CustomField result = customFieldManager.createCustomField(field.name, field.description, type, searcher, contexts, allTypes);
            return Response.ok(asResponse(result, false)).cacheControl(never()).build();
        }
        catch (GenericEntityException e)
        {
            throw new IllegalStateException("Something went really wrong", e);
        }
    }

    @GET
    @AnonymousAllowed
    @Path("get")
    public Response getCustomFields(@QueryParam("config") final boolean config)
    {
        return Response.ok(Lists.newArrayList(transform(customFieldManager.getCustomFieldObjects(), new Function<CustomField, Object>()
        {
            @Override
            public CustomFieldResponse apply(final CustomField input)
            {
                return asResponse(input, config);
            }
        }))).cacheControl(never()).build();
    }

    @DELETE
    @AnonymousAllowed
    @Path("delete/{id}")
    public Response deleteCustomField(@PathParam("id") String customFieldId)
    {
        if(customFieldId == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Please supply custom field id").build();
        }

        CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);
        if(customField == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Custom field with id " + customFieldId + " does not exist").build();
        }

        try
        {
            customFieldManager.removeCustomField(customField);
            return Response.ok().build();
        }
        catch (RemoveException e)
        {
            throw new IllegalStateException("Something went wrong: ", e);
        }

    }

    private CustomFieldResponse asResponse(final CustomField input, final boolean config)
    {
        final CustomFieldSearcher customFieldSearcher = input.getCustomFieldSearcher();
        final String searcherKey = customFieldSearcher == null ? null : customFieldSearcher.getDescriptor().getCompleteKey();
        final CustomFieldResponse response = new CustomFieldResponse(input.getName(), input.getId(), input.getCustomFieldType().getKey(), input.getDescription(), searcherKey);
        if (config)
        {
            response.setConfig(getConfig(input));
        }

        return response;
    }

    private List<CustomFieldConfig> getConfig(final CustomField input)
    {
        List<CustomFieldConfig> config = Lists.newArrayList();

        for (FieldConfigScheme fieldConfigScheme : input.getConfigurationSchemes())
        {
            final FieldConfig onlyConfig = fieldConfigScheme.getOneAndOnlyConfig();
            if (onlyConfig != null)
            {
                CustomFieldConfig bean = new CustomFieldConfig();
                bean.setProjects(asSet(getProjectNames(fieldConfigScheme.getAssociatedProjectObjects())));
                bean.setIssueTypes(asSet(getIssueTypeNames(fieldConfigScheme.getConfigs().keySet())));

                for (FieldConfigItem item : onlyConfig.getConfigItems())
                {
                    if (item.getType() instanceof SettableOptionsConfigItem)
                    {
                        bean.setOptions(asList(convertOptions((Options) item.getConfigurationObject(null))));
                    }
                }
                config.add(bean);
            }
        }
        return config;
    }

    private Iterable<CustomFieldOption> convertOptions(Iterable<Option> options)
    {
        return transform(options, new Function<Option, CustomFieldOption>()
        {
            @Override
            public CustomFieldOption apply(@Nullable final Option input)
            {
                return convertOption(input);
            }
        });
    }

    private CustomFieldOption convertOption(Option option)
    {
        final CustomFieldOption customFieldOption = new CustomFieldOption();
        customFieldOption.setId(option.getOptionId());
        customFieldOption.setName(option.getValue());
        customFieldOption.setChildren(asList(convertOptions(option.getChildOptions())));

        return customFieldOption;
    }

    private <T> List<T> asList(Iterable<? extends T> iterable)
    {
        return Lists.newArrayList(iterable);
    }

    private <T> Set<T> asSet(Iterable<? extends T> iterable)
    {
        return Sets.newHashSet(iterable);
    }

    private Iterable<String> getProjectNames(Iterable<Project> projects)
    {
        return transform(filter(projects, Predicates.notNull()), new Function<Project, String>()
        {
            @Override
            public String apply(final Project input)
            {
                return input.getName();
            }
        });
    }

    private Iterable<String> getIssueTypeNames(Iterable<String> ids)
    {
        return transform(filter(ids, Predicates.notNull()), new Function<String, String>()
        {
            @Override
            public String apply(final String input)
            {
                return manager.getIssueTypeObject(input).getName();
            }
        });
    }
}
