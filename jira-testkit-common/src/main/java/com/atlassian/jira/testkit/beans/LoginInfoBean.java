package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Represents login info
 *
 * @since 5.2-m29
 */
public class LoginInfoBean
{
    @JsonProperty
    private long loginCount;

    @JsonProperty
    private long currentFailedLoginCount;

    @JsonProperty
    private long totalFailedLoginCount;


    public long getLoginCount()
    {
        return loginCount;
    }

    public void setLoginCount(final long loginCount)
    {
        this.loginCount = loginCount;
    }

    public long getCurrentFailedLoginCount()
    {
        return currentFailedLoginCount;
    }

    public void setCurrentFailedLoginCount(final long currentFailedLoginCount)
    {
        this.currentFailedLoginCount = currentFailedLoginCount;
    }

    public long getTotalFailedLoginCount()
    {
        return totalFailedLoginCount;
    }

    public void setTotalFailedLoginCount(final long totalFailedLoginCount)
    {
        this.totalFailedLoginCount = totalFailedLoginCount;
    }

}
