package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.context.ProjectContext;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.OrderableField;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.issue.fields.layout.field.EditableDefaultFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayoutImpl;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.renderer.DefaultHackyFieldRendererRegistry;
import com.atlassian.jira.issue.fields.renderer.HackyFieldRendererRegistry;
import com.atlassian.jira.issue.fields.renderer.HackyRendererType;
import com.atlassian.jira.issue.fields.renderer.JiraRendererPlugin;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
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
    private final FieldLayoutManager fieldLayoutManager;
    private final CustomFieldManager customFieldManager;
    private final ProjectManager projectManager;
    private final FieldConfigSchemeManager fieldConfigSchemeManager;
    private final RendererManager rendererManager;
    private final JiraAuthenticationContext authenticationContext;
    private final HackyFieldRendererRegistry hackyFieldRendererRegistry;

    public FieldConfigurationBackdoor(FieldLayoutManager fieldLayoutManager, CustomFieldManager customFieldManager, ProjectManager projectManager,
            FieldConfigSchemeManager fieldConfigSchemeManager, RendererManager rendererManager, JiraAuthenticationContext authenticationContext)
    {
        this.fieldLayoutManager = fieldLayoutManager;
        this.customFieldManager = customFieldManager;
        this.projectManager = projectManager;
        this.fieldConfigSchemeManager = fieldConfigSchemeManager;
        this.rendererManager = rendererManager;
        this.authenticationContext = authenticationContext;
        this.hackyFieldRendererRegistry = new DefaultHackyFieldRendererRegistry();
    }

    @GET
    @AnonymousAllowed
    @Path("copy")
    public Response copyFieldConfiguration(@QueryParam("name") String fieldConfigName, @QueryParam("copyName") String newFieldConfigName)
    {
        EditableFieldLayout originalLayout = getFieldLayout(fieldConfigName);
        EditableFieldLayout editableFieldLayout = new EditableFieldLayoutImpl(null, originalLayout.getFieldLayoutItems());
        if (StringUtils.isBlank(newFieldConfigName))
        {
            editableFieldLayout.setName("Copy of " + originalLayout.getName());
        }
        else
        {
            editableFieldLayout.setName(newFieldConfigName);
        }
        editableFieldLayout.setDescription(originalLayout.getDescription());
        fieldLayoutManager.storeEditableFieldLayout(editableFieldLayout);

        return Response.ok().build();
    }

    @GET
    @AnonymousAllowed
    @Path("hideField")
    public Response hideField(@QueryParam("name") String fieldConfigName, @QueryParam("fieldId") String fieldId)
    {
        EditableFieldLayout fieldLayout = getFieldLayout(fieldConfigName);

        if (fieldLayout.getId() == null || fieldLayout.getId() == 10000) {
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

        if (fieldLayout.getId() == null || fieldLayout.getId() == 10000) {
            final EditableDefaultFieldLayout editableDefaultFieldLayout = fieldLayoutManager.getEditableDefaultFieldLayout();
            editableDefaultFieldLayout.show(editableDefaultFieldLayout.getFieldLayoutItem(fieldId));
            fieldLayoutManager.storeEditableDefaultFieldLayout(editableDefaultFieldLayout);
        } else {
            fieldLayout.show(fieldLayout.getFieldLayoutItem(fieldId));
            fieldLayoutManager.storeEditableFieldLayout(fieldLayout);
        }
        return Response.ok().build();
    }
    
    @POST
    @AnonymousAllowed
    @Path("changeFieldVisibility")
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
    @Path("associateCustomFieldWithProject")
    public Response associateCustomFieldWithProject(@QueryParam("fieldId") String fieldId, @QueryParam("projectName") String projectName)
    {
        final CustomField customField = customFieldManager.getCustomFieldObject(fieldId);
        final FieldConfigScheme fieldConfigScheme = customField.getConfigurationSchemes().get(0);
        final Project project = projectManager.getProjectObjByName(projectName);
        fieldConfigSchemeManager.updateFieldConfigScheme(fieldConfigScheme, ImmutableList.<JiraContextNode>of(new ProjectContext(project, null)), customField);
        customFieldManager.refreshConfigurationSchemes(customField.getIdAsLong());

        return Response.ok().build();
    }

    @GET
    @AnonymousAllowed
    @Path("renderer")
    public Response setRenderer(@QueryParam("fieldConfigurationName") String fieldConfigName, @QueryParam("fieldId") final String fieldId,
            @QueryParam("renderer") final String renderer) {
        EditableFieldLayout editableFieldLayout = getFieldLayout(fieldConfigName);
        FieldLayoutItem item = editableFieldLayout.getFieldLayoutItem(fieldId);

        editableFieldLayout.setRendererType(item, getRendererType(item.getOrderableField(), renderer));

        fieldLayoutManager.storeEditableFieldLayout(editableFieldLayout);
        fieldLayoutManager.refresh();
        return Response.ok().build();
    }

    private EditableFieldLayout getFieldLayout(String fieldConfigName)
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

    private String getRendererType(final OrderableField field, final String rendererName) {
        if (hackyFieldRendererRegistry.shouldOverrideDefaultRenderers(field)) {
            return Iterables.getOnlyElement(Iterables.filter(hackyFieldRendererRegistry.getRendererTypes(field), new Predicate<HackyRendererType>()
            {
                @Override
                public boolean apply(@Nullable HackyRendererType input)
                {
                    return StringUtils.equals(rendererName, authenticationContext.getI18nHelper().getText(input.getDisplayNameI18nKey()));
                }
            })).getKey();
        }

        return Iterables.getOnlyElement(Iterables.filter(rendererManager.getAllActiveRenderers(), new Predicate<JiraRendererPlugin>()
        {
            @Override
            public boolean apply(@Nullable JiraRendererPlugin input)
            {
                return StringUtils.equals(rendererName, input.getDescriptor().getName());
            }
        })).getRendererType();
    }

}