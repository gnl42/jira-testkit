package com.atlassian.jira.testkit.plugin.issue.fields.layout.field;

import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.testkit.issue.fields.layout.field.FieldLayoutManagerAdapter;
import com.atlassian.jira.testkit.issue.fields.layout.field.Jira62FieldLayoutManagerAdapterImpl;
import com.atlassian.pocketknife.api.version.JiraVersionService;
import com.atlassian.pocketknife.api.version.VersionKit;

public class FieldLayoutManagerAdapterFactoryImpl implements FieldLayoutManagerAdapterFactory
{
    private final JiraVersionService jiraVersionService;
    private final FieldLayoutManager fieldLayoutManager;

    public FieldLayoutManagerAdapterFactoryImpl(JiraVersionService jiraVersionService, FieldLayoutManager fieldLayoutManager)
    {
        this.jiraVersionService = jiraVersionService;
        this.fieldLayoutManager = fieldLayoutManager;
    }

    @Override
    public boolean isAvailable()
    {
        return jiraVersionService.version().isGreaterThanOrEqualTo(VersionKit.parse("6.2"));
    }

    @Override
    public FieldLayoutManagerAdapter create()
    {
        if (isAvailable())
        {
            return new Jira62FieldLayoutManagerAdapterImpl(fieldLayoutManager);
        }
        throw new UnsupportedOperationException("Field Layout Manager not available until JIRA 6.2");
    }
}
