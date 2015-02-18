package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;

import java.net.URI;

/**
 * Rest client to invoke methods on arbitrary URI-s.
 */
public final class GenericRestClient extends RestApiClient<GenericRestClient>
{
    public GenericRestClient()
    {
        super(null);
    }

    public <T> Response<T> get(final URI path, Class<T> responseClass)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return client()
                        .resource(path)
                        .queryParam("os_authType", "basic")
                        .queryParam("os_username", percentEncode(loginAs))
                        .queryParam("os_password", percentEncode(loginPassword))
                        .get(ClientResponse.class);
            }
        }, responseClass);
    }
}
