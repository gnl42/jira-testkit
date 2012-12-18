package com.atlassian.jira.testkit.client;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate
 * ApplicationProperties.
 *
 * See ApplicationPropertiesBackdoor for the code this plugs into at the back-end.
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
        post(createResource().path("applicationProperties/text/set"), new KeyValueHolder(key, value), String.class);
        return this;
    }

    public ApplicationPropertiesControl setString(String key, String value)
    {
        post(createResource().path("applicationProperties/string/set"), new KeyValueHolder(key, value), String.class);
        return this;
    }

    public String getString(String key){
        return get(createResource().path("applicationProperties/string/get")
                .queryParam("key", key));
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
        final ConfigInfo info = get(createResource().path("config-info"), ConfigInfo.class);
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
