package com.atlassian.jira.functest.framework.backdoor;

import com.atlassian.jira.webtests.LicenseKeys;
import com.atlassian.jira.webtests.util.JIRAEnvironmentData;

/**
 * TODO: Document this class / interface here
 *
 * @since v5.2
 */
public class LicenseControl extends BackdoorControl<LicenseControl> {
	public LicenseControl(JIRAEnvironmentData environmentData) {
		super(environmentData);
	}

	public boolean switchToPersonalLicense() {
		return post(createResource().path("license").path("set"), LicenseKeys.V2_PERSONAL.getLicenseString(), Boolean.class);
	}
}
