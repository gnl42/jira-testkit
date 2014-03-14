package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * JSON representation of a notification scheme. The current state is not exhaustive of the scheme's state - add
 * attributes as you need them here.
 * @since 6.3
 */
public class NotificationSchemeBean
{
    @JsonProperty
    public final Long id;
    @JsonProperty
    public final String name;

    public NotificationSchemeBean(final Long id, final String name)
    {
        this.id = id;
        this.name = name;
    }
}
