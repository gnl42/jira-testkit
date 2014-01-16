/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;
import com.atlassian.jira.testkit.plugin.util.CacheControl;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Use this backdoor to manipulate Permission Schemes as part of setup for tests.
 *
 * This class should only be called by the com.atlassian.jira.testkit.client.FieldConfigurationSchemesControl.
 *
 * @since v6.2.21
 */
@Path ("fieldConfigurationSchemes")
@Produces ({ MediaType.APPLICATION_JSON })
public class FieldConfigurationSchemesBackdoor
{
    private FieldLayoutManager schemeManager;

    public FieldConfigurationSchemesBackdoor(FieldLayoutManager schemeManager)
    {
        this.schemeManager = schemeManager;
    }

    @GET
    @AnonymousAllowed
    @Path("create")
    public Response create(@QueryParam ("schemeName") String newSchemeName, @QueryParam("schemeDescription") String description)
    {
        FieldLayoutScheme copyScheme = schemeManager.createFieldLayoutScheme(newSchemeName, description);

        return Response.ok(copyScheme.getId()).build();
    }

    @DELETE
    @Path("{schemeId}")
    public Response delete(@PathParam ("schemeId") Long schemeId)
    {
        schemeManager.deleteFieldLayoutScheme(schemeManager.getMutableFieldLayoutScheme(schemeId));
        return Response.ok().cacheControl(CacheControl.never()).build();
    }
}
