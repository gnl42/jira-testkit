/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

/**
 * Object that can deal with JIRA REST dates.
 *
 * @since v4.4
 */

import org.joda.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Locale;

/**
 * Adapter for converting LocalDate to String and vice-versa.
 *
 * @since v4.4
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate>
{
    /**
     * The ISO8601 date format used in the REST plugin.
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public LocalDate unmarshal(String s) throws Exception
    {
        return s == null ? null : new LocalDate(s);
    }

    @Override
    public String marshal(LocalDate date) throws Exception
    {
        return date == null ? null : date.toString(DATE_FORMAT, Locale.US);
    }
}
