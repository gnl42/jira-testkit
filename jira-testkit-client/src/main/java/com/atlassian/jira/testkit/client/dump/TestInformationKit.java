/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.dump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Document this class / interface here
 *
 * @since v5.2
 */
public class TestInformationKit
{
    private static Map<String, Map<String, TestCaseCounter>> testCaseCounters = new HashMap<String, Map<String, TestCaseCounter>>();
    private static String currentTestName = null;

    /**
     * This will record a "named" performance counter against the current TestCase in play
     *
     * @param counterName some arbitary counter name
     * @param value the value to record
     */
    public static void recordCounter(String counterName, long value)
    {
        if (currentTestName != null)
        {
            Map<String, TestCaseCounter> counterMap = testCaseCounters.get(currentTestName);
            if (counterMap == null)
            {
                counterMap = new HashMap<String, TestCaseCounter>();
                testCaseCounters.put(currentTestName, counterMap);
            }

            TestCaseCounter testCaseCounter = counterMap.get(counterName);
            if (testCaseCounter == null)
            {
                testCaseCounter = new TestCaseCounter(counterName);
                counterMap.put(counterName, testCaseCounter);
            }
            testCaseCounter.update(value);
        }
    }

    public static class TestCaseCounter
    {
        private final String name;
        private double value;

        private TestCaseCounter(final String name)
        {
            this.name = name;
            this.value = 0;
        }

        public double update(double value)
        {
            this.value += value;
            return this.value;
        }

        public String getName()
        {
            return name;
        }

        public double getValue()
        {
            return value;
        }
    }

    public static List<TestCaseCounter> getCountersForTest(String testName)
    {
        List<TestCaseCounter> counters = new ArrayList<TestCaseCounter>();
        final Map<String, TestCaseCounter> mapOfCounters = testCaseCounters.get(testName);
        if (mapOfCounters != null)
        {
            for (String counterName : mapOfCounters.keySet())
            {
                counters.add(mapOfCounters.get(counterName));
            }
        }
        return counters;
    }

    public static FuncTestTimer pullTimer(String name)
    {
        return new FuncTestTimerImpl(name);
    }
}
