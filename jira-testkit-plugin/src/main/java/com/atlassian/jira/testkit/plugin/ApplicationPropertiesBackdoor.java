/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Use this backdoor to manipulate ApplicationProperties as part of setup for tests.
 *
 * This class should only be called by the <code>com.atlassian.jira.testkit.client.ApplicationPropertiesControl</code>.
 *
 * @since v5.0
 */
@Produces ({MediaType.APPLICATION_JSON})
@Consumes ({MediaType.APPLICATION_JSON})
@Path ("applicationProperties")
public class ApplicationPropertiesBackdoor
{
    private final ApplicationProperties applicationProperties;

    public ApplicationPropertiesBackdoor(ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
    }

    @GET
    @AnonymousAllowed
    @Path("option/set")
    public Response setOption(@QueryParam ("key") String key, @QueryParam ("value") boolean value)
    {
        applicationProperties.setOption(key, value);
        return Response.ok(null).build();
    }

    @POST
    @AnonymousAllowed
    @Path("text/set")
    public Response setText(KeyValueHolder holder)
    {
        applicationProperties.setText(holder.key, holder.value);
        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("option/get")
    public Response getOption(@QueryParam ("key") String key)
    {
        return Response.ok(applicationProperties.getOption(key)).build();
    }

    @GET
    @AnonymousAllowed
    @Path("string/get")
    public Response getString(@QueryParam ("key") String key)
    {
        return Response.ok(applicationProperties.getString(key)).build();
    }

    @POST
    @AnonymousAllowed
    @Path("string/set")
    public Response setString(KeyValueHolder holder)
    {
        applicationProperties.setString(holder.key, holder.value);
        return Response.ok(null).build();
    }

    private static class KeyValueHolder
    {
        public String key;
        public String value;
    }
}
