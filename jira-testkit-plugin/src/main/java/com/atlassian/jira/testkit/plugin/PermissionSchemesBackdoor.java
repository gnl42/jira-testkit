/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.permission.JiraPermissionHolderType;
import com.atlassian.jira.permission.PermissionSchemeEntry;
import com.atlassian.jira.permission.PermissionSchemeManager;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.scheme.SchemeEntity;
import com.atlassian.jira.security.plugin.ProjectPermissionKey;
import com.atlassian.jira.testkit.plugin.util.CacheControl;
import com.atlassian.jira.user.UserKeyService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final Logger log = LoggerFactory.getLogger(PermissionSchemesBackdoor.class);
    private final PermissionSchemeManager schemeManager;
    private final UserKeyService userKeyService;

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
        parameter = transformParameter(type, parameter);

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
                log.error("Error adding new entry for permission scheme {0}", schemeId , e);
                return Response.serverError().entity(e.getMessage()).cacheControl(CacheControl.never()).build();
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
        parameter = transformParameter(type, parameter);

        ProjectPermissionKey permission = new ProjectPermissionKey(permissionKey);
        Collection<PermissionSchemeEntry> matchingEntries = getPermissionSchemeEntries(schemeId, permission, type, parameter);

        if (!matchingEntries.isEmpty())
        {
            for (PermissionSchemeEntry entry: matchingEntries)
            {
                try
                {
                    schemeManager.deleteEntity(entry.getId());
                }
                catch (GenericEntityException e)
                {
                    log.error("Error deleting existing entry for permission scheme {0}", schemeId , e);
                    return Response.serverError().entity(e.getMessage()).cacheControl(CacheControl.never()).build();
                }
            }
        }
        else
        {
            log.info("Attempted to remove an entity which does not exist; ignoring");
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
        parameter = transformParameter(type, parameter);

        ProjectPermissionKey permission = new ProjectPermissionKey(permissionKey);
        Collection<PermissionSchemeEntry> matchingEntries = getPermissionSchemeEntries(schemeId, permission);


        for (PermissionSchemeEntry entry : matchingEntries)
        {
            try
            {
                schemeManager.deleteEntity(entry.getId());
            }
            catch (GenericEntityException e)
            {
                log.error("Error deleting existing entry for permission scheme {0}", schemeId , e);
                return Response.serverError().entity(e.getMessage()).cacheControl(CacheControl.never()).build();
            }
        }


        try
        {
            GenericValue scheme = schemeManager.getScheme(schemeId);
            SchemeEntity entity = new SchemeEntity(type, parameter, permissionKey);
            schemeManager.createSchemeEntity(scheme, entity);
        }
        catch (GenericEntityException e)
        {
            log.error("Error adding new entry for permission scheme {0}", schemeId , e);
            return Response.serverError().entity(e.getMessage()).cacheControl(CacheControl.never()).build();
        }

        return Response.ok(null).build();
    }

    /**
     * Gets all the current assigned permission holders for a given permission. If no permission is given all system
     * permissions are returned. If no type is given, all types are placed in the response for the permission.
     * This returns a JSON of Permission Key -> Permission Type -> Parameters. For example:
     * {
     *     EDIT_ISSUES: {
     *         USER: ["bob", "jane"],
     *         GROUP: ["jira-developers, "jira-admins"]
     *     }
     *     BROWSE_USERS: {
     *         GROUP: ["jira-admins"]
     *     }
     * }
     *
     * @param schemeId the scheme ID
     * @param permissionKey the permission to get permissions for, null if you want all permissions
     * @param type the type of permissionHolder, e.g. "user", null if you want all types
     */
    @GET
    @AnonymousAllowed
    @Path("entity/assigned")
    public Response getCurrentlyAssigned(@Nonnull @QueryParam ("schemeId") long schemeId,
                                         @Nullable @QueryParam ("permission") String permissionKey,
                                         @Nullable @QueryParam ("type") String type,
                                         @Nullable @QueryParam ("parameter") String parameter)
    {
        final String modifiedParameter = transformParameter(type, parameter);
        List<ProjectPermissionKey> permissionsToFind = Lists.newArrayList();

        if (permissionKey == null)
        {
            permissionsToFind = ALL_PERMISSIONS;
        }
        else
        {
            permissionsToFind.add(new ProjectPermissionKey(permissionKey));
        }

        Map<String, Map<String, List<String>>> results = new HashMap<>();

        permissionsToFind.stream().forEach(permission -> {
            Collection<PermissionSchemeEntry> matchingEntries;
            if (type == null)
            {
                matchingEntries = getPermissionSchemeEntries(schemeId, permission);
            }
            else if (modifiedParameter == null)
            {
                matchingEntries = getPermissionSchemeEntries(schemeId, permission, type);
            }
            else
            {
                matchingEntries = getPermissionSchemeEntries(schemeId, permission, type, modifiedParameter);
            }

            Map<String, List<String>> typesToParameters = Maps.newHashMap();

            matchingEntries.stream().forEach(permissionSchemeEntry -> {
                String entryType = permissionSchemeEntry.getType();
                if (!typesToParameters.containsKey(entryType)) {
                    typesToParameters.put(entryType, Lists.newArrayList());
                }

                typesToParameters.get(entryType).add(permissionSchemeEntry.getParameter());
            });
            if (typesToParameters.size() > 0) {
                results.put(permission.permissionKey(), typesToParameters);
            }
        });

        return Response.ok(results).build();
    }


    /**
     * There are a few changes that must be made to the parameter depending on the type of permission scheme entity
     * that we want to modify. This collects them to a single function.
     */
    private @Nullable String transformParameter(@Nonnull final String type, @Nullable final String parameter)
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
        if (JiraPermissionHolderType.USER.getKey().equals(type))
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
        if (JiraPermissionHolderType.GROUP.getKey().equals(type) && "".equals(parameter))
        {
            return null;
        }
        return parameter;
    }


    private Collection<PermissionSchemeEntry> getPermissionSchemeEntries(long schemeId,
                                                                         @Nonnull ProjectPermissionKey permission)
    {
        return schemeManager.getPermissionSchemeEntries(schemeId, permission);
    }

    private Collection<PermissionSchemeEntry> getPermissionSchemeEntries(long schemeId,
                                                                         @Nonnull ProjectPermissionKey permission,
                                                                         String type)
    {
        return schemeManager.getPermissionSchemeEntries(schemeId, permission, type);
    }


    private Collection<PermissionSchemeEntry> getPermissionSchemeEntries(long schemeId,
                                                                         @Nonnull ProjectPermissionKey permission,
                                                                         @Nonnull String type,
                                                                         @Nullable String parameter)
    {
        return getPermissionSchemeEntries(schemeId, permission, type).stream()
                .filter(entry -> {
                    if (parameter == null) {
                        return (entry.getParameter() == null);
                    } else {
                        return (entry.getParameter() != null && entry.getParameter().equals(parameter));
                    }
                })
                .collect(Collectors.toList());
    }

    public static final List<ProjectPermissionKey> ALL_PERMISSIONS = Lists.newArrayList(
            ProjectPermissions.ADMINISTER_PROJECTS,
            ProjectPermissions.BROWSE_PROJECTS,
            ProjectPermissions.VIEW_DEV_TOOLS,
            ProjectPermissions.VIEW_READONLY_WORKFLOW,
            ProjectPermissions.CREATE_ISSUES,
            ProjectPermissions.EDIT_ISSUES,
            ProjectPermissions.TRANSITION_ISSUES,
            ProjectPermissions.SCHEDULE_ISSUES,
            ProjectPermissions.MOVE_ISSUES,
            ProjectPermissions.ASSIGN_ISSUES,
            ProjectPermissions.ASSIGNABLE_USER,
            ProjectPermissions.RESOLVE_ISSUES,
            ProjectPermissions.CLOSE_ISSUES,
            ProjectPermissions.MODIFY_REPORTER,
            ProjectPermissions.DELETE_ISSUES,
            ProjectPermissions.LINK_ISSUES,
            ProjectPermissions.SET_ISSUE_SECURITY,
            ProjectPermissions.VIEW_VOTERS_AND_WATCHERS,
            ProjectPermissions.MANAGE_WATCHERS,
            ProjectPermissions.ADD_COMMENTS,
            ProjectPermissions.EDIT_ALL_COMMENTS,
            ProjectPermissions.EDIT_OWN_COMMENTS,
            ProjectPermissions.DELETE_ALL_COMMENTS,
            ProjectPermissions.DELETE_OWN_COMMENTS,
            ProjectPermissions.CREATE_ATTACHMENTS,
            ProjectPermissions.DELETE_ALL_ATTACHMENTS,
            ProjectPermissions.DELETE_OWN_ATTACHMENTS,
            ProjectPermissions.WORK_ON_ISSUES,
            ProjectPermissions.EDIT_OWN_WORKLOGS,
            ProjectPermissions.EDIT_ALL_WORKLOGS,
            ProjectPermissions.DELETE_OWN_WORKLOGS,
            ProjectPermissions.DELETE_ALL_WORKLOGS
    );
}
