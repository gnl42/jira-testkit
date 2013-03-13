/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

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
