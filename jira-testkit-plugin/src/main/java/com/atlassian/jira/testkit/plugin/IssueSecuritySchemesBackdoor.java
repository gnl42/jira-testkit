/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permission and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.issue.security.IssueSecurityLevel;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.issue.security.IssueSecuritySchemeManager;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.scheme.SchemeEntity;
import com.atlassian.jira.testkit.plugin.util.CacheControl;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Use this backdoor to manipulate Issue Security Schemes as part of setup for tests.
 *
 * This class should only be called by the com.atlassian.jira.testkit.client.IssueSecuritySchemesControl.
 *
 * @since v6.2.19
 */
@Path ("issueSecuritySchemes")
@Produces ({ MediaType.APPLICATION_JSON })
public class IssueSecuritySchemesBackdoor
{
    private final IssueSecuritySchemeManager schemeManager;
    private final IssueSecurityLevelManager levelManager;

    public IssueSecuritySchemesBackdoor(IssueSecuritySchemeManager schemeManager, IssueSecurityLevelManager levelManager)
    {
        this.schemeManager = schemeManager;
        this.levelManager = levelManager;
    }

    @GET
    @AnonymousAllowed
    @Path("create")
    public Response create(@QueryParam ("schemeName") String newSchemeName, @QueryParam("schemeDescription") String description)
    {
        Scheme copyScheme = schemeManager.createSchemeObject(newSchemeName, description);

        return Response.ok(copyScheme.getId()).build();
    }

    @DELETE
    @Path("{schemeId}")
    public Response delete(@PathParam ("schemeId") Long schemeId)
    {
        try
        {
            schemeManager.deleteScheme(schemeId);
        }
        catch (GenericEntityException e)
        {
            throw new RuntimeException(e);
        }
        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    @POST
    @Path("{schemeId}")
    public Response addSecurityLevel(@PathParam ("schemeId") int id, @QueryParam("name") String name, @QueryParam("description") String description)
    {
        IssueSecurityLevel issueSecurityLevel = levelManager.createIssueSecurityLevel(id, name, description);

        return Response.ok().entity(issueSecurityLevel.getId()).cacheControl(CacheControl.never()).build();
    }

    @POST
    @Path("{schemeId}/{securityLevelId}")
    public Response addUserToSecurityLevel(@PathParam("schemeId") long schemeId, @PathParam ("securityLevelId") long securityLevelId, @QueryParam("userKey") String userKey) throws GenericEntityException {
        //SingleUser
        String type = "user";
        String parameter = userKey;

        SchemeEntity entity = new SchemeEntity(type, parameter, securityLevelId);
        GenericValue scheme = schemeManager.getScheme(schemeId);
        schemeManager.createSchemeEntity(scheme, entity);

        levelManager.clearUsersLevels();

        return Response.ok().cacheControl(CacheControl.never()).build();
    }
}
