/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import static java.lang.String.valueOf;

import java.util.List;

import com.atlassian.jira.testkit.beans.Status;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

/**
 * Some helper methods for Statuses.
 * 
 * See <code>com.atlassian.jira.testkit.plugin.StatusBackdoor</code> in
 * jira-testkit-plugin for backend.
 */
public class StatusControl extends BackdoorControl<StatusControl>
{
    private static final String DEFAULT_ICON = "/images/icons/statuses/generic.png";
    private static final GenericType<List<Status>> LIST_GENERIC_TYPE = new GenericType<List<Status>>() {
    };

    public StatusControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public Status createStatus(String name)
    {
        return createStatus(name, "");
    }

    public Status createStatus(String name, String description)
    {
        final Status status = new Status();
        status.setName(name);
        status.setDescription(description);
        status.setIconUrl(DEFAULT_ICON);

        return createStatusResource().post(Status.class, status);
    }

    public void editStatus(String id, String name, String description, String iconUrl)
    {
        final Status status = new Status();
        status.setId(id);
        status.setName(name);
        status.setDescription(description);
        if (iconUrl == null) {
            status.setIconUrl(DEFAULT_ICON);
        } else {
            status.setIconUrl(iconUrl);
        }

        createStatusResource().put(status);
    }

    public List<Status> getStatuses()
    {
        return createStatusResource().get(LIST_GENERIC_TYPE);
    }

    public void deleteStatus(long id)
    {
        createStatusResource().path(valueOf(id)).delete();
    }

    private WebResource createStatusResource()
    {
        return createResource().path("status");
    }
}
