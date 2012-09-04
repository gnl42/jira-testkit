package com.atlassian.jira.testkit.client.restclient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Representation for a time tracking entry.
 *
 * @since v4.3
 */
public class TimeTracking
{
    public String originalEstimate;
    public String remainingEstimate;
    public String timeSpent;
    public Long originalEstimateSeconds;
    public Long remainingEstimateSeconds;
    public Long timeSpentSeconds;

    @Override
    public boolean equals(Object o)
    {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
