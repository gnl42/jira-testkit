package com.atlassian.jira.testkit.client;

/**
 * Use this class from func/selenium/page-object tests that need to log messages on server side.
 * <p/>
 * See {@link com.atlassian.jira.testkit.plugin.LogAccess} in jira-testkit-plugin for backend.
 *
 * @since v6.0
 */
public class LogControl extends BackdoorControl<LogControl>
{
    public LogControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void error(String msg)
    {
        get(createResource().path("log/error").queryParam("msg", msg));
    }

    public void info(String msg)
    {
        get(createResource().path("log/info").queryParam("msg", msg));
    }
}
