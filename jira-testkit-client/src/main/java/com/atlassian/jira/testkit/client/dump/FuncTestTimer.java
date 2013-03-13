/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.dump;

/**
 * A timer that can time certain FuncTest operations
 *
 * @since v4.1
 */
public interface FuncTestTimer
{
    /**
     * @return the name of the Timer
     */
    String getName();

    /**
     * Called to end the timer.
     * @return the number of milliseconds since the timer was created
     */
    long end();
}
