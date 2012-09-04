package com.atlassian.jira.testkit.client.restclient;

public class Visibility
{
    public String type;
    public String value;

    public Visibility() {}

    public Visibility(final String type, final String value)
    {
        this.type = type;
        this.value = value;
    }
}
