/*
 * Copyright (C) 2012 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.tests;

import com.atlassian.jira.functest.framework.Administration;
import com.atlassian.jira.functest.framework.DefaultFuncTestHttpUnitOptions;
import com.atlassian.jira.functest.framework.FuncTestHelperFactory;
import com.atlassian.jira.functest.framework.FuncTestWebClientListener;
import com.atlassian.jira.functest.framework.Navigation;
import com.atlassian.jira.functest.framework.backdoor.Backdoor;
import com.atlassian.jira.functest.framework.setup.JiraSetupInstanceHelper;
import com.atlassian.jira.pageobjects.pages.JiraLoginPage;
import com.atlassian.jira.webtests.WebTesterFactory;
import com.atlassian.jira.webtests.util.LocalTestEnvironmentData;
import net.sourceforge.jwebunit.WebTester;
import org.junit.rules.ExternalResource;

public class FuncTestHelper extends ExternalResource {
	public final FuncTestHelperFactory factory;
	public final Administration administration;
	public final LocalTestEnvironmentData environmentData;
	public final WebTester webTester;
	public final Navigation navigation;
	public final Backdoor backdoor;

	public FuncTestHelper() {
		DefaultFuncTestHttpUnitOptions.setDefaultOptions();
		environmentData = new LocalTestEnvironmentData();
		webTester = WebTesterFactory.createNewWebTester(environmentData);

		factory = new FuncTestHelperFactory(webTester, environmentData);
		administration = factory.getAdministration();
		navigation = factory.getNavigation();
		backdoor = factory.getBackdoor();

		new JiraSetupInstanceHelper(webTester, environmentData).ensureJIRAIsReadyToGo(new FuncTestWebClientListener());
		new JicWebSudoControl(backdoor, webTester).disable();
	}

	@Override
	protected void before() throws Throwable {
		webTester.beginAt("/");
		navigation.login(JiraLoginPage.USER_ADMIN, JiraLoginPage.PASSWORD_ADMIN);
	}
}