package com.atlassian.jira.testkit.client;

/**
 * Holds user credentials
 *
 * @since v6.0.50
 */
public class UserCredentials
{
    private final String username;
    private final String password;

    public static UserCredentials credentialsFor(final String username, final String password)
    {
        return new UserCredentials(username, password);
    }

    public UserCredentials(final String username, final String password)
    {
        this.username = username;
        this.password = password;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }
}
