package com.atlassian.jira.testkit.client;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Dark Features.
 *
 * See {@link com.atlassian.jira.testkit.plugin.DarkFeaturesBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class DarkFeaturesControl extends BackdoorControl<DarkFeaturesControl>
{
    public DarkFeaturesControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void enableForUser(String username, String feature)
    {
        get(createResource().path("darkFeatures").path("user").path("enable").queryParam("username", username).queryParam("feature", feature));
    }

    public void disableForUser(String username, String feature)
    {
        get(createResource().path("darkFeatures").path("user").path("disable").queryParam("username", username).queryParam("feature", feature));
    }

    public void enableForSite(String feature)
    {
        get(createResource().path("darkFeatures").path("site").path("enable").queryParam("feature", feature));
    }

    public void disableForSite(String feature)
    {
        get(createResource().path("darkFeatures").path("site").path("disable").queryParam("feature", feature));
    }
    
    public boolean isGlobalEnabled(String feature)
    {
        return Boolean.parseBoolean(get(createResource().path("darkFeatures").path("global").path("enabled").queryParam("feature", feature)));
    }
}
