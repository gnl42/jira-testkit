package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.compatibility.bridge.application.ApplicationRoleManagerBridge;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.license.JiraLicenseManager;
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
 * Resource for setting up and restoring data in func tests.
 *
 * @since 4.4
 */
@Path ("license")
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class LicenseBackdoor {
    private final JiraLicenseManager licenseManager;
    private final ApplicationRoleManagerBridge applicationRoleManagerBridge;

    public LicenseBackdoor(ApplicationRoleManagerBridge applicationRoleManagerBridge) {
        this.licenseManager = ComponentAccessor.getComponentOfType(JiraLicenseManager.class);
        this.applicationRoleManagerBridge = applicationRoleManagerBridge;
    }

    @POST
    @Path("set")
    @AnonymousAllowed
    public Response license(String license) {

        try {
            licenseManager.setLicense(license);
            return Response.ok(true).cacheControl(never()).build();
        } catch (Exception e) {
            return Response.ok(false).cacheControl(never()).build();
        }
    }

    @GET
    @Path("rolesEnabled")
    @AnonymousAllowed
    public Response rolesEnabled()
    {
        final boolean isEnabled = applicationRoleManagerBridge.isBridgeActive() && applicationRoleManagerBridge.rolesEnabled();
        return Response.ok(Boolean.toString(isEnabled)).build();
    }
}