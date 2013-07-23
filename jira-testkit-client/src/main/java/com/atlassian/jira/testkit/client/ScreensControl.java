/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.beans.Screen;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import java.util.List;

public class ScreensControl extends BackdoorControl<ScreensControl>
{
    public ScreensControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public List<Screen> getAllScreens()
    {
        return createResource()
                .get(new GenericType<List<Screen>>(){});
    }

    public Screen getScreen(final String screenIdOrName)
    {
        return createResource()
                .queryParam("screen", screenIdOrName)
                .get(Screen.class);
    }

    public ScreensControl addTabToScreen(final String screenName,final String name)
    {
        get(createResource().path("addTab")
                .queryParam("screen", "" + screenName)
                .queryParam("name", name));
        return this;
    }

    public ScreensControl deleteTabFromScreen(final String screenName,final String name)
    {
        get(createResource().path("deleteTab")
                .queryParam("screen", "" + screenName)
                .queryParam("name", name));
        return this;
    }

    public ScreensControl addFieldToScreen(final String screenName,final String fieldName)
    {
        get(createResource().path("addField")
                .queryParam("screen", "" + screenName)
                .queryParam("field", fieldName));
        return this;
    }

    public ScreensControl removeFieldFromScreen(final String screenName,final String fieldName)
    {
        get(createResource().path("removeField")
                .queryParam("screen", "" + screenName)
                .queryParam("field", fieldName));
        return this;
    }

    @Override
    protected WebResource createResource()
    {
        return super.createResource().path("screens");
    }
}
