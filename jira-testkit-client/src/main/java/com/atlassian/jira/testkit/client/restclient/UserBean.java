/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @since v5.0
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class UserBean
{
    public String self;
    public String name;
    public String displayName;
    public String key;
    public String password;
    public String emailAddress;
    public String notification;

    public UserBean() {}

    public UserBean(final String name, final String displayName, final String key, final String password, final String emailAddress, final String notification)
    {
        this.name = name;
        this.displayName = displayName;
        this.key = key;
        this.password = password;
        this.emailAddress = emailAddress;
        this.notification = notification;
    }

    @JsonIgnore
    public Builder but()
    {
        return builder()
                .setDisplayName(displayName)
                .setEmailAddress(emailAddress)
                .setKey(key)
                .setName(name)
                .setNotification(notification)
                .setPassword(password);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private String name;
        private String displayName;
        private String key;
        private String password;
        private String emailAddress;
        private String notification;

        public Builder setName(final String name)
        {
            this.name = name;
            return this;
        }

        public Builder setDisplayName(final String displayName)
        {
            this.displayName = displayName;
            return this;
        }

        public Builder setKey(final String key)
        {
            this.key = key;
            return this;
        }

        public Builder setPassword(final String password)
        {
            this.password = password;
            return this;
        }

        public Builder setEmailAddress(final String emailAddress)
        {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder setNotification(final String notification)
        {
            this.notification = notification;
            return this;
        }

        public UserBean build()
        {
            return new UserBean(name, displayName, key, password, emailAddress, notification);
        }
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getKey()
    {
        return key;
    }

    public String getPassword()
    {
        return password;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public String getNotification()
    {
        return notification;
    }

    public String getSelf()
    {
        return self;
    }
}
