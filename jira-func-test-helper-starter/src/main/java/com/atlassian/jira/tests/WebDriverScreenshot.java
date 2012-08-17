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

import com.atlassian.jira.functest.framework.WebTestDescription;
import com.atlassian.jira.functest.framework.suite.JUnit4WebTestDescription;
import com.atlassian.jira.pageobjects.JiraTestedProduct;
import com.atlassian.jira.pageobjects.config.TestEnvironment;
import com.atlassian.webdriver.AtlassianWebDriver;
import org.apache.commons.io.FileUtils;
import org.hamcrest.StringDescription;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class WebDriverScreenshot extends TestWatcher {
	private static final Logger logger = LoggerFactory.getLogger(WebDriverScreenshot.class);

	private final AtlassianWebDriver webDriver;
	private final TestEnvironment testEnvironment = new TestEnvironment();

	public WebDriverScreenshot(AtlassianWebDriver webDriver) {
		this.webDriver = webDriver;
	}

	public WebDriverScreenshot(JiraTestedProduct product) {
		this(product.getTester().getDriver());
	}

	@Override
	protected void failed(Throwable e, Description description) {
		attemptScreenshot(new JUnit4WebTestDescription(description));
	}

	private void attemptScreenshot(WebTestDescription description)
	{
		if (!isScreenshotCapable())
		{
			logger.warn(new StringDescription().appendText("Unable to take screenshot: WebDriver ")
					.appendValue(webDriver.getDriver()).appendText(" is not instance of TakesScreenshot").toString());
			return;
		}
		takeScreenshot(description);
	}

	private void takeScreenshot(WebTestDescription description)
	{
		try
		{
			TakesScreenshot takingScreenshot = (TakesScreenshot) webDriver.getDriver();
			File screenshot = takingScreenshot.getScreenshotAs(OutputType.FILE);
			File target = new File(testEnvironment.artifactDirectory(), fileName(description));
			FileUtils.copyFile(screenshot, target);
			logger.info("A screenshot of the page has been stored under " + target.getAbsolutePath());
		}
		catch(Exception e)
		{
			logger.error(new StringDescription().appendText("Unable to take screenshot for failed test ")
					.appendValue(description).toString(), e);
		}
	}

	private String fileName(WebTestDescription description)
	{
		return description.testClass().getSimpleName() + "." + description.methodName() + ".png";
	}

	private boolean isScreenshotCapable()
	{
		return webDriver.getDriver() instanceof TakesScreenshot;
	}
}

