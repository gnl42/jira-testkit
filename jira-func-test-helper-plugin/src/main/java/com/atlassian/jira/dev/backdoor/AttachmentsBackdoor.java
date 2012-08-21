package com.atlassian.jira.dev.backdoor;

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

import static com.atlassian.jira.dev.backdoor.util.CacheControl.never;

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
