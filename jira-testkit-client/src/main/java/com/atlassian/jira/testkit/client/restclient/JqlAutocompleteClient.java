package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Client for querying JQL Autocomplete (to test for suggestions based on field and partially typed value).
 *
 * @since v5.0.29
 */
public class JqlAutocompleteClient extends RestApiClient<JqlAutocompleteClient>
{
    /**
     * Constructs a new JqlAutocompleteClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public JqlAutocompleteClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData, "1.0");
    }

    /**
     * Get suggestions for the field and partially typed value.
     *
     * @param fieldName the field name
     * @param fieldValue the partial value
     * @return results object
     */
    public JqlAutocompleteResults getAutocomplete(final String fieldName, final String fieldValue)
    {
        return autocompleteResource(fieldName, fieldValue).get(JqlAutocompleteResults.class);
    }

    /**
     * Get response of call to autocomplete end point.
     * @param fieldName the field name
     * @param fieldValue the partial value
     * @return response object
     * @see #getAutocomplete(String, String)
     */
    public Response getAutocompleteResponse(final String fieldName, final String fieldValue)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return autocompleteResource(fieldName, fieldValue).get(ClientResponse.class);
            }
        });
    }

    /**
     * Creates a WebResource.Builder for the autocomplete resource.
     *
     * @return a WebResource.Builder
     */
    private WebResource autocompleteResource(String fieldName, String fieldValue)
    {
        return createResource()
                .path("jql").path("autocomplete")
                .queryParam("fieldName", fieldName)
                .queryParam("fieldValue", fieldValue);
    }
}
