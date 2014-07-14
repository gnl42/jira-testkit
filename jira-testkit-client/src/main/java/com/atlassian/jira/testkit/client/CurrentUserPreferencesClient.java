package com.atlassian.jira.testkit.client;

import com.google.common.collect.Sets;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.Set;

/**
 *
 * @since v6.3
 */
public class CurrentUserPreferencesClient extends RestApiClient<CurrentUserPreferencesClient> {
    private static final String PREFERENCES_PATH = "mypreferences";
    private static final String KEY = "key";
    private Set<ClientResponse> responses = Sets.newHashSet();

    public CurrentUserPreferencesClient(JIRAEnvironmentData environmentData) {
        super(environmentData);
    }

    public ClientResponse getPreference(final String key) {
        WebResource webResource = createResource().path(PREFERENCES_PATH);
        if (key != null) {
            webResource = webResource.queryParam(KEY, key);
        }
        final ClientResponse clientResponse = webResource.get(ClientResponse.class);
        responses.add(clientResponse);
        return clientResponse;
    }

    public ClientResponse setPreference(final String key, final String value) {
        WebResource webResource = createResource().path(PREFERENCES_PATH);
        if (key != null) {
            webResource = webResource.queryParam(KEY, key);
        }
        final ClientResponse put = webResource.type("application/json").put(ClientResponse.class, value);
        responses.add(put);
        return put;
    }

    public ClientResponse removePreference(final String key) {
        WebResource webResource = createResource().path(PREFERENCES_PATH);
        if (key != null) {
            webResource = webResource.queryParam(KEY, key);
        }
        final ClientResponse delete = webResource.delete(ClientResponse.class);
        responses.add(delete);
        return delete;
    }

    public void close() {
        for (ClientResponse response : responses) {
            response.close();
        }
    }
}
