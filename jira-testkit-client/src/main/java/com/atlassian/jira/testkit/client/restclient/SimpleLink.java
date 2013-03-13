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

/**
 * TODO: Document this class / interface here
 *
 * @since v5.0
 */
public class SimpleLink
{
    public String id;
    public String styleClass;
    public String iconClass;
    public String label;
    public String title;
    public String href;
    public Integer weight;


    public SimpleLink id(String id)
    {
        this.id = id;
        return this;
    }

    public SimpleLink styleClass(String styleClass)
    {
        this.styleClass = styleClass;
        return this;
    }

    public SimpleLink iconClass(String iconClass)
    {
        this.iconClass = iconClass;
        return this;
    }

    public SimpleLink label(String label)
    {
        this.label = label;
        return this;
    }

    public SimpleLink title(String title)
    {
        this.title = title;
        return this;
    }

    public SimpleLink href(String href)
    {
        this.href = href;
        return this;
    }

    public SimpleLink weight(Integer weight)
    {
        this.weight = weight;
        return this;
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
