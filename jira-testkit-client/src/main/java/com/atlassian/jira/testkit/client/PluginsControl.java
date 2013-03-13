/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
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

	public void setPluginLicense(String pluginKey, String license) throws JSONException {
		pluginKey += "-key";
		final String pluginDescStr = createResourceForPath("plugins").path(pluginKey).get(String.class);
		final JSONObject pluginDesc = new JSONObject(pluginDescStr);
		final JSONObject licenseDetails = new JSONObject();
		licenseDetails.put("rawLicense", license);
		pluginDesc.put("licenseDetails", licenseDetails);

		createResourceForPath("plugins").
				path(pluginKey).
				accept("application/vnd.atl.plugins.plugin+json").
				type("application/vnd.atl.plugins.plugin+json").put(pluginDesc.toString());
	}
}
