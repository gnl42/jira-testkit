package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.bc.license.JiraLicenseUpdaterService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.Consumes;
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
public class LicenseBackdoor
{
	private final JiraLicenseUpdaterService licenseService;
    private final JiraAuthenticationContext jiraAuthenticationContext;

	public LicenseBackdoor(JiraAuthenticationContext jiraAuthenticationContext)
	{
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.licenseService = ComponentAccessor.getComponentOfType(JiraLicenseUpdaterService.class);
	}

	@POST
	@Path("set")
	@AnonymousAllowed
	public Response license(String license)
	{

		try {
			licenseService.setLicense(licenseService.validate(jiraAuthenticationContext.getI18nHelper(), license));
			return Response.ok(true).cacheControl(never()).build();
		} catch (Exception e) {
			return Response.ok(false).cacheControl(never()).build();
		}
	}

}