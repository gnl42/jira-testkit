/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.rules;

import com.atlassian.jira.testkit.client.Backdoor;
import com.google.common.collect.ImmutableList;
import org.junit.rules.ExternalResource;

import javax.annotation.Nonnull;

public class EnableDarkFeatureRule extends ExternalResource {
	private final ImmutableList<Backdoor> jiras;
	private final String feature;
	private boolean disableAfter = false;

	public <T extends Backdoor> EnableDarkFeatureRule(@Nonnull String s, T...jiras) {
		this.feature = s;
		this.jiras = ImmutableList.<Backdoor>copyOf(jiras);
	}

	public EnableDarkFeatureRule andDisableAfter() {
		this.disableAfter = true;
		return this;
	}

	@Override
	protected void before() throws Throwable {
		super.before();
		for(Backdoor jira : jiras) {
			jira.darkFeatures().enableForSite(feature);
		}
	}

	@Override
	protected void after() {
		if (this.disableAfter) {
			for(Backdoor jira : jiras) {
				jira.darkFeatures().disableForSite(feature);
			}
		}
		super.after();
	}

}