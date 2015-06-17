package com.atlassian.jira.testkit.client;

/**
 * Use this class from func/selenium/page-object tests that need to check if roles enabled.
 *
 * @since v5.0
 */
public class RolesEnabledControl extends BackdoorControl<RolesEnabledControl>
{
    public RolesEnabledControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public boolean isRolesEnabled()
    {
        return Boolean.parseBoolean(get(createResource().path("rolesEnabled")));
    }
}
