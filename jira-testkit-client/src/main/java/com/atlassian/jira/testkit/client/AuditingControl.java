package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.beans.AuditEntryBean;

import javax.ws.rs.core.MediaType;

/**
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

    public void clearAllRecords() {
        get(createResource().path(AUDITING_PATH).path("clearAll"));
    }

    public void moveAllRecordsBackInTime(long secondsIntoPast)
    {
        get(createResource().path(AUDITING_PATH).path("moveAllRecordsBackInTime")
                .queryParam("secondsIntoPast", Long.toString(secondsIntoPast)));
    }

    /**
     * Adds an audit entry to the JIRA audit logs.
     *
     * @param entry the entry to add
     * @since 7.0.0
     */
    public void addEntry(AuditEntryBean entry)
    {
        createResource().path(AUDITING_PATH).path("addEntry")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE).post(entry);
    }
}
