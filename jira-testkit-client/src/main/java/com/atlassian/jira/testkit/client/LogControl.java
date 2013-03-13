/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

/**
 * Use this class from func/selenium/page-object tests that need to log messages on server side.
 * <p/>
 * See {@link com.atlassian.jira.testkit.plugin.LogAccess} in jira-testkit-plugin for backend.
 *
 * @since v6.0
 */
public class LogControl extends BackdoorControl<LogControl>
{
    public LogControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void error(String msg)
    {
        get(createResource().path("log/error").queryParam("msg", msg));
    }

    public void info(String msg)
    {
        get(createResource().path("log/info").queryParam("msg", msg));
    }
}
