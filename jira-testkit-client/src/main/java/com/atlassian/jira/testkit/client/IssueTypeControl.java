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
 * See {@link com.atlassian.jira.testkit.plugin.IssueTypeBackdoor} in jira-testkit-plugin for backend.
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

        return createIssueTypeResource().post(IssueType.class, issueType);
    }

    public List<IssueType> getIssueTypes()
    {
        return createIssueTypeResource().get(LIST_GENERIC_TYPE);
    }

    public void deleteIssueType(long id)
    {
        createIssueTypeResource().path(valueOf(id)).delete();
    }

    public void translateIssueConstants(IssueConstantTranslation issueConstantTranslation)
    {
        createIssueTypeResource().path("translateConstants").put(issueConstantTranslation);
    }

    private WebResource createIssueTypeResource()
    {
        return createResource().path("issueType");
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

    public static class IssueConstantTranslation
    {
        private String constantName;
        private String locale;
        private String name;
        private String description;
        private String constantType;

        public IssueConstantTranslation()
        {
        }

        public IssueConstantTranslation(String constantName, String locale, String name, String description, String constantType)
        {
            this.constantName = constantName;
            this.locale = locale;
            this.name = name;
            this.description = description;
            this.constantType = constantType;
        }

        public String getConstantName()
        {
            return constantName;
        }

        public void setConstantName(String constantName)
        {
            this.constantName = constantName;
        }

        public String getLocale()
        {
            return locale;
        }

        public void setLocale(String locale)
        {
            this.locale = locale;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public String getConstantType()
        {
            return constantType;
        }

        public void setConstantType(String constantType)
        {
            this.constantType = constantType;
        }
    }
}
