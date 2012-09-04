package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Representation of a dark feature value in the JIRA internal REST interface.
 *
 * @since v5.2
 */
@JsonSerialize
public class DarkFeature
{
    public boolean enabled;

    public DarkFeature() {}

    public DarkFeature(boolean enabled)
    {
        this.enabled = enabled;
    }
}
