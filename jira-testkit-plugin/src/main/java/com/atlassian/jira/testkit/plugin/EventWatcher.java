package com.atlassian.jira.testkit.plugin;

import java.util.Collection;

/**
 * @since v5.0
 */
public interface EventWatcher
{
    void listen(final Object object);
    Collection<String> getEvents();
}
