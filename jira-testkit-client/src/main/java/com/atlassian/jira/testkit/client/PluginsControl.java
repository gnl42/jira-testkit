package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;

/**
 * 
 * See {@link com.atlassian.jira.testkit.plugin.PluginBackdoor} in jira-testkit-plugin for backend.
 * 
 *  @since 5.0
 */

public class PluginsControl extends BackdoorControl<PluginsControl>
{
    public PluginsControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void disablePlugin(final String pluginKey)
    {
        WebResource resource = createResource().path("plugins/disable")
                .queryParam("key", pluginKey);
        get(resource);
    }

    public void enablePlugin(final String pluginKey)
    {
        WebResource resource = createResource().path("plugins/enable")
                .queryParam("key", pluginKey);
        get(resource);
    }

    public void disablePluginModule(final String completeKey)
    {
        WebResource resource = createResource().path("plugins/disableModule")
                .queryParam("key", completeKey);
        get(resource);
    }

    public void enablePluginModule(final String completeKey)
    {
        WebResource resource = createResource().path("plugins/enableModule")
                .queryParam("key", completeKey);
        get(resource);
    }
}
