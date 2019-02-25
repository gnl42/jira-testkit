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
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.context.GlobalIssueContext;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.customfields.CustomFieldSearcher;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.CustomFieldUtils;
import com.atlassian.jira.issue.customfields.config.item.SettableOptionsConfigItem;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.ConfigurableField;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItem;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.testkit.beans.CustomFieldConfig;
import com.atlassian.jira.testkit.beans.CustomFieldDefaultValue;
import com.atlassian.jira.testkit.beans.CustomFieldOption;
import com.atlassian.jira.testkit.beans.CustomFieldRequest;
import com.atlassian.jira.testkit.beans.CustomFieldResponse;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;
import org.ofbiz.core.entity.GenericEntityException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;

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
    private final ConstantsManager constantsManager;
    private final FieldConfigSchemeManager fieldConfigSchemeManager;
    private final ProjectManager projectManager;

    public CustomFieldsBackdoor(CustomFieldManager customFieldManager,
                                ConstantsManager constantsManager,
                                FieldConfigSchemeManager fieldConfigSchemeManager,
                                ProjectManager projectManager)
    {
        this.customFieldManager = customFieldManager;
        this.constantsManager = constantsManager;
        this.fieldConfigSchemeManager = fieldConfigSchemeManager;
        this.projectManager = projectManager;
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
                searcher = searchers.stream()
                        .filter(customFieldSearcher -> field.searcherKey.equals(customFieldSearcher.getDescriptor().getCompleteKey()))
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new);
            }
            catch (NoSuchElementException e)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("Searcher with key " + field.searcherKey
                        + " not found for type '" + field.type + "'").build();
            }
        }
        // global context
        final List<JiraContextNode> contexts = Collections.singletonList(GlobalIssueContext.getInstance());
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

    @PUT
    @Path ("{id}")
    public Response updateCustomField(@PathParam ("id") String customFieldId, final CustomFieldRequest field)
    {
        if (customFieldId == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Please supply custom field id").build();
        }

        CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);
        if (customField == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Custom field with id " + customFieldId + " does not exist").build();
        }

        if (field.type != null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Type cannot be changed").build();
        }
        CustomFieldSearcher searcher = null;
        if (field.searcherKey != null)
        {
            final List<CustomFieldSearcher> searchers = customFieldManager.getCustomFieldSearchers(customField.getCustomFieldType());
            try
            {
                searcher = searchers.stream()
                        .filter(customFieldSearcher -> field.searcherKey.equals(customFieldSearcher.getDescriptor().getCompleteKey()))
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new);
            }
            catch (NoSuchElementException e)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("Searcher with key " + field.searcherKey
                        + " not found for type '" + field.type + "'").build();
            }
        }

        customFieldManager.updateCustomField(customField.getIdAsLong(), field.name, field.description, searcher);
        return Response.ok().build();
    }

    @GET
    @Path ("{id}")
    public Response updateCustomField(@PathParam ("id") final String customFieldId, @QueryParam("config") final boolean config)
    {
        if (customFieldId == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Please supply custom field id").build();
        }

        CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);
        if (customField == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Custom field with id " + customFieldId + " does not exist").build();
        }

        return Response.ok(asResponse(customField, config)).build();
    }

    @POST
    @AnonymousAllowed
    @Path("addOption/{id}")
    public Response addOption(@PathParam("id") String customFieldId, String optionValue)
    {
        if(customFieldId == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Please supply custom field id").build();
        }

        final CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);
        if(customField == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Custom field with id " + customFieldId + " does not exist").build();
        }

        final Options options = customField.getOptions(null, GlobalIssueContext.getInstance());
        if (options != null) {
            options.addOption(options.getOptionById(null), optionValue);
        }
        return Response.ok().build();
    }

    @DELETE
    @AnonymousAllowed
    @Path("deleteOption/{id}")
    public Response deleteOption(@PathParam("id") String customFieldId, String optionValue)
    {
        if(customFieldId == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Please supply custom field id").build();
        }

        final CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);
        if(customField == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Custom field with id " + customFieldId + " does not exist").build();
        }

        final Options options = customField.getOptions(null, GlobalIssueContext.getInstance());
        if (options != null)
        {
            Option toDelete = options.getOptionForValue(optionValue, null);
            if (toDelete != null)
            {
                options.removeOption(toDelete);
            }
        }
        return Response.ok().build();
    }

    @GET
    @AnonymousAllowed
    @Path("get")
    public Response getCustomFields(@QueryParam("config") final boolean config)
    {
        final List<CustomFieldResponse> result = customFieldManager.getCustomFieldObjects().stream()
                .map(input -> asResponse(input, config))
                .collect(Collectors.toList());

        return Response.ok(result).cacheControl(never()).build();
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

    @Path("addCustomFieldContext")
    @POST
    public Response addContext(@QueryParam("customFieldId") final Long customFieldId,
                               @QueryParam("projectIds") final List<Long> projectIds,
                               @QueryParam("issueTypeIds") final List<String> issueTypeIds) {
        Long[] projectIdArray = projectIds.toArray(new Long[0]);

        String[] issueTypeIdArray = issueTypeIds.isEmpty() ? new String[]{null} : issueTypeIds.toArray(new String[0]);

        final FieldConfigScheme configScheme = new FieldConfigScheme.Builder(null)
                .setName("New Scheme")
                .setDescription("Dummy description")
                .toFieldConfigScheme();

        final List<JiraContextNode> contexts = CustomFieldUtils.buildJiraIssueContexts(false, projectIdArray, projectManager);
        final List<IssueType> issueTypes = CustomFieldUtils.buildIssueTypes(constantsManager, issueTypeIdArray);
        final CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);

        FieldConfigScheme result = fieldConfigSchemeManager.createFieldConfigScheme(configScheme, contexts, issueTypes, customField);

        return Response.ok(result.getId().toString()).build();
    }

    @Path("deleteCustomFieldContext")
    @DELETE
    public Response deleteContext(@QueryParam("contextId") final Long contextId) {
        fieldConfigSchemeManager.removeFieldConfigScheme(contextId);
        return Response.noContent().build();
    }

    @PUT
    @Path("setDefaultValueForContext")
    public Response updateCustomFieldDefaultValue(@QueryParam("contextId") Long fieldConfigSchemeId,
                                                  final CustomFieldDefaultValue defaultValue) {
        if (fieldConfigSchemeId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Please supply custom field context id (field config scheme id)").build();
        }

        FieldConfigScheme fieldConfigScheme = fieldConfigSchemeManager.getFieldConfigScheme(fieldConfigSchemeId);
        if (fieldConfigScheme == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Field config scheme not found").build();
        }
        final ConfigurableField<?> field = fieldConfigScheme.getField();
        if (!(field instanceof CustomField)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("ConfigurableField associated with field config scheme is not CustomField").build();
        }
        final CustomField customField = (CustomField) field;

        customField.getCustomFieldType().setDefaultValue(fieldConfigScheme.getOneAndOnlyConfig(), defaultValue.value);
        return Response.ok().build();
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
                bean.setId(onlyConfig.getId());
                bean.setProjects(getProjectNames(fieldConfigScheme.getAssociatedProjectObjects()));
                bean.setIssueTypes(getIssueTypeNames(fieldConfigScheme.getConfigs().keySet()));

                for (FieldConfigItem item : onlyConfig.getConfigItems())
                {
                    if (item.getType() instanceof SettableOptionsConfigItem)
                    {
                        bean.setOptions(convertOptions((Options) item.getConfigurationObject(null)));
                    }
                }
                config.add(bean);
            }
        }
        return config;
    }

    private List<CustomFieldOption> convertOptions(List<Option> options)
    {
        return options.stream()
                .map(this::convertOption)
                .collect(Collectors.toList());
    }

    private CustomFieldOption convertOption(Option option)
    {
        final CustomFieldOption customFieldOption = new CustomFieldOption();
        customFieldOption.setId(option.getOptionId());
        customFieldOption.setName(option.getValue());
        customFieldOption.setChildren(convertOptions(option.getChildOptions()));

        return customFieldOption;
    }

    private Set<String> getProjectNames(List<Project> projects)
    {
        return projects.stream()
                .filter(Objects::nonNull)
                .map(Project::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> getIssueTypeNames(Set<String> ids)
    {
        return ids.stream()
                .filter(Objects::nonNull)
                .map(id -> constantsManager.getIssueTypeObject(id).getName())
                .collect(Collectors.toSet());
    }
}
