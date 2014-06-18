package com.atlassian.jira.testkit.plugin.auditing;

import com.atlassian.jira.testkit.auditing.AuditingManagerAdapter;
import com.atlassian.jira.auditing.Jira62AuditingManagerAdapterImpl;
import com.atlassian.pocketknife.api.version.JiraVersionService;
import com.atlassian.pocketknife.api.version.VersionKit;

public class AuditingManagerAdapterFactoryImpl implements AuditingManagerAdapterFactory
{
    private final JiraVersionService jiraVersionService;

    public AuditingManagerAdapterFactoryImpl(JiraVersionService jiraVersionService)
    {
        this.jiraVersionService = jiraVersionService;
    }

    @Override
    public boolean isAvailable()
    {
        return jiraVersionService.version().isGreaterThanOrEqualTo(VersionKit.parse("6.2"));
    }

    @Override
    public AuditingManagerAdapter create()
    {
        if (isAvailable())
        {
            return new Jira62AuditingManagerAdapterImpl();
        }
        throw new UnsupportedOperationException("Auditing Manager not available until JIRA 6.2");
    }
}
