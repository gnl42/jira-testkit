/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.log;

/**
 * Implementors of this interface can log information to the
 * func test log.
 *
 * @since v3.13
 */
public interface FuncTestLogger
{
    /**
     * This will data via a String.valueOf(logData) in the specified object.
     *
     * @param logData the objct to log via String.valueOf().
     */
    public void log(Object logData);

    public void log(Throwable t);

}
