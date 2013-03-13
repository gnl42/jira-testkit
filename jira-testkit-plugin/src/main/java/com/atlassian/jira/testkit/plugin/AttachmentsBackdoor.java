/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.util.AttachmentPathManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;

@Path("attachments")
@AnonymousAllowed
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AttachmentsBackdoor {

	private final ApplicationProperties applicationProperties;
	private final AttachmentPathManager attachmentPathManager;

	public AttachmentsBackdoor(ApplicationProperties applicationProperties, AttachmentPathManager attachmentPathManager) {
		this.applicationProperties = applicationProperties;
		this.attachmentPathManager = attachmentPathManager;
	}

	@GET
	@Path("enable")
	public Response enable() {
		attachmentPathManager.setUseDefaultDirectory();
		applicationProperties.setOption(APKeys.JIRA_OPTION_ALLOWATTACHMENTS, true);
		return Response.ok().cacheControl(never()).build();
	}

	@GET
	@Path("disable")
	public Response disable() {
		attachmentPathManager.disableAttachments();
		return Response.ok().cacheControl(never()).build();
	}
}