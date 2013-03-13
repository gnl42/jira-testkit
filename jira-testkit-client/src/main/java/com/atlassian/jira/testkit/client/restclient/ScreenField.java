/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.sun.jersey.api.client.GenericType;

import java.util.List;

public class ScreenField
{

    public static final GenericType<List<ScreenField>> LIST = new GenericType<List<ScreenField>>()
    {
    };

    public String fieldId;
    public String id;
    public String name;

    public ScreenField()
    {
    }

    public ScreenField(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        ScreenField that = (ScreenField) o;

        if (name != null ? !name.equals(that.name) : that.name != null) { return false; }

        return true;
    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }
}
