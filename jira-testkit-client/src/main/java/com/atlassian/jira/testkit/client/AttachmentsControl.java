/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;

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

    public String getAttachmentPath() {
        return get(createResource().path("attachments").path("attachmentPath"));
    }

	public void setAttachmentPath(final String newPath) {
		final WebResource resource = createResource().path("attachments").path("attachmentPath");
		resource.entity(newPath, MediaType.TEXT_PLAIN_TYPE).post();
	}
}