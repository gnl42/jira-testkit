/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.testkit.plugin.util.CacheControl;
import com.atlassian.jira.util.JiraDurationUtils;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @since v5.0.1
 */
@Path ("timeTracking")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class TimeTrackingBackdoor
{
	private final ApplicationProperties applicationProperties;
	private final JiraDurationUtils jiraDurationUtils;
	private final FieldManager fieldManager;
	private final JiraAuthenticationContext authenticationContext;

	public TimeTrackingBackdoor(ApplicationProperties applicationProperties, FieldManager fieldManager, JiraAuthenticationContext authenticationContext) {
		this.applicationProperties = applicationProperties;
		this.jiraDurationUtils = ComponentAccessor.getComponentOfType(JiraDurationUtils.class);
		this.fieldManager = fieldManager;
		this.authenticationContext = authenticationContext;
	}

	@GET
	@Path("enable")
	public Response enable(@QueryParam("hoursPerDay") String hoursPerDay, @QueryParam("daysPerWeek") String daysPerWeek,
			@QueryParam("format") String format, @QueryParam("unit") String unit, @QueryParam("legacy") String legacy)
	{
		applicationProperties.setOption(APKeys.JIRA_OPTION_TIMETRACKING, true);
		applicationProperties.setString(APKeys.JIRA_TIMETRACKING_HOURS_PER_DAY, hoursPerDay);
		applicationProperties.setString(APKeys.JIRA_TIMETRACKING_DAYS_PER_WEEK, daysPerWeek);
		applicationProperties.setString(APKeys.JIRA_TIMETRACKING_FORMAT, format);
		applicationProperties.setString(APKeys.JIRA_TIMETRACKING_DEFAULT_UNIT, StringUtils.upperCase(unit));
		applicationProperties.setOption(APKeys.JIRA_OPTION_TIMETRACKING_ESTIMATES_LEGACY_BEHAVIOUR,
				Boolean.valueOf(legacy));

		jiraDurationUtils.updateFormatters(applicationProperties, authenticationContext);
		fieldManager.refresh();

		return Response.ok().cacheControl(CacheControl.never()).build();
	}
}