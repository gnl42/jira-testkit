package com.atlassian.jira.testkit.client;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate User Profiles.
 *
 * See UserProfileBackdoor for the code this plugs into at the back-end.
 *
 * @since v5.0
 */
public class UserProfileControl extends BackdoorControl<UserProfileControl>
{
    public UserProfileControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * Changes the given user's email format to the one supplied.
     *
     * @param username the user to change the email format for
     * @param format either "html" or "text"
     */
    @SuppressWarnings ("UnusedDeclaration")
    public void changeUserNotificationType(String username, String format)
    {
        get(createResource().path("userProfile/notificationType/set")
                .queryParam("username", username)
                .queryParam("format", format));
    }

    /**
     * Changes the given user's time zone to one supplied.
     *
     * @param username the user to change the time zone for.
     * @param timeZone the time zone to set.
     */
    @SuppressWarnings ("UnusedDeclaration")
    public void setUserTimeZone(String username, String timeZone)
    {
        createResource().path("userProfile/timeZone")
                .queryParam("username", username)
                .queryParam("timeZone", timeZone)
                .put();
    }
}
