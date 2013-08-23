/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * TODO: Document this class / interface here
 *
 * @since v5.0
 */

@Path ("plugins")
public class PluginsBackdoor
{

    private final PluginController pluginController;
    private final PluginAccessor pluginAccessor;

    public PluginsBackdoor(PluginController pluginController, PluginAccessor pluginAccessor)
    {
        this.pluginController = pluginController;
        this.pluginAccessor = pluginAccessor;
    }

    @GET
    @AnonymousAllowed
    @Path ("state")
    public Response getStatePlugin(@QueryParam ("key") String key)
    {
        PluginState state = pluginAccessor.getPlugin(key).getPluginState();
        return Response.ok(state.name()).build();
    }

    @GET
    @AnonymousAllowed
    @Path ("disable")
    public Response disablePlugin(@QueryParam ("key") String key)
    {
        pluginController.disablePlugin(key);
        return Response.ok().build();
    }

    @GET
    @AnonymousAllowed
    @Path ("enable")
    public Response enablePlugin(@QueryParam ("key") String key)
    {
        pluginController.enablePlugins(key);
        return Response.ok().build();
    }

    @GET
    @AnonymousAllowed
    @Path ("disableModule")
    public Response disablePluginModule(@QueryParam("key") String key) {
        pluginController.disablePluginModule(key);
        return Response.ok().build();
    }

    @GET
    @AnonymousAllowed
    @Path ("enableModule")
    public Response enablePluginModule(@QueryParam("key") String key) {
        pluginController.enablePluginModule(key);
        return Response.ok().build();
    }
}
