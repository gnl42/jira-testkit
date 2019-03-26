/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.context.ProjectContext;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.issue.fields.layout.field.EditableDefaultFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.ImmutableList;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * A backdoor for manipulating field configurations. There is more functionality available in JIRA's Func Tests.
 *
 * @since v5.0.1
 * @author mtokar
 */
@Path("fieldConfiguration")
public class FieldConfigurationBackdoor
{
    private static final int DEFAULT_FIELD_LAYOUT_ID = 10000;

    private final FieldLayoutManager fieldLayoutManager;
    private final CustomFieldManager customFieldManager;
    private final ProjectManager projectManager;
    private final FieldConfigSchemeManager fieldConfigSchemeManager;

    public FieldConfigurationBackdoor(FieldLayoutManager fieldLayoutManager, CustomFieldManager customFieldManager,
            ProjectManager projectManager, FieldConfigSchemeManager fieldConfigSchemeManager)
    {
        this.fieldLayoutManager = fieldLayoutManager;
        this.customFieldManager = customFieldManager;
        this.projectManager = projectManager;
        this.fieldConfigSchemeManager = fieldConfigSchemeManager;
    }

    @GET
    @AnonymousAllowed
    @Path("hideField")
    public Response hideField(@QueryParam("name") String fieldConfigName, @QueryParam("fieldId") String fieldId)
    {
        EditableFieldLayout fieldLayout = getFieldLayout(fieldConfigName);

        if (fieldLayout.getId() == null || fieldLayout.getId() == DEFAULT_FIELD_LAYOUT_ID) {
            final EditableDefaultFieldLayout editableDefaultFieldLayout = fieldLayoutManager.getEditableDefaultFieldLayout();
            editableDefaultFieldLayout.hide(editableDefaultFieldLayout.getFieldLayoutItem(fieldId));
            fieldLayoutManager.storeEditableDefaultFieldLayout(editableDefaultFieldLayout);
        } else {
            fieldLayout.hide(fieldLayout.getFieldLayoutItem(fieldId));
            fieldLayoutManager.storeEditableFieldLayout(fieldLayout);
        }

        return Response.ok().build();
    }

    @GET
    @AnonymousAllowed
    @Path("showField")
    public Response showField(@QueryParam("name") String fieldConfigName, @QueryParam("fieldId") String fieldId)
    {
        EditableFieldLayout fieldLayout = getFieldLayout(fieldConfigName);

        if (fieldLayout.getId() == null || fieldLayout.getId() == DEFAULT_FIELD_LAYOUT_ID) {
            final EditableDefaultFieldLayout editableDefaultFieldLayout = fieldLayoutManager.getEditableDefaultFieldLayout();
            editableDefaultFieldLayout.show(editableDefaultFieldLayout.getFieldLayoutItem(fieldId));
            fieldLayoutManager.storeEditableDefaultFieldLayout(editableDefaultFieldLayout);
        } else {
            fieldLayout.show(fieldLayout.getFieldLayoutItem(fieldId));
            fieldLayoutManager.storeEditableFieldLayout(fieldLayout);
        }
        return Response.ok().build();
    }

    @GET
    @AnonymousAllowed
    @Path("makeFieldOptional")
    public Response makeOptional(@QueryParam("name") String fieldConfigName, @QueryParam("fieldId") String fieldId)
    {
        EditableFieldLayout fieldLayout = getFieldLayout(fieldConfigName);

        if (fieldLayout.getId() == null || fieldLayout.getId() == DEFAULT_FIELD_LAYOUT_ID) {
            final EditableDefaultFieldLayout editableDefaultFieldLayout = fieldLayoutManager.getEditableDefaultFieldLayout();
            editableDefaultFieldLayout.makeOptional(editableDefaultFieldLayout.getFieldLayoutItem(fieldId));
            fieldLayoutManager.storeEditableDefaultFieldLayout(editableDefaultFieldLayout);
        } else {
            fieldLayout.makeOptional(fieldLayout.getFieldLayoutItem(fieldId));
            fieldLayoutManager.storeEditableFieldLayout(fieldLayout);
        }

        return Response.ok().build();
    }

