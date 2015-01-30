package com.atlassian.jira.testkit.client.restclient;

import java.net.URI;

public class MoveStatus
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
