/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import java.util.List;

import static java.lang.String.valueOf;

/**
 * Some helper methods for IssueTypes.
 *
 * See <code>com.atlassian.jira.testkit.plugin.IssueTypeBackdoor</code> in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class IssueTypeControl extends BackdoorControl<IssueTypeControl>
{
    private static final GenericType<List<IssueType>> LIST_GENERIC_TYPE = new GenericType<List<IssueType>>(){};

    public IssueTypeControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public IssueType createIssueType(String name)
    {
        return createIssueType(name, "/images/icons/genericissue.gif");
    }

    public IssueType createIssueType(String name, String iconURL)
    {
        final IssueType issueType = new IssueType();
        issueType.setName(name);
        issueType.setIconUrl(iconURL);
        issueType.setSubtask(false);

        return createIssueType(issueType);
    }

    public IssueType createIssueType(IssueType issueType) {
        return createIssueTypeResource().post(IssueType.class, issueType);
    }

    public List<IssueType> getIssueTypes()
    {
        return createIssueTypeResource().get(LIST_GENERIC_TYPE);
    }

    public List<IssueType> getIssueTypesForProject(final String projectIdOrKey)
    {
        return createIssueTypeResource(projectIdOrKey).get(LIST_GENERIC_TYPE);
    }

    public void deleteIssueType(long id)
    {
        createIssueTypeResource().path(valueOf(id)).delete();
    }

    private WebResource createIssueTypeResource()
    {
        return createResource().path("issueType");
    }

    private WebResource createIssueTypeResource(String projectIdOrKey)
    {
        return createIssueTypeResource().path("project").path(projectIdOrKey);
    }

    public static class IssueType
    {
        private String id;
        private String description;
        private String iconUrl;
        private String name;
        private boolean subtask;

        public IssueType()
        {
        }

        public IssueType(String id, String name, String description, String iconUrl, boolean subtask)
        {
            this.id = id;
            this.description = description;
            this.iconUrl = iconUrl;
            this.name = name;
            this.subtask = subtask;
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public String getIconUrl()
        {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl)
        {
            this.iconUrl = iconUrl;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public boolean isSubtask()
        {
            return subtask;
        }

        public void setSubtask(boolean subtask)
        {
            this.subtask = subtask;
        }
    }
}
