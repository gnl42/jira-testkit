/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

/**
 * A matcher to match iterables that contain a string having a given prefix.
 *
 * @since v4.3
 */
public class ContainsStringThatStartsWith<T extends Iterable<String>> extends TypeSafeMatcher<T>
{
    public static <T extends Iterable<String>> Matcher<T> containsStringThatStartsWith(String startsWith)
    {
        return new ContainsStringThatStartsWith<T>(startsWith);
    }

    /**
     * The expected prefix.
     */
    private final String startsWith;

    /**
     * Creates a new ContainsStringStartingWith matcher.
     *
     * @param startsWith the prefix to expect
     */
    public ContainsStringThatStartsWith(String startsWith)
    {
        if (startsWith == null) { throw new NullPointerException("startsWith"); }
        this.startsWith = startsWith;
    }

    @Override
    public boolean matchesSafely(T item)
    {
        for (String string : item)
        {
            if (string.startsWith(startsWith))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText(String.format("An Iterable containing a string that starts with \"%s\"", startsWith));
    }
}
