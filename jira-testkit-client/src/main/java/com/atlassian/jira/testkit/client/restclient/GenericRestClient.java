package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;

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
                return getResource(path);
            }
        }, responseClass);
    }

    public <T> Response<T> get(final URI path, GenericType<T> responseType)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return getResource(path);
            }
        }, responseType);
    }


    public <T> Response<PageBean<T>> getNextPage(PageBean<T> currentPage, GenericType<PageBean<T>> actualPageType)
    {
        if (Boolean.TRUE.equals(currentPage.getIsLastPage()) || currentPage.getNextPage() == null) {
            throw new IllegalArgumentException("last page or next URL not specified");
        }

        return get(currentPage.getNextPage(), actualPageType);
    }


    private ClientResponse getResource(final URI path)
    {
        return client()
                .resource(path)
                .queryParam("os_authType", "basic")
                .queryParam("os_username", percentEncode(loginAs))
                .queryParam("os_password", percentEncode(loginPassword))
                .get(ClientResponse.class);
    }
}
