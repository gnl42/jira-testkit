/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.core.AtlassianCoreException;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.preferences.ExtendedPreferences;
import com.atlassian.jira.user.preferences.PreferenceKeys;
import com.atlassian.jira.user.preferences.UserPreferencesManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

/**
 * Use this backdoor to manipulate User Profiles as part of setup for tests.
 *
 * This class should only be called by the com.atlassian.jira.testkit.client.UserProfileControl.
 *
 * @since v5.0
 */
@Path ("userProfile")
public class UserProfileBackdoor
{
    private final UserPreferencesManager userPreferencesManager;
    private final UserUtil userUtil;

    public UserProfileBackdoor(UserPreferencesManager userPreferencesManager, UserUtil userUtil)
    {
        this.userPreferencesManager = userPreferencesManager;
        this.userUtil = userUtil;
    }

    @GET
    @AnonymousAllowed
    @Path("notificationType/set")
    public Response addGlobalPermission(@QueryParam ("username") String username,
            @QueryParam ("format") String format)
    {
        final ApplicationUser user = getUserByName(username);
        try
        {
            preferencesOf(user).setString(PreferenceKeys.USER_NOTIFICATIONS_MIMETYPE, format);
        }
        catch (AtlassianCoreException e)
        {
            throw new RuntimeException(e);
        }
        return Response.ok(null).build();
    }

    @PUT
    @AnonymousAllowed
    @Path("timeZone")
    public Response setTimeZone(@QueryParam ("username") String username, @QueryParam ("timeZone") String timeZone)
    {
        final ApplicationUser user = getUserByName(username);
        try
        {
            preferencesOf(user).setString(PreferenceKeys.USER_TIMEZONE, timeZone);
        }
        catch (AtlassianCoreException e)
        {
            throw new RuntimeException(e);
        }

        return Response.ok(null).build();
    }

    /**
     * Sets a user preference.
     *
     * @param username the username
     * @param name the name of the preference
     * @param type the data type of the preference, e.g. "boolean" or "long"
     * @param value the preference value
     * @throws AtlassianCoreException if there was a problem setting the preference
     * @since 6.0.19
     */
    @PUT
    @Path ("preference/{name}")
    @Consumes (MediaType.TEXT_PLAIN)
    public void setUserPreference(@QueryParam ("username") String username, @PathParam ("name") String name, @QueryParam ("type") String type, String value)
            throws AtlassianCoreException
    {
        final ApplicationUser user = getUserByName(username);
        if ("boolean".equalsIgnoreCase(type))
        {
            preferencesOf(user).setBoolean(name, Boolean.valueOf(value));
            return;
        }

        if ("long".equalsIgnoreCase(type))
        {
            preferencesOf(user).setLong(name, Long.parseLong(value));
            return;
        }

        preferencesOf(user).setString(name, value != null ? value : "");
    }

    /**
     * Gets a user preference.
     *
     * @param username the username
     * @param name the preference name
     * @param type the preference data type
     * @return the preference value
     * @since 6.0.28
     */
    @GET
    @Path ("preference/{name}")
    @Produces ({MediaType.APPLICATION_JSON})
    public Response getUserPreference(@QueryParam("username") String username, @PathParam("name") String name, @QueryParam("type") String type)
    {
        final ApplicationUser user = getUserByName(username);
        final Object value;
        if ("boolean".equalsIgnoreCase(type))
        {
            value = preferencesOf(user).getBoolean(name);
        }
        else if ("long".equalsIgnoreCase(type))
        {
            value = preferencesOf(user).getLong(name);
        }
        else
        {
            value = preferencesOf(user).getString(name);
        }

        return Response.ok(value).build();
    }

    /**
     * Removes a user preference.
     *
     * @param username the username
     * @param name the preference name
     * @throws AtlassianCoreException if there was a problem removing the preference
     * @since 6.0.19
     */
    @DELETE
    @Path ("preference/{name}")
    @Consumes (MediaType.TEXT_PLAIN)
    public void removeUserPreference(@QueryParam ("username") String username, @PathParam ("name") String name) throws AtlassianCoreException
    {
        final ApplicationUser user = getUserByName(username);
        preferencesOf(user).remove(name);
    }

    private ApplicationUser getUserByName(String username)
    {
        final ApplicationUser user = userUtil.getUserByName(username);
        if (user == null)
        {
            throw new WebApplicationException(404);
        }
        return user;
    }

    private ExtendedPreferences preferencesOf(ApplicationUser user)
    {
        return userPreferencesManager.getExtendedPreferences(user);
    }
}
