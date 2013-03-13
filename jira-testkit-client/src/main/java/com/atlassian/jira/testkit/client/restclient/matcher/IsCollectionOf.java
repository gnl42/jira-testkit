/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient.matcher;

import com.atlassian.jira.testkit.client.restclient.Project;
import com.atlassian.jira.util.Function;
import com.atlassian.jira.util.collect.Transformed;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @since v4.3
 */
public class IsCollectionOf<T> extends TypeSafeMatcher<Collection<T>> {

    private final Collection<T> expected;

    public IsCollectionOf(Collection<T> expected)
    {
        this.expected = expected;
    }

    public boolean matchesSafely(Collection<T> given)
    {
        List<T> tmp = new ArrayList<T>(expected);
        for (T t : given)
        {
            //noinspection SimplifiableIfStatement
            if (!tmp.remove(t))
            {
                return false;
            }
        }
        return tmp.isEmpty();
    }

    public static Matcher<Collection<String>> ofProjectKeys(List<Project> projects)
    {
        final Collection<String> projectKeys = Transformed.collection(projects, new Function<Project, String>()
        {
            @Override
            public String get(Project project)
            {
                return project.key;
            }
        });

        return new IsCollectionOf<String>(projectKeys);
    }
    public static <T> Matcher<Collection<T>> ofItems(T... items)
    {
        return new IsCollectionOf<T>(Arrays.asList(items));
    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText(expected.toString());
    }
}
