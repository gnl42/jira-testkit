package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.compatibility.bridge.application.ApplicationRoleManagerBridge;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * A backdoor for checking if roles is enabled.
 */

@Path("rolesEnabled")
public class RolesEnabledBackdoor
{
    private final ApplicationRoleManagerBridge applicationRoleManagerBridge;

    public RolesEnabledBackdoor(ApplicationRoleManagerBridge applicationRoleManagerBridge)
    {
        this.applicationRoleManagerBridge = applicationRoleManagerBridge;
    }

    @GET
    @AnonymousAllowed
    public Response rolesEnabled()
    {
        final boolean isEnabled = applicationRoleManagerBridge.isBridgeActive() && applicationRoleManagerBridge.rolesEnabled();
        return Response.ok(Boolean.toString(isEnabled)).build();
    }
}
