/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.jerseyclient.ApacheClientFactoryImpl;
import com.atlassian.jira.testkit.client.jerseyclient.JerseyClientFactory;
import com.atlassian.jira.testkit.client.restclient.Errors;
import com.atlassian.jira.testkit.client.restclient.Response;
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nonnull;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * Abstract base class for REST API clients.
 *
 * @since v5.0
 */
public abstract class RestApiClient<T extends RestApiClient<T>>
{
    /**
     * Logger for this client.
     */
    private static final Logger log = LoggerFactory.getLogger(RestApiClient.class);

    /**
     * The REST plugin version to test.
     */
    public static final String REST_VERSION = "2";

    /**
     * Lazily-instantiated Jersey client in thread local variable.
     */
    private static ThreadLocal<Client> client = new ThreadLocal<Client>()
    {
        @Override
        protected Client initialValue()
        {
            final DefaultClientConfig config = new DefaultClientConfig();

            final JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
            jacksonProvider.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            config.getSingletons().add(jacksonProvider);
            config.getClasses().add(MultiPartWriter.class);

            final JerseyClientFactory clientFactory = new ApacheClientFactoryImpl(config);
            Client client = clientFactory.create();
            if (log.isDebugEnabled())
            {
                client.addFilter(new LoggingFilter());
            }
            return client;
        }
    };

    /**
     * The JIRA environment data
     */
    private final JIRAEnvironmentData environmentData;

    /**
     * The user to log in as.
     */
    protected String loginAs = "admin";

    protected String loginPassword = loginAs;

    private final Set<ClientResponse> responses = Sets.newHashSet();

    /**
     * The version of the REST plugin to test.
     */
    private String version;

    /**
     * Constructs a new RestApiClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    protected RestApiClient(JIRAEnvironmentData environmentData)
    {
        this(environmentData, REST_VERSION);
    }

    /**
     * Constructs a new RestApiClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     * @param version a String containing the version to test against
g     */
    protected RestApiClient(JIRAEnvironmentData environmentData, String version)
    {
        this.environmentData = environmentData;
        this.version = version;
    }

    /**
     * Ensures that this client does not authenticate when making a request.
     *
     * @return this
     */
    @SuppressWarnings ("unchecked")
    public T anonymous()
    {
        loginAs = null;
        loginPassword = null;
        return (T) this;
    }

    /**
     * Makes this client authenticate as the given user.
     *
     * @param username a String containing the username
     * @return this
     */
    @SuppressWarnings ("unchecked")
    public T loginAs(String username)
    {
        return loginAs(username, username);
    }

    /**
     * Makes this client authenticate as the given <tt>username</tt> and <tt>password</tt>.
     *
     * @param username a String containing the username
     * @param password a String containing the passoword
     * @return this
     */
    @SuppressWarnings ("unchecked")
    public T loginAs(String username, String password)
    {
        loginAs = username;
        loginPassword = password;
        return (T) this;
    }

    @Nonnull
    public JIRAEnvironmentData getEnvironmentData()
    {
        return environmentData;
    }

    /**
     * Creates the resource that corresponds to the root of the REST API.
     *
     * @return a WebResource for the REST API root
     */
    protected WebResource createResource()
    {
        return resourceRoot(environmentData.getBaseUrl().toExternalForm()).path("rest").path("api").path(version);
    }

    /**
     * Creates the resource that corresponds to the root of the internal REST interface.
     *
     * @return a WebResource for the internal REST interface root
     */
    protected WebResource createResourceInternal()
    {
        return resourceRoot(environmentData.getBaseUrl().toExternalForm()).path("rest").path("internal").path("1.0");
    }

    /**
     * Creates the resource that corresponds to the root of the gadget REST interface.
     *
     * @return a WebResource for the gadget REST interface root
     */
    protected WebResource createResourceGadget()
    {
        return resourceRoot(environmentData.getBaseUrl().toExternalForm()).path("rest").path("gadget").path("1.0");
    }

