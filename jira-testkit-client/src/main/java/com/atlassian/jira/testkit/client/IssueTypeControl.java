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
        final IssueType issueType = new IssueType();
        issueType.setName(name);
        issueType.setIconUrl("/images/icons/genericissue.gif");
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
}
