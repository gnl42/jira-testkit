package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.util.TimeBombLicence;

public class LicenseControl extends BackdoorControl<LicenseControl> {
	public LicenseControl(JIRAEnvironmentData environmentData) {
		super(environmentData);
	}

	public boolean switchToPersonalLicense() {
        return createResource().path("license").path("set").post(Boolean.class, TimeBombLicence.V2_PERSONAL);
    }
}