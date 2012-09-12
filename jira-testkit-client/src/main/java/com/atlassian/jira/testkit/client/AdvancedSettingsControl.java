package com.atlassian.jira.testkit.client;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Advanced Configuration.
 *
 * See {@link com.atlassian.jira.testkit.plugin.ApplicationPropertiesBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class AdvancedSettingsControl extends BackdoorControl<AdvancedSettingsControl>
{
    public AdvancedSettingsControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public void setTextFieldCharacterLengthLimit(long limit)
    {
        post(createResource().path("applicationProperties/text/set"), new KeyValueHolder("jira.text.field.character.limit", String.valueOf(limit)), String.class);
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
}
