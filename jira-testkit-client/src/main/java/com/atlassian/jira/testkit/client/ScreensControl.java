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

    public ScreensControl addTabToScreen(final String screenName, final String name)
    {
        get(createResource().path("addTab")
                .queryParam("screen", "" + screenName)
                .queryParam("name", name));
        return this;
    }

    public ScreensControl deleteTabFromScreen(final String screenName, final String name)
    {
        get(createResource().path("deleteTab")
                .queryParam("screen", "" + screenName)
                .queryParam("name", name));
        return this;
    }

    public ScreensControl addFieldToScreen(final String screenName, final String fieldName)
    {
        return addFieldToScreen(screenName, fieldName, null, null);
    }

    public ScreensControl addFieldToScreen(final String screenName, final String fieldName, String tabName, String position)
    {
        WebResource query = createResource().path("addField")
                .queryParam("screen", "" + screenName)
                .queryParam("field", fieldName);
        if (tabName != null)
        {
            query = query.queryParam("tab", tabName);
        }
        if (position != null)
        {
            query = query.queryParam("position", position);
        }
        get(query);

        return this;
    }

    public ScreensControl setFieldPosition(final String screenName, final String fieldName, int position)
    {
        get(createResource().path("setFieldPosition")
                .queryParam("screen", "" + screenName)
                .queryParam("field", fieldName)
                .queryParam("position", String.valueOf(position)));
        return this;
    }

    public ScreensControl removeFieldFromScreen(final String screenName, final String fieldName)
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
