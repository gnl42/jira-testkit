package com.atlassian.jira.testkit.client;

/**
 * See {@link com.atlassian.jira.testkit.plugin.I18nBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class I18nControl extends BackdoorControl<UsersAndGroupsControl>
{
    public I18nControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public String getText(String key, String locale)
    {
        return createResource().path("i18n")
                .queryParam("key", key)
                .queryParam("locale", locale).get(String.class);
    }
}
