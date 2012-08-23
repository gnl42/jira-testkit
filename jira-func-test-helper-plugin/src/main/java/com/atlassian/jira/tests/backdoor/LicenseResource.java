package com.atlassian.jira.tests.backdoor;

import com.atlassian.jira.bc.license.JiraLicenseUpdaterService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.ComponentManager.getComponentInstanceOfType;
import static com.atlassian.jira.tests.backdoor.util.CacheControl.never;


/**
 * Resource for setting up and restoring data in func tests.
 *
 * @since 4.4
 */
@Path ("license")
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class LicenseResource
{
    private final JiraLicenseUpdaterService licenseService;

    public LicenseResource()
    {
        this.licenseService = getComponentInstanceOfType(JiraLicenseUpdaterService.class);
    }

    @POST
    @Path("set")
    @AnonymousAllowed
    public Response license(String license)
    {
		try {
			licenseService.setLicense(licenseService.validate(null, license));
			return Response.ok(true).cacheControl(never()).build();
		} catch (Exception e) {
			return Response.ok(false).cacheControl(never()).build();
		}
    }

}
