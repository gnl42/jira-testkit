/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.properties.JiraSystemProperties;
import com.atlassian.jira.config.properties.SystemPropertyKeys;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;


/**
 * @since 4.4
 */
@Path ("websudo")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class WebSudoBackdoor
{
    private final ApplicationProperties applicationProperties;

    public WebSudoBackdoor(ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
    }

    @GET
    public Response status()
    {
        return Response.ok(getCurrentState()).cacheControl(never()).build();
    }

    @POST
    public Response setStatus(boolean state)
    {
        if (getCurrentState() != state)
        {
            setState(state);
        }
        return Response.ok(state).cacheControl(never()).build();
    }

    private void setState(boolean enabled)
    {
        System.setProperty(SystemPropertyKeys.WEBSUDO_IS_DISABLED, Boolean.toString(!enabled));
        JiraSystemProperties.resetReferences();
        applicationProperties.setOption(APKeys.WebSudo.IS_DISABLED, !enabled);
    }

    private boolean getCurrentState()
    {
        return !(Boolean.getBoolean(SystemPropertyKeys.WEBSUDO_IS_DISABLED) ||
                applicationProperties.getOption(APKeys.WebSudo.IS_DISABLED));
    }
}
