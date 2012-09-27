package com.atlassian.jira.testkit.plugin.workflows;

import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeStyle;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.scheme.SchemeEntity;
import com.atlassian.jira.testkit.beans.WorkflowSchemeData;
import com.atlassian.jira.workflow.DraftWorkflowScheme;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowScheme;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @since v5.2
 */
public class WorkflowSchemeDataFactoryImpl implements WorkflowSchemeDataFactory
{
    private final IssueTypeManager issueTypeManager;
    private final WorkflowSchemeManager workflowSchemeManager;
    private final DateTimeFormatter formatter;

    WorkflowSchemeDataFactoryImpl(IssueTypeManager issueTypeManager, WorkflowSchemeManager workflowSchemeManager,
            DateTimeFormatter formatter)
    {
        this.issueTypeManager = issueTypeManager;
        this.workflowSchemeManager = workflowSchemeManager;
        this.formatter = formatter.withStyle(DateTimeStyle.RELATIVE).withLocale(Locale.ENGLISH);
    }

    @Override
    public WorkflowSchemeData toData(WorkflowScheme scheme)
    {
        String defaultWorkflow = null;
        Map<String, String> map = Maps.newHashMap();
        for (Map.Entry<String, String> entry : scheme.getMappings().entrySet())
        {
            String key = entry.getKey();
            if (key != null)
            {
                key = issueTypeManager.getIssueType(key).getName();
                map.put(key, entry.getValue());
            }
            else
            {
                defaultWorkflow = entry.getValue();
            }
        }

        final WorkflowSchemeData data = new WorkflowSchemeData().setId(scheme.getId())
                .setName(scheme.getName()).setDescription(scheme.getDescription())
                .setMappings(map).setDefaultWorkflow(defaultWorkflow).setDraft(scheme.isDraft())
                .setActive(workflowSchemeManager.isActive(scheme));

        if (scheme instanceof DraftWorkflowScheme)
        {
            DraftWorkflowScheme draftWorkflowScheme = (DraftWorkflowScheme) scheme;
            data.setLastModified(formatter.format(draftWorkflowScheme.getLastModifiedDate()));
            data.setLastModifiedUser(draftWorkflowScheme.getLastModifiedUser().getName());
        }
        return data;
    }

    @Override
    public WorkflowSchemeData toData(Scheme scheme)
    {
        Map<String, String> mappings = Maps.newHashMap();
        Collection<SchemeEntity> entities = scheme.getEntities();
        String defaultWorkflow =null;
        for (SchemeEntity entity : entities)
        {
            String parameter = entity.getParameter();
            if (parameter == null || "0".equals(parameter))
            {
                defaultWorkflow = entity.getEntityTypeId().toString();
            }
            else
            {
                IssueType object = issueTypeManager.getIssueType(parameter);
                mappings.put(object.getName(), entity.getEntityTypeId().toString());
            }
        }
        if (defaultWorkflow == null)
        {
            defaultWorkflow = JiraWorkflow.DEFAULT_WORKFLOW_NAME;
        }

        return new WorkflowSchemeData().setId(scheme.getId())
                .setName(scheme.getName()).setDescription(scheme.getDescription())
                .setMappings(mappings).setDefaultWorkflow(defaultWorkflow).setDraft(false)
                .setActive(workflowSchemeManager.getProjects(scheme).isEmpty());
    }

    Function<Scheme, WorkflowSchemeData> fromSchemeToDataFunction()
    {
        return new Function<Scheme, WorkflowSchemeData>()
        {
            @Override
            public WorkflowSchemeData apply(Scheme scheme) {
                return toData(scheme);
            }
        };
    }

    Scheme schemeFromData(WorkflowSchemeData data)
    {
        List<SchemeEntity> entity = Lists.newArrayList();
        for (Map.Entry<String, String> value : data.getMappings().entrySet())
        {
            entity.add(new SchemeEntity(value.getValue(), findIssueType(value.getKey())));
        }
        if (data.getDefaultWorkflow() != null)
        {
            entity.add(new SchemeEntity(data.getDefaultWorkflow(), "0"));
        }

        return new Scheme(null, null, data.getName(), data.getDescription(), entity);
    }

    DraftWorkflowScheme draftFromData(WorkflowSchemeData data, DraftWorkflowScheme current)
    {
        DraftWorkflowScheme.Builder builder = current.builder().clearMappings();
        if (data.getDefaultWorkflow() != null)
        {
            builder.setDefaultWorkflow(data.getDefaultWorkflow());
        }

        for (Map.Entry<String, String> entry : data.getMappings().entrySet())
        {
            builder.setMapping(findIssueType(entry.getKey()), entry.getValue());
        }
        return builder.build();
    }

    private String findIssueType(String type)
    {
        IssueType obj = issueTypeManager.getIssueType(type);
        if (obj == null)
        {
            for (IssueType issueType : issueTypeManager.getIssueTypes())
            {
                if (issueType.getName().equals(type))
                {
                    obj = issueType;
                    break;
                }
            }
        }

        if (obj == null)
        {
            throw new IllegalArgumentException("Unable to find IssueType with id or name of '" + type + "'.");
        }
        return obj.getId();
    }
}
