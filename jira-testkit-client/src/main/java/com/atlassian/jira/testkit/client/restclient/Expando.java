package com.atlassian.jira.testkit.client.restclient;

import java.util.List;

/**
* Expando attribute.
*
* @since v4.3
*/
public class Expando<T>
{
    public long size;
    public List<T> items;
}
