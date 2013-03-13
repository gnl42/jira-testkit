/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Representation of a dark feature value in the JIRA internal REST interface.
 *
 * @since v5.2
 */
@JsonSerialize
public class DarkFeature
{
    public boolean enabled;

    public DarkFeature() {}

    public DarkFeature(boolean enabled)
    {
        this.enabled = enabled;
    }
}
