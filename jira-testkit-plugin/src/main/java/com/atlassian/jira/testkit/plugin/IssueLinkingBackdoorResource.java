/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeDestroyer;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Iterables;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;

/**
 * Are you allowed to enable or disable issue links.
 *
 * @since v5.0.4
 */
@Path ("issueLinking")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class IssueLinkingBackdoorResource
{
    private final IssueLinkTypeManager issueLinkTypeManager;
	private final IssueLinkTypeDestroyer issueLinkTypeDestroyer;
	private final UserUtil userUtil;
	private final ApplicationProperties applicationProperties;

    public IssueLinkingBackdoorResource(IssueLinkTypeManager issueLinkTypeManager, IssueLinkTypeDestroyer issueLinkTypeDestroyer,
			UserUtil userUtil, ApplicationProperties applicationProperties)
    {
        this.issueLinkTypeManager = issueLinkTypeManager;
		this.issueLinkTypeDestroyer = issueLinkTypeDestroyer;
		this.userUtil = userUtil;
		this.applicationProperties = applicationProperties;
    }
    
    @GET
    public Response get()
    {
        return Response.ok(applicationProperties.getOption(APKeys.JIRA_OPTION_ISSUELINKING)).cacheControl(never()).build();
    }

    @POST
    public Response set(Boolean enabled)
    {
        if (enabled != applicationProperties.getOption(APKeys.JIRA_OPTION_ISSUELINKING))
        {
            applicationProperties.setOption(APKeys.JIRA_OPTION_ISSUELINKING, enabled);
        }
        return Response.ok(applicationProperties.getOption(APKeys.JIRA_OPTION_ISSUELINKING)).cacheControl(never()).build();
    }

    @GET
    @AnonymousAllowed
    @Path("create")
    public Response addLink(
            @QueryParam ("name") String name,
            @QueryParam ("outward") String outward,
            @QueryParam ("inward") String inward,
            @QueryParam ("style") String style)
    {
        issueLinkTypeManager.createIssueLinkType(name, outward, inward, style);
        return Response.ok(null).build();
    }

	@GET
	@AnonymousAllowed
	@Path("delete")
	public Response deleteLink(@QueryParam ("name") String name) {
		final User sysadmin = Iterables.get(userUtil.getJiraSystemAdministrators(), 0);
        for (IssueLinkType issueLink : issueLinkTypeManager.getIssueLinkTypesByName(name)) {
            issueLinkTypeDestroyer.removeIssueLinkType(issueLink.getId(), null, sysadmin);
        }
		return Response.ok(null).build();
	}
}
