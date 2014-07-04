package com.atlassian.jira.testkit.client;

/**
 * TODO: Document this class / interface here
 *
 * @since v6.1
 */
public class AuditingControl extends BackdoorControl<AuditingControl>
{

    public static final String AUDITING_PATH = "auditing";

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
        get(createResource().path(AUDITING_PATH).path("enable"));
    }

    public void disable() {
        get(createResource().path(AUDITING_PATH).path("disable"));
    }

    public void clearAllRecords() {
        get(createResource().path(AUDITING_PATH).path("clearAll"));
    }

    public void moveAllRecordsBackInTime(long secondsIntoPast)
    {
        get(createResource().path(AUDITING_PATH).path("moveAllRecordsBackInTime")
                .queryParam("secondsIntoPast", Long.toString(secondsIntoPast)));
    }
}
