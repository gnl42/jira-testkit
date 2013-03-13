/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient.matcher;

import com.atlassian.jira.testkit.client.restclient.Response;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Matches if the response contains an error message that matches the given matcher.
 *
 * @since v4.3
 */
public class HasErrorMessage extends TypeSafeMatcher<Response>
{
    /**
     * The matcher that will be used on the error messages in the response.
     */
    private final Matcher<? super String> errorMessageMatcher;

    /**
     * Matches a response if it contains an error message that is equal to the given string..
     *
     * @param errorMessage a String containing the expected error message
     * @return a Matcher<Response>
     */
    public static Matcher<Response> hasErrorMessage(String errorMessage)
    {
        return new HasErrorMessage(equalTo(errorMessage));
    }

    /**
     * Matches a response if it contains an error message that matches the given matcher.
     *
     * @param errorMatcher a Matcher<String> that will be used for the error message
     * @return a Matcher<Response>
     */
    public static Matcher<Response> hasErrorMessage(Matcher<String> errorMatcher)
    {
        return new HasErrorMessage(errorMatcher);
    }

    /**
     * Creates a new HasErrorMessage matcher.
     *
     * @param errorMessageMatcher a Matcher<String> that will be used for the error message
     */
    public HasErrorMessage(Matcher<? super String> errorMessageMatcher)
    {
        this.errorMessageMatcher = errorMessageMatcher;
    }

    @Override
    public boolean matchesSafely(Response response)
    {
        if (response.entity != null && response.entity.errorMessages != null)
        {
            for (Object error : response.entity.errorMessages)
            {
                if (errorMessageMatcher.matches(error))
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText("A Response with an error message that is ")
                .appendDescriptionOf(errorMessageMatcher);
    }
}
