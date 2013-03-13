/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

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