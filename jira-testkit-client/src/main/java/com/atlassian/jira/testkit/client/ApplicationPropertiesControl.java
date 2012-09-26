package com.atlassian.jira.testkit.client;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate
 * ApplicationProperties.
 *
 * See {@link com.atlassian.jira.testkit.plugin.ApplicationPropertiesBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class ApplicationPropertiesControl extends BackdoorControl<ApplicationPropertiesControl>
{
    public ApplicationPropertiesControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public ApplicationPropertiesControl setOption(String key, boolean value)
    {
        get(createResource().path("applicationProperties/option/set")
                .queryParam("key", key)
                .queryParam("value", "" + value));
        return this;
    }

    public ApplicationPropertiesControl setText(String key, String value)
    {
        createResource().path("applicationProperties/text/set").post(String.class, new KeyValueHolder(key, value));
        return this;
    }

    public ApplicationPropertiesControl setString(String key, String value)
    {
        createResource().path("applicationProperties/string/set").post(String.class, new KeyValueHolder(key, value));
        return this;
    }

    public ApplicationPropertiesControl disableXsrfChecking()
    {
        return setOption("jira.xsrf.enabled", false);
    }

    public ApplicationPropertiesControl enableXsrfChecking()
    {
        return setOption("jira.xsrf.enabled", true);
    }

    public String getJiraHome()
    {
        final ConfigInfo info = createResource().path("config-info").get(ConfigInfo.class);
        return info.jiraHomePath;
    }

    private static class KeyValueHolder
    {
        public String key;
        public String value;

        public KeyValueHolder(String key, String value)
        {
            this.key = key;
            this.value = value;
        }
    }

    public static class ConfigInfo
    {
        public String jiraHomePath;
        public boolean isSetUp;

        public ConfigInfo() {}

        public ConfigInfo(String home, boolean  isSetUp)
        {
            this.jiraHomePath = home;
            this.isSetUp = isSetUp;
        }
    }
}
