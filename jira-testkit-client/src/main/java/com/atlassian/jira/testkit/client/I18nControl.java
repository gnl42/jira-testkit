package com.atlassian.jira.testkit.client;

/**
 * TODO: Document this class / interface here
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
