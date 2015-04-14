package com.atlassian.jira.testkit.client.util;

import com.atlassian.fugue.Option;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.some;

/**
 * Class that contain static factory methods for JSON matchers.
 */
public final class JsonMatchers
{
    /**
     * Asserts that a JSON object has the specified field defined.
     *
     * @param field field, or a chain of getters (e.g. "issue.fields.summary")
     * @return hamcrest matcher
     */
    public static JsonHasFieldMatcher hasField(final String field)
    {
        return new JsonHasFieldMatcher(field);
    }

    public static final class JsonHasFieldMatcher extends TypeSafeMatcher<JSONObject>
    {
        private final String field;

        private JsonHasFieldMatcher(String field)
        {
            this.field = field;
        }

        /**
         * After asserting that the field actually exists, check the field's value.
         *
         * For example:
         *
         * <pre>assertThat(json, hasField("version.id").equalTo(42));</pre>
         *
         * @param value expected value
         * @return a matcher
         */
        public Matcher<JSONObject> equalTo(final Object value)
        {
            return Matchers.allOf(Lists.<Matcher<? super JSONObject>>newArrayList(this, new TypeSafeMatcher<JSONObject>()
            {
                @Override
                protected boolean matchesSafely(JSONObject item)
                {
                    return value.equals(getValueOfField(item).getOrNull());
                }

                public void describeTo(Description description)
                {
                    description.appendText("that it is equal to '" + value + "'");
                }
            }));
        }

        @Override
        protected boolean matchesSafely(JSONObject item)
        {
            return getValueOfField(item).isDefined();
        }

        private Option<Object> getValueOfField(JSONObject item)
        {
            JSONObject object = item;
            LinkedList<String> chain = Lists.newLinkedList(Splitter.on('.').split(field));

            try
            {
                while (chain.size() > 1)
                {
                    object = object.getJSONObject(chain.pop());
                }
                return some(object.get(chain.pop()));
            }
            catch (JSONException e)
            {
                return none();
            }
        }

        public void describeTo(Description description)
        {
            description.appendText("JSON expected to have field '" + field + "'");
        }
    }
}
