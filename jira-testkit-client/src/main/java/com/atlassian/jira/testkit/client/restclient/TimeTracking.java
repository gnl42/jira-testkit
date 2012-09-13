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

	/**
	 * <p>Represents the "format" which Time Tracking information can be displayed in.</p>
	 * <dl>
	 * <dt>PRETTY</dt>
	 * <dd>e.g. "2 days, 4 hours, 30 minutes"</dd>
	 * <dt>DAYS</dt>
	 * <dd>e.g. "2d 4.5h"</dd>
	 * <dt>HOURS</dt>
	 * <dd>e.g. "52.5h"</dd>
	 * </dl>
	 */
	public enum Format
	{
		PRETTY, DAYS, HOURS
	}

	/**
	 * <p>Represents a "mode" in which the Time Tracking module operates.</p> The current modes are:
	 * <dl>
	 * <dt>MODERN</dt>
	 * <dd>In this mode, the Original and Remaining estimate can be edited independently.</dd>
	 * <dt>LEGACY</dt>
	 * <dd>In this mode, the Original Estimate can only be edited before logging work, and after that the user
	 * can only edit the Remaining Estimate. This is being kept for backwards compatibility</dd>
	 * </dl>
	 */
	public enum Mode
	{
		MODERN("Modern Mode"),
		LEGACY("Legacy Mode");

		private final String value;

		static final String LEGACY_MODE_TEXT = "Legacy mode is currently <b>ON</b>.";

		Mode(final String value)
		{
			this.value = value;
		}

		@Override
		public String toString()
		{
			return value;
		}
	}

	/**
	 * Represnts the default unit to use for time tracking.
	 */
	public enum Unit
	{
		MINUTE, HOUR, DAY, WEEK
	}
}
