package com.atlassian.jira.testkit.plugin;

public interface AbstractAdapterFactory<T>
{
    boolean isAvailable();

    T create();
}
