package com.atlassian.jira.tests;

import com.atlassian.jira.pageobjects.JiraTestedProduct;
import com.atlassian.jira.pageobjects.config.EnvironmentBasedProductInstance;
import com.google.common.base.Preconditions;
import org.junit.rules.ExternalResource;

public class JiraTestedProductHelper extends ExternalResource {
	public JiraTestedProduct jira;

	@Override
	protected void before() throws Throwable {
		jira = new JiraTestedProduct(new EnvironmentBasedProductInstance());
	}

	@Override
	protected void after() {
		jira = null;
	}

	public JiraTestedProduct jira() {
		Preconditions.checkNotNull(jira);
		return jira;
	}
}