    @GET
    @AnonymousAllowed
    @Path("makeFieldRequired")
    public Response makeRequired(@QueryParam("name") String fieldConfigName, @QueryParam("fieldId") String fieldId)
    {
        EditableFieldLayout fieldLayout = getFieldLayout(fieldConfigName);

        if (fieldLayout.getId() == null || fieldLayout.getId() == DEFAULT_FIELD_LAYOUT_ID) {
            final EditableDefaultFieldLayout editableDefaultFieldLayout = fieldLayoutManager.getEditableDefaultFieldLayout();
            editableDefaultFieldLayout.makeRequired(editableDefaultFieldLayout.getFieldLayoutItem(fieldId));
            fieldLayoutManager.storeEditableDefaultFieldLayout(editableDefaultFieldLayout);
        } else {
            fieldLayout.makeRequired(fieldLayout.getFieldLayoutItem(fieldId));
            fieldLayoutManager.storeEditableFieldLayout(fieldLayout);
        }
        return Response.ok().build();
    }
    
    @POST
    @AnonymousAllowed
    @Path("changeFieldVisibility")
    @XsrfProtectionExcluded // Only available during testing.
    public Response changeFieldVisibility(@QueryParam("fieldConfigurationName") String configurationName, @QueryParam("fieldName") String fieldName, @QueryParam("hide") boolean hide)
    {
        final EditableFieldLayout editableFieldLayout = getFieldLayout(configurationName);
        final FieldLayoutItem fieldLayoutItem = editableFieldLayout.getFieldLayoutItem(fieldName);
        if (hide)
        {
            editableFieldLayout.hide(fieldLayoutItem);
        }
        else
        {
            editableFieldLayout.show(fieldLayoutItem);
        }

        if (editableFieldLayout.isDefault())
        {
            fieldLayoutManager.storeEditableDefaultFieldLayout((EditableDefaultFieldLayout) editableFieldLayout);
        }
        else
        {
            fieldLayoutManager.storeEditableFieldLayout(editableFieldLayout);
        }

        return Response.ok().build();
    }

    @POST
    @AnonymousAllowed
    @Path("changeFieldDescription")
    @XsrfProtectionExcluded // Only available during testing.
    public Response changeFieldDescription(@QueryParam("fieldConfigurationName") String configurationName, @QueryParam("fieldName") String fieldName, @QueryParam("description") String description)
    {
        final EditableFieldLayout editableFieldLayout = getFieldLayout(configurationName);
        final FieldLayoutItem fieldLayoutItem = editableFieldLayout.getFieldLayoutItem(fieldName);

        editableFieldLayout.setDescription(fieldLayoutItem, description);

        if (editableFieldLayout.isDefault())
        {
            fieldLayoutManager.storeEditableDefaultFieldLayout((EditableDefaultFieldLayout) editableFieldLayout);
        }
        else
        {
            fieldLayoutManager.storeEditableFieldLayout(editableFieldLayout);
        }

        return Response.ok().build();
    }


    @POST
    @AnonymousAllowed
    @Path("associateCustomFieldWithProject")
    @XsrfProtectionExcluded // Only available during testing.
    public Response associateCustomFieldWithProject(@QueryParam("fieldId") String fieldId, @QueryParam("projectName") String projectName)
    {
        final CustomField customField = customFieldManager.getCustomFieldObject(fieldId);
        final FieldConfigScheme fieldConfigScheme = customField.getConfigurationSchemes().get(0);
        final Project project = projectManager.getProjectObjByName(projectName);
        fieldConfigSchemeManager.updateFieldConfigScheme(fieldConfigScheme, ImmutableList.<JiraContextNode>of(new ProjectContext(project, null)), customField);
        customFieldManager.refreshConfigurationSchemes(customField.getIdAsLong());

        return Response.ok().build();
    }

    protected final EditableFieldLayout getFieldLayout(String fieldConfigName)
    {
        List<EditableFieldLayout> editableFieldLayouts = fieldLayoutManager.getEditableFieldLayouts();
        for (EditableFieldLayout editableFieldLayout : editableFieldLayouts)
        {
            if (fieldConfigName.equalsIgnoreCase(editableFieldLayout.getName()))
            {
                return editableFieldLayout;
            }
        }
        return null;
    }
}
