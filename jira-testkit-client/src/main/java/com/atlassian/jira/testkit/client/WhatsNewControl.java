package com.atlassian.jira.testkit.client;

public class WhatsNewControl extends BackdoorControl<WhatsNewControl>
{

    /**
     * Creates a new BackdoorControl.
     *
     * @param environmentData a JIRAEnvironmentData
     */
    public WhatsNewControl(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    @Override
    protected String getRestModulePath()
    {
        return "whatsnew";
    }

    public void disableForCurrentlyLoggedInUser()
    {
        createResource().path("show").delete();
    }
}
