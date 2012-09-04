package com.atlassian.jira.testkit.client.restclient;

import java.net.URI;

public class MoveField
{

    public enum Position
    {
        Earlier,
        Later,
        First,
        Last
    }

    public URI after;
    public Position position;

}
