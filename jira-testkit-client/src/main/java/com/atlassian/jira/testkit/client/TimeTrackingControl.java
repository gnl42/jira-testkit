/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.restclient.TimeTracking;

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