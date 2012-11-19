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