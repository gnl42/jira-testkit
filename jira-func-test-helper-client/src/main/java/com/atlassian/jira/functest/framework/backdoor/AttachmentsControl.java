package com.atlassian.jira.functest.framework.backdoor;

import com.atlassian.jira.webtests.util.JIRAEnvironmentData;

/**
 * TODO: Document this class / interface here
 *
 * @since v5.2
 */
public class AttachmentsControl extends BackdoorControl<AttachmentsControl> {
	public AttachmentsControl(JIRAEnvironmentData environmentData) {
		super(environmentData);
	}

	public void enable() {
		get(createResource().path("attachments").path("enable"));
	}

	public void disable() {
		get(createResource().path("attachments").path("disable"));
	}
}
