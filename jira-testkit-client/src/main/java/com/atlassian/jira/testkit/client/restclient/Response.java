/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode;

/**
 * An response, with optional error messages.
 *
 * @since v4.3
 */
public class Response<T>
{
    public final int statusCode;
    public final Errors entity;
    public final T body;

    public Response(int statusCode, Errors entity)
    {
        this.statusCode = statusCode;
        this.entity = entity;
        this.body = null;
    }

    public Response(int statusCode, Errors entity, T body)
    {
        this.statusCode = statusCode;
        this.entity = entity;
        this.body = body;
    }

    @Override
    public boolean equals(Object obj)
    {
        return reflectionEquals(this, obj);
    }

    @Override
    public int hashCode()
    {
        return reflectionHashCode(this);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
