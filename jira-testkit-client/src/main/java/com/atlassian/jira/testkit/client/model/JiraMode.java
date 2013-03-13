/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.model;

/**
 * Config property indicting JIRA operation mode: private or public.
 *
 * @since 5.1
 */
public enum JiraMode
{
    PUBLIC,
    PRIVATE;

    public static JiraMode fromValue(String optionValue)
    {
        for (JiraMode mode : values())
        {
            if (mode.optionValue().equals(optionValue))
            {
                return mode;
            }
        }
        throw new IllegalArgumentException("No mode with option value '" + optionValue + "'");
    }

    public static JiraMode forPublicModeEnabledValue(boolean publicModeEnabled)
    {
        return publicModeEnabled ? PUBLIC : PRIVATE;
    }


    public String optionValue()
    {
        return name().toLowerCase();
    }

}
