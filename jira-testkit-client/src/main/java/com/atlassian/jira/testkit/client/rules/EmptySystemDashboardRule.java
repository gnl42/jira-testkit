package com.atlassian.jira.testkit.client.rules;

import com.atlassian.jira.testkit.client.Backdoor;
import com.google.common.collect.ImmutableList;
import org.junit.rules.ExternalResource;import java.lang.Override;import java.lang.Throwable;

/**
 *
 * @since v2.1
 */
public class EmptySystemDashboardRule extends ExternalResource {
	private final ImmutableList<Backdoor> jiras;

	public <T extends Backdoor> EmptySystemDashboardRule(T... jiras) {
		this.jiras = ImmutableList.<Backdoor>copyOf(jiras);
	}

	@Override
	protected void before() throws Throwable {
		super.before();
		for(Backdoor jira : jiras) {
			jira.dashboard().emptySystemDashboard();
		}
	}
}
