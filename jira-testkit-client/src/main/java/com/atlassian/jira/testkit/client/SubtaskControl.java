/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;

/**
 * Allows you to enable/disable sub-tasks.
 *
 * See <code>com.atlassian.jira.testkit.plugin.SubtaskBackdoor</code> in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class SubtaskControl extends BackdoorControl<SubtaskControl>
{
    public SubtaskControl(JIRAEnvironmentData environmentData) 
    {
        super(environmentData);
    }
    
    public boolean isEnabled()
    {
        return createSubtaskResource().get(Boolean.class);
    }

    public boolean enable()
    {
        return createSubtaskResource().post(Boolean.class, true);
    }

    public boolean disable()
    {
        return createSubtaskResource().post(Boolean.class, false);
    }

    private WebResource createSubtaskResource()
    {
        return createResource().path("subtask");
    }
}
