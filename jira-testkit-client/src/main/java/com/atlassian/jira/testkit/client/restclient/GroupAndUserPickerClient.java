package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class GroupAndUserPickerClient extends RestApiClient<GroupAndUserPickerClient>
{
    public GroupAndUserPickerClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public GroupAndUserPickerClient(JIRAEnvironmentData environmentData, String version)
    {
        super(environmentData, version);
    }

    /**
     * GETs the group suggestions maching the given query string
     *
     * @param query a String to search groups against
     * @return a GroupSuggestions
     * @throws com.sun.jersey.api.client.UniformInterfaceException if anything goes wrong
     */
    public GroupAndUserSuggestions get(final String query) throws UniformInterfaceException
    {
        return groupsAndUsersFromQuery(query).get(GroupAndUserSuggestions.class);
    }

    /**
     * Returns a WebResponse for no query string.
     *
     * @return a WebResource
     */
    private WebResource groupsAndUsersFromQuery(String query)
    {
        return createResource().path("groupuserpicker").queryParam("query", query);
    }
}
