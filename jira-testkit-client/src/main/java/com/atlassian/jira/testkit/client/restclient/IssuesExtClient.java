package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.BackdoorControl;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.sun.jersey.api.client.WebResource;

import java.util.Date;

public class IssuesExtClient extends BackdoorControl<IssuesExtClient> {
    public IssuesExtClient(JIRAEnvironmentData environmentData) {
        super(environmentData);
    }

    public void touch(final String key) {
        createResource()
                .path("touch")
                .queryParam("key", key)
                .put();
    }

    public void changeUpdated(final String key, final Date date) {
        createResource()
                .path("changeUpdated")
                .queryParam("key", key)
                .queryParam("date", Long.toString(date.getTime()))
                .put();
    }

    public void changeCreated(final String key, final Date date) {
        createResource()
                .path("changeCreated")
                .queryParam("key", key)
                .queryParam("date", Long.toString(date.getTime()))
                .put();
    }

    @Override
    protected WebResource createResource() {
        return super.createResource().path("issues");
    }
}
