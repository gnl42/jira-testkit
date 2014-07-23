/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.net.URI;
import java.util.Map;

/**
 * Mimicks UserJsonBean
 *
 * @since v5.0
 */
public class UserJson
{
    public String self;
    public String name;
    public String key;
    public String displayName;
    public boolean active;
    public Map<String, String> avatarUrls;
    public String emailAddress;
    public String timeZone;

    public UserJson self(String self)
    {
        this.self = self;
        return this;
    }

    public UserJson self(URI self)
    {
        this.self = self.toString();
        return this;
    }

    public UserJson name(String name)
    {
        this.name = name;
        return this;
    }

    public UserJson key(String key)
    {
        this.key = key;
        return this;
    }

    public UserJson displayName(String displayName)
    {
        this.displayName = displayName;
        return this;
    }

    public UserJson active(boolean active)
    {
        this.active = active;
        return this;
    }

    public UserJson avatarUrls(Map<String, String> avatarUrls)
    {
        this.avatarUrls = avatarUrls;
        return this;
    }

    public UserJson emailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
        return this;
    }


    public UserJson timeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public enum Expand
    {
        groups
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
