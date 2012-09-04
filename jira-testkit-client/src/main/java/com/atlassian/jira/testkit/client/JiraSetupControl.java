package com.atlassian.jira.testkit.client;

/**
 *
 * @since v5.2
 */
public class JiraSetupControl  extends BackdoorControl<DashboardControl>
{
    public JiraSetupControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void jiraInitialSetup() {
        post(createResource().path("setup/initialSetup"));
    }

    public void resetSetup() {
        get(createResource().path("setup/resetSetup"));
    }

    public void importResult() {
        get(createResource().path("setup/importResult"));
    }
}
