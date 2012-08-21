package com.atlassian.jira.functest.framework.backdoor;

import com.atlassian.jira.functest.framework.admin.TimeTracking;
import com.atlassian.jira.webtests.util.JIRAEnvironmentData;

/**
 * TODO: Document this class / interface here
 *
 * @since v5.2
 */
public class TimeTrackingControl extends BackdoorControl<TimeTrackingControl> {
	public TimeTrackingControl(JIRAEnvironmentData environmentData) {
		super(environmentData);
	}

	public void enable(String hoursPerDay, String daysPerWeek, TimeTracking.Format format, TimeTracking.Unit unit, TimeTracking.Mode mode) {
		get(createResource().path("timeTracking").path("enable").queryParam("hoursPerDay", hoursPerDay).queryParam("daysPerWeek", daysPerWeek)
				.queryParam("format", format.toString()).queryParam("unit", unit.toString()).queryParam("legacy",
						String.valueOf(TimeTracking.Mode.LEGACY.equals(mode))));
	}
}
