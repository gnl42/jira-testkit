package com.atlassian.jira.testkit.auditing;

import com.atlassian.jira.exception.PermissionException;

public interface AuditingManagerAdapter
{
    /**
     * @since JIRA 6.2
     */
    void setAuditingEnabled(boolean enable) throws PermissionException;
}
