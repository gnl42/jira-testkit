package com.atlassian.jira.testkit.client;

/**
 * TODO: Document this class / interface here
 *
 * @since v6.1
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

    public void enable() {
        get(createResource().path("auditing").path("enable"));
    }

    public void disable() {
        get(createResource().path("auditing").path("disable"));
    }

    public void clearAllRecords() {
        get(createResource().path("auditing").path("clearAll"));
    }
}
