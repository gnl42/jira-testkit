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
        return get(createSubtaskResource(), Boolean.class);
    }

    public boolean enable()
    {
        return post(createSubtaskResource(), true, Boolean.class);
    }

    public boolean disable()
    {
        return post(createSubtaskResource(), false, Boolean.class);
    }

    private WebResource createSubtaskResource()
    {
        return createResource().path("websudo");
    }
}
