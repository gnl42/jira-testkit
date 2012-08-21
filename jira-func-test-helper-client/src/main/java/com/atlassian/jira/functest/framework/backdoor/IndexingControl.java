package com.atlassian.jira.functest.framework.backdoor;

import com.atlassian.jira.webtests.util.JIRAEnvironmentData;

/**
 * TODO: Document this class / interface here
 *
 * @since v5.2
 */
public class IndexingControl extends BackdoorControl<IndexingControl> {
	public IndexingControl(JIRAEnvironmentData environmentData) {
		super(environmentData);
	}

	public void reIndex() {
		get(createResource().path("indexing").path("reIndex"));
	}
}
