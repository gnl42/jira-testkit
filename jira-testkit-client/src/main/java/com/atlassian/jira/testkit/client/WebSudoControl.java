package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;

/**
 * Simple control for Web Sudo.
 *
 * See {@link com.atlassian.jira.testkit.plugin.WebSudoBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class WebSudoControl extends BackdoorControl<WebSudoControl>
{
    public WebSudoControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public boolean isEnabled()
    {
        return createSubtaskResource().get(Boolean.class);
    }

    public boolean enable()
    {
        return createSubtaskResource().post(Boolean.class, true);
    }

    public boolean disable()
    {
        return createSubtaskResource().post(Boolean.class, false);
    }

    private WebResource createSubtaskResource()
    {
        return createResource().path("websudo");
    }
}
