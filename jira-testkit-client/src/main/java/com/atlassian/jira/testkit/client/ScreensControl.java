/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

public class ScreensControl extends BackdoorControl<ScreensControl>
{
    public ScreensControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public ScreensControl addTabToScreen(final String screenName,final String name)
    {
        get(createResource().path("screens").path("addTab")
                .queryParam("screen", "" + screenName)
                .queryParam("name", name));
        return this;
    }

    public ScreensControl deleteTabFromScreen(final String screenName,final String name)
    {
        get(createResource().path("screens").path("deleteTab")
                .queryParam("screen", "" + screenName)
                .queryParam("name", name));
        return this;
    }

    public ScreensControl addFieldToScreen(final String screenName,final String fieldName)
    {
        get(createResource().path("screens").path("addField")
                .queryParam("screen", "" + screenName)
                .queryParam("field", fieldName));
        return this;
    }

    public ScreensControl removeFieldFromScreen(final String screenName,final String fieldName)
    {
        get(createResource().path("screens").path("removeField")
                .queryParam("screen", "" + screenName)
                .queryParam("field", fieldName));
        return this;
    }

}
