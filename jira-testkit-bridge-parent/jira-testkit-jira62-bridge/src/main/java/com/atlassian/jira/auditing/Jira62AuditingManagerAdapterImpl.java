package com.atlassian.jira.auditing;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.testkit.auditing.AuditingManagerAdapter;

public class Jira62AuditingManagerAdapterImpl implements AuditingManagerAdapter
{
    private final AuditingManager delegate;

    public Jira62AuditingManagerAdapterImpl()
    {
        delegate = ComponentAccessor.getComponent(AuditingManager.class);
    }

    @Override
    public void setAuditingEnabled(boolean enable) throws PermissionException
    {
        delegate.setAuditingEnabled(enable);
    }
}
