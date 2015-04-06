package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.beans.ProjectTypeBean;
import com.sun.jersey.api.client.WebResource;

import java.util.List;

public class ProjectTypeControl extends BackdoorControl<ProjectTypeControl>
{
    public ProjectTypeControl(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public List<ProjectTypeBean> getAllProjectTypes()
    {
        return createApplicationResource().get(ProjectTypeBean.LIST_TYPE);
    }

    public ProjectTypeBean getByKey(String key)
    {
        return createApplicationResource(key).get(ProjectTypeBean.class);
    }

    private WebResource createApplicationResource(final String key)
    {
        return createApplicationResource().path(key);
    }

    private WebResource createApplicationResource()
    {
        return createResource().path("project-type");
    }
}

