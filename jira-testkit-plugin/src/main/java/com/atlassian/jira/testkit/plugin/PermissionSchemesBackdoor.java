/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.permission.PermissionSchemeEntry;
import com.atlassian.jira.permission.PermissionSchemeManager;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.scheme.SchemeEntity;
import com.atlassian.jira.security.plugin.ProjectPermissionKey;
import com.atlassian.jira.testkit.plugin.util.CacheControl;
import com.atlassian.jira.user.UserKeyService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
 * This class should only be called by the com.atlassian.jira.testkit.client.PermissionSchemesControl.
 *
 * @since v5.0
 */
@Path ("permissionSchemes")
@Produces ({ MediaType.APPLICATION_JSON })
public class PermissionSchemesBackdoor
{
    Logger log = LoggerFactory.getLogger(PermissionSchemesBackdoor.class);
    private PermissionSchemeManager schemeManager;
    private UserKeyService userKeyService;

    public PermissionSchemesBackdoor(PermissionSchemeManager schemeManager, UserKeyService userKeyService)
    {
        this.schemeManager = schemeManager;
        this.userKeyService = userKeyService;
    }

    @GET
    @AnonymousAllowed
    @Path("copyDefault")
    public Response copyDefault(@QueryParam ("schemeName") String newSchemeName)
    {
        Scheme defaultScheme = schemeManager.getDefaultSchemeObject();
        Scheme copyScheme = schemeManager.copyScheme(defaultScheme);

        // TODO - create new scheme blah blah immutable blah
        copyScheme.setName(newSchemeName);
        schemeManager.updateScheme(copyScheme);

        return Response.ok(copyScheme.getId()).build();
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

    @GET
    @AnonymousAllowed
    @Path("legacy/entity/add")
    @Deprecated
    public Response addSchemeEntity(@QueryParam ("schemeId") long schemeId,
            @QueryParam ("permission") long permission,
            @QueryParam ("type") String type,
            @QueryParam ("parameter") String parameter)
    {
        try
        {
            GenericValue scheme = schemeManager.getScheme(schemeId);
            List<GenericValue> entities = schemeManager.getEntities(scheme, permission, type, parameter);
            if (!entities.isEmpty())
            {
                throw new IllegalStateException("PermissionScheme entity to be added already exists");
            }

            SchemeEntity entity = new SchemeEntity(type, parameter, permission);
            schemeManager.createSchemeEntity(scheme, entity);
        }
        catch (GenericEntityException e)
        {
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("entity/add")
    public Response addSchemeEntity(@QueryParam ("schemeId") long schemeId,
            @QueryParam ("permission") String permissionKey,
            @QueryParam ("type") String type,
            @QueryParam ("parameter") String parameter)
    {
        parameter = validateParameter(type, parameter);

        ProjectPermissionKey permission = new ProjectPermissionKey(permissionKey);

        Collection<PermissionSchemeEntry> matchingEntries = getPermissionSchemeEntries(schemeId, permission, type, parameter);

        if (matchingEntries.isEmpty())
        {
            try
            {
                GenericValue scheme = schemeManager.getScheme(schemeId);
                SchemeEntity entity = new SchemeEntity(type, parameter, permissionKey);
                schemeManager.createSchemeEntity(scheme, entity);
            }
            catch (GenericEntityException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            log.info("Attempted to add an entity which already exists; ignoring");
        }

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("legacy/entity/remove")
    @Deprecated
    public Response removeEntity(@QueryParam ("schemeId") long schemeId,
                                @QueryParam ("permission") long permission,
                                @QueryParam ("type") String type,
                                @QueryParam ("parameter") String parameter)
    {
        try
        {
            GenericValue scheme = schemeManager.getScheme(schemeId);
            List<GenericValue> entities = schemeManager.getEntities(scheme, permission, type, parameter);
            if (entities.isEmpty())
            {
                throw new IllegalStateException("PermissionScheme entity to be removed does not exist");
            }

            for (GenericValue entity : entities)
            {
                Long id = entity.getLong("id");
                schemeManager.deleteEntity(id);
            }
        }
        catch (GenericEntityException e)
        {
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

    @GET
    @AnonymousAllowed
    @Path("entity/remove")
    public Response removeEntity(@QueryParam ("schemeId") long schemeId,
            @QueryParam ("permission") String permissionKey,
            @QueryParam ("type") String type,
            @QueryParam ("parameter") String parameter)
    {
        parameter = validateParameter(type, parameter);

        ProjectPermissionKey permission = new ProjectPermissionKey(permissionKey);
        Collection<PermissionSchemeEntry> matchingEntries = getPermissionSchemeEntries(schemeId, permission, type, parameter);

        if (matchingEntries.isEmpty())
        {
            log.info("Attempted to remove an entity which does not exist; ignoring");
        }
        else
        {

            matchingEntries.stream().forEach(permissionSchemeEntry ->
            {
                try
                {
                    schemeManager.deleteEntity(permissionSchemeEntry.getId());
                }
                catch (GenericEntityException e)
                {
                    throw new RuntimeException(e);
                }
            });
        }

        return Response.ok(null).build();
    }

    /**
     * Removes all matching entities for the given permission and type, and adds the entity with the given parameter.
     *
     * @param schemeId the scheme ID
     * @param permission the permission
     * @param type the type
     * @param parameter the parameter
     * @return an empty 200 response
     */
    @GET
    @AnonymousAllowed
    @Path("legacy/entity/replace")
    @Deprecated
    public Response replaceEntities(@QueryParam ("schemeId") long schemeId,
            @QueryParam ("permission") long permission,
            @QueryParam ("type") String type,
            @QueryParam ("parameter") String parameter)
    {
        try
        {
            GenericValue scheme = schemeManager.getScheme(schemeId);
            List<GenericValue> entities = schemeManager.getEntities(scheme, permission);

            for (GenericValue entity : entities)
            {
                Long id = entity.getLong("id");
                schemeManager.deleteEntity(id);
            }

            SchemeEntity entity = new SchemeEntity(type, parameter, permission);
            schemeManager.createSchemeEntity(scheme, entity);
        }
        catch (GenericEntityException e)
        {
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

    /**
     * Removes all matching entities for the given permission and type, and adds the entity with the given parameter.
     *
     * @param schemeId the scheme ID
     * @param permissionKey the permission key
     * @param type the type
     * @param parameter the parameter
     * @return an empty 200 response
     */
    @GET
    @AnonymousAllowed
    @Path("entity/replace")
    public Response replaceEntities(@QueryParam ("schemeId") long schemeId,
            @QueryParam ("permission") String permissionKey,
            @QueryParam ("type") String type,
            @QueryParam ("parameter") String parameter)
    {
        parameter = validateParameter(type, parameter);

        ProjectPermissionKey permission = new ProjectPermissionKey(permissionKey);
        Collection<PermissionSchemeEntry> matchingEntries = getPermissionSchemeEntries(schemeId, permission);


        matchingEntries.stream().forEach(permissionSchemeEntry ->
        {
            try
            {
                schemeManager.deleteEntity(permissionSchemeEntry.getId());

            }
            catch (GenericEntityException e)
            {
                throw new RuntimeException(e);
            }
        });


        try
        {
            GenericValue scheme = schemeManager.getScheme(schemeId);
            SchemeEntity entity = new SchemeEntity(type, parameter, permissionKey);
            schemeManager.createSchemeEntity(scheme, entity);
        }
        catch (GenericEntityException e)
        {
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

    /**
     * There are a few changes that must be made to the parameter depending on the type of permission scheme entity
     * that we want to modify. This collects them to a single function.
     */
    private @Nullable String validateParameter(@Nonnull final String type, @Nullable final String parameter)
    {
        String newParameter = convertUserKey(type, parameter);

        newParameter = fixAnyoneGroupParameter(type, newParameter);

        return newParameter;
    }


    /**
     * The web interface when adding a user permission type uses this getKeyForUsername, so the backdoor should
     * do the same.
     */
    private @Nonnull String convertUserKey(@Nonnull final String type, @Nullable final String parameter)
    {
        if ("user".equals(type))
        {
            return userKeyService.getKeyForUsername(parameter);
        }
        return parameter;
    }


    /**
     * The empty string is often used on the client side but it is stored internally as null.
     * Therefore, make sure future uses of this backdoor do not fail because they don't realise this.
     */
    private @Nullable String fixAnyoneGroupParameter(@Nonnull final String type, @Nullable final String parameter)
    {
        if ("group".equals(type) && "".equals(parameter))
        {
            return null;
        }
        return parameter;
    }


    private Collection<PermissionSchemeEntry> getPermissionSchemeEntries(@Nonnull long schemeId,
                                                                         @Nonnull ProjectPermissionKey permission)
    {
        return schemeManager.getPermissionSchemeEntries(schemeId, permission);
    }


    private Collection<PermissionSchemeEntry> getPermissionSchemeEntries(@Nonnull long schemeId,
                                                                         @Nonnull ProjectPermissionKey permission,
                                                                         @Nonnull String type,
                                                                         @Nullable String parameter)
    {

        return schemeManager.getPermissionSchemeEntries(schemeId, permission, type).stream()
                .filter(entry -> {
                    if (parameter == null) {
                        return (entry.getParameter() == null);
                    } else {
                        return (entry.getParameter() != null && entry.getParameter().equals(parameter));
                    }
                })
                .collect(Collectors.toList());
    }
}
