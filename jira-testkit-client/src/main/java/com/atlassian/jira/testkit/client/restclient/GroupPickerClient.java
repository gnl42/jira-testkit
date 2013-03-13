/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.lang.StringUtils;

/**
 * Client for the GroupPicker resource
 *
 * @since v4.4
 */
public class GroupPickerClient extends RestApiClient<GroupPickerClient>
{
    /**
     * Constructs a new GroupPickerClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public GroupPickerClient(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the group suggestions maching the given query string
     *
     * @param query a String to search groups against
     * @return a GroupSuggestions
     * @throws UniformInterfaceException if anything goes wrong
     */
    public GroupSuggestions get(final String query) throws UniformInterfaceException
    {
        return groupsFromQuery(query).get(GroupSuggestions.class);
    }

    /**
     * GETs the group suggestions matching the given query string, returning a Response object.
     *
     * @param query a String to search against
     * @return a Response
     */
    public Response getResponse(final String query)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return groupsFromQuery(query).get(ClientResponse.class);
            }
        });
    }

    /**
     * Returns a WebResponse for the given a group query string.
     *
     * @param query the group query used for searching
     * @return a WebResource
     */
    private WebResource groupsFromQuery(final String query)
    {

        WebResource path = createResource().path("groups").path("picker");
        if(!StringUtils.isBlank(query))
        {
            path = path.queryParam("query", query);
        }
        return path;
    }

    /**
     * Returns a WebResponse for no query string.
     *
     * @return a WebResource
     */
    private WebResource groupsFromQuery()
    {
        return createResource().path("groups").path("picker");
    }

}

