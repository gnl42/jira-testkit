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