    /**
     * Creates a WebResource for the given URL. The relevant authentication parameters are added to the resource, if
     * applicable.
     *
     * @param url a String containing a URL
     * @return a WebResource, with optional authentication parameters
     */
    protected WebResource resourceRoot(String url)
    {
        WebResource resource = client().resource(url);
        if (loginAs != null)
        {
            resource = resource.queryParam("os_authType", "basic")
                    .queryParam("os_username", percentEncode(loginAs))
                    .queryParam("os_password", percentEncode(loginPassword));
        }

        return resource;
    }

    /**
     * Returns the Jersey client to use.
     *
     * @return a Client
     */
    protected final Client client()
    {
        return client.get();
    }

    protected Response<?> toResponse(Method method)
    {
        ClientResponse clientResponse = registerResponse(method.call());
        if (clientResponse.getStatus() == 200)
        {
            final Response<?> response = new Response(clientResponse.getStatus(), null);
            clientResponse.close();
            return response;
        }

        return errorResponse(clientResponse);
    }

    protected <T> Response<T> toResponse(Method method, Class<T> clazz)
    {
         return toResponse(method, new GenericType<T>(clazz));
    }

    protected <T> Response<T> toResponse(Method method, GenericType<T> clazz)
    {
        ClientResponse clientResponse = registerResponse(method.call());
        if (clientResponse.getStatus() < 300)
        {
            T object = null;
            if (clientResponse.hasEntity())
            {
                object = clientResponse.getEntity(clazz);
            }
            final Response<T> tResponse = new Response<T>(clientResponse.getStatus(), null, object);
            clientResponse.close();
            return tResponse;
        }

        return errorResponse(clientResponse);
    }

    protected <T> Response<T> errorResponse(ClientResponse clientResponse)
    {
        Errors entity = null;
        if (clientResponse.hasEntity() && APPLICATION_JSON_TYPE.isCompatible(clientResponse.getType()))
        {
            try
            {
                entity = clientResponse.getEntity(Errors.class);
            }
            catch (Exception e) { log.debug("Failed to deserialise Errors from response", e); }
        }

        final Response<T> response = new Response<T>(clientResponse.getStatus(), entity);
        clientResponse.close();
        return response;
    }

    /**
     * Adds the expand query param to the given WebResource. The name of the attributes to expand must exactly match the
     * name of the enum instances that are passed in.
     *
     * @param resource a WebResource
     * @param expands an EnumSet containing the attributes to expand
     * @return the input WebResource, with added expand parameters
     */
    protected WebResource expanded(WebResource resource, EnumSet<?> expands)
    {
        if (expands.isEmpty())
        {
            return resource;
        }

        return resource.queryParam("expand", percentEncode(StringUtils.join(expands, ",")));
    }

    protected ClientResponse registerResponse(ClientResponse response) {
        responses.add(response);
        return response;
    }

    public void cleanUp()
    {
        for(ClientResponse respnse : responses)
        {
            respnse.close();
        }
        responses.clear();
    }

    /**
     * Constructs an EnumSet from a var-args param.
     *
     * @param cls the Enum class object
     * @param expand the enum instances to expand
     * @param <E> the Enum class
     * @return an EnumSet
     */
    protected static <E extends Enum<E>> EnumSet<E> setOf(Class<E> cls, E... expand)
    {
        return expand.length == 0 ? EnumSet.noneOf(cls) : EnumSet.of(expand[0], expand);
    }

    /**
     * Percent-encode the % when stuffing it into a query param. Otherwise it may not get escaped properly, as per <a
     * href="https://extranet.atlassian.com/x/v4Qlbw">this EAC blog</a>.
     *
     * @param queryParam the query param value
     * @return a String with % replaced by %25
     */
    protected static String percentEncode(String queryParam)
    {
        return queryParam == null ? null : queryParam.replace("%", "%25");
    }

    /**
     * Method interface to use with getResponse.
     */
    public static interface Method
    {
        ClientResponse call();
    }
}
