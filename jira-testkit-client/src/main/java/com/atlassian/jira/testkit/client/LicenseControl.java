package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.util.TimeBombLicence;

public class LicenseControl extends BackdoorControl<LicenseControl> {
	public LicenseControl(JIRAEnvironmentData environmentData) {
		super(environmentData);
	}

	public boolean switchToPersonalLicense() {
		return post(createResource().path("license").path("set"), TimeBombLicence.V2_PERSONAL, Boolean.class);
	}

    public boolean setLicense(String license) {
        return post(createResource().path("license").path("set"), license, Boolean.class);
    }
}