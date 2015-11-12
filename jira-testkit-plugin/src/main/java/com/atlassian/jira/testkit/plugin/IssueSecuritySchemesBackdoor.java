/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permission and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.jira.issue.security.IssueSecurityLevel;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.issue.security.IssueSecuritySchemeManager;
import com.atlassian.jira.permission.JiraPermissionHolderType;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.scheme.SchemeEntity;
import com.atlassian.jira.testkit.plugin.util.CacheControl;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

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
    private final Logger log = LoggerFactory.getLogger(PermissionSchemesBackdoor.class);

    private final IssueSecuritySchemeManager schemeManager;
    private final IssueSecurityLevelManager levelManager;

    public IssueSecuritySchemesBackdoor(IssueSecuritySchemeManager schemeManager, IssueSecurityLevelManager levelManager)
    {
        this.schemeManager = schemeManager;
        this.levelManager = levelManager;
    }

    /**
     * Filters the security levels to only include the ones with the given security level id and has it's own id
     * @param securityLevels to be filtered
     * @param securityLevelId to be found
     * @return list of filtered levels
     */
    private List<GenericValue> getSecurityLevels(final List<GenericValue> securityLevels, long securityLevelId)
    {
        return securityLevels.stream()
                .filter(genericValue -> {
                    Long id = (Long) genericValue.get("id");
                    Long securityId = (Long) genericValue.get("security");
                    return (id != null && securityId != null && securityId == securityLevelId);
                })
                .collect(Collectors.toList());
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
    @XsrfProtectionExcluded // Only available during testing.
    public Response addSecurityLevel(@PathParam ("schemeId") int id, @QueryParam("name") String name, @QueryParam("description") String description)
    {
        IssueSecurityLevel issueSecurityLevel = levelManager.createIssueSecurityLevel(id, name, description);

        return Response.ok().entity(issueSecurityLevel.getId()).cacheControl(CacheControl.never()).build();
    }

    @POST
    @Path("{schemeId}/{securityLevelId}")
    @XsrfProtectionExcluded // Only available during testing.
    public Response addUserToSecurityLevel(@PathParam("schemeId") long schemeId, @PathParam ("securityLevelId") long securityLevelId, @QueryParam("userKey") String userKey) throws GenericEntityException
    {
        SchemeEntity entity = new SchemeEntity(JiraPermissionHolderType.USER.getKey(), userKey, securityLevelId);
        GenericValue scheme = schemeManager.getScheme(schemeId);
        schemeManager.createSchemeEntity(scheme, entity);

        levelManager.clearUsersLevels();

        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    @DELETE
    @Path("{schemeId}/{securityLevelId}/user/{userKey}")
    @XsrfProtectionExcluded // Only available during testing.
    public Response deleteUserFromSecurityLevel(@PathParam("schemeId") long schemeId,
                                                @PathParam ("securityLevelId") long securityLevelId,
                                                @PathParam("userKey") String userKey) throws GenericEntityException
    {
        final List<GenericValue> allEntities = schemeManager.getEntities(JiraPermissionHolderType.USER.getKey(), userKey);
        final List<GenericValue> filteredEntities = getSecurityLevels(allEntities, securityLevelId);

        if (filteredEntities.size() > 1)
        {
            return Response.serverError().cacheControl(CacheControl.never()).build();
        }
        else if (filteredEntities.size() == 0)
        {
            log.info("Attempted to remove an entity which does not exist; ignoring");
            return Response.ok().build();
        }
        else
        {
            Long entityId = (Long)filteredEntities.get(0).get("id");
            schemeManager.deleteEntity(entityId);
            return Response.ok().cacheControl(CacheControl.never()).build();
        }
    }

    @DELETE
    @Path("{schemeId}/{securityLevelId}/userCF/{userKey}")
    @XsrfProtectionExcluded // Only available during testing.
    public Response deleteUserCustomFieldFromSecurityLevel(@PathParam("schemeId") long schemeId,
                                                @PathParam ("securityLevelId") long securityLevelId,
                                                @PathParam("userKey") String customField) throws GenericEntityException
    {
        final List<GenericValue> allEntities = schemeManager.getEntities(JiraPermissionHolderType.USER_CUSTOM_FIELD.getKey(), customField);
        final List<GenericValue> entities = getSecurityLevels(allEntities, securityLevelId);

        if (entities.size() > 1)
        {
            return Response.serverError().cacheControl(CacheControl.never()).build();

        }
        else if (entities.size() == 0)
        {
            log.info("Attempted to remove an entity which does not exist; ignoring");
            return Response.ok().build();
        }
        else
        {
            Long entityId = (Long)entities.get(0).get("id");
            schemeManager.deleteEntity(entityId);
            return Response.ok().cacheControl(CacheControl.never()).build();
        }
    }

    @DELETE
    @Path("{schemeId}/{securityLevelId}")
    @XsrfProtectionExcluded // Only available during testing.
    public Response removeSecurityLevel(@PathParam("schemeId") int id, @PathParam("securityLevelId") int securityId)
    {

        List<IssueSecurityLevel> filteredLevels = levelManager.getIssueSecurityLevels(id).stream()
                .filter(issueSecurityLevel -> issueSecurityLevel.getId() == securityId).collect(Collectors.toList());

        if (filteredLevels.size() > 1)
        {
            return Response.serverError().cacheControl(CacheControl.never()).build();
        }
        else if (filteredLevels.size() == 0)
        {
            log.info("Attempted to remove an entity which does not exist; ignoring");
            return Response.ok().build();
        }
        else
        {
            levelManager.deleteSecurityLevel(filteredLevels.get(0).getId());
            return Response.ok().build();
        }
    }
}
