package com.atlassian.jira.testkit.client;

/**
 * Controls the backdoor for Auditing features. Will only work correctly against JIRA 6.2 and higher.
 */
public class AuditingControl extends BackdoorControl<AuditingControl>
{
    /**
     * Creates a new BackdoorControl.
     *
     * @param environmentData a JIRAEnvironmentData
     */
    public AuditingControl(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * Only works for JIRA 6.2 and higher.
     */
    public void enable() {
        get(createResource().path("auditing").path("enable"));
    }

    /**
     * Only works for JIRA 6.2 and higher.
     */
    public void disable() {
        get(createResource().path("auditing").path("disable"));
    }

    /**
     * Only works for JIRA 6.2 and higher.
     */
    public void clearAllRecords() {
        get(createResource().path("auditing").path("clearAll"));
    }
}
