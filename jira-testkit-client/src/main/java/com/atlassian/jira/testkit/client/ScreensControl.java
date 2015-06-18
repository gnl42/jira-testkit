package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.beans.Screen;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import org.apache.axis.utils.StringUtils;

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

    public ScreensControl addFieldToScreenTab(final String screenName, final String tabName, final String fieldName, String position)
    {
        final WebResource webResource = createResource().path("screens").path("addField")
                .queryParam("screen", "" + screenName)
                .queryParam("tab", tabName)
                .queryParam("field", fieldName);


        if (position != null && !StringUtils.isEmpty(position))
        {
            webResource.queryParam("position", position);
        }
        get(webResource);
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
