package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.dump.FuncTestTimer;
import com.atlassian.jira.testkit.client.dump.TestInformationKit;
import com.atlassian.jira.testkit.client.log.FuncTestLogger;
import com.atlassian.jira.testkit.client.log.FuncTestLoggerImpl;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.MediaType;

/**
 * Parent class for Backdoor controllers making func-test REST requests to set up test state without the UI overhead.
 *
 * @since v5.0
 */
public abstract class BackdoorControl<T extends BackdoorControl<T>> extends RestApiClient<T>
{
    /**
     * The default REST path for the TestKit resources.
     */
    public static final String DEFAULT_REST_PATH = "testkit-test";

    /**
     * The JIRA base URL.
     */
    protected final String rootPath;

    /**
     * The FuncTestLogger to use for logging.
     */
    protected final FuncTestLogger logger;

    /**
     * Creates a new BackdoorControl.
     *
     * @param environmentData a JIRAEnvironmentData
     */
    public BackdoorControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
        this.rootPath = environmentData.getBaseUrl().toExternalForm();
        this.logger = new FuncTestLoggerImpl(2);
    }

    /**
     * Convenience method that simply calls {@code WebResource.get(String.class)} on the passed-in WebResource.
     * <p/>
     * <em>This method is often used for mutative operations</em> which should really be using a POST instead, but doing
     * a GET is much more convenient since you can do it right from your browser when testing a backdoor and this is
     * only testing code so it's OK.
     *
     * @param webResource the WebResource to perform the GET on
     */
    protected final String get(final WebResource webResource)
    {
        return webResource.get(String.class);
    }

    /**
     * Deprecated: use {@link WebResource#get(com.sun.jersey.api.client.GenericType)} instead.
     * <p/>
     * <b>This method will be removed in 5.2.</b>
     */
    @Deprecated
    protected <T> T get(WebResource webResource, GenericType<T> returnType)
    {
        return webResource.get(returnType);
    }

    /**
     * Deprecated: use {@link WebResource#get(Class)} instead.
     * <p/>
     * <b>This method will be removed in 5.2.</b>
     */
    @Deprecated
    protected <T> T get(WebResource webResource, Class<T> returnClass)
    {
        return webResource.get(returnClass);
    }

    /**
     * Deprecated: use {@link WebResource#get(Class)} instead.
     * <p/>
     * <b>This method will be removed in 5.2.</b>
     */
    @Deprecated
    protected long getId(final WebResource webResource)
    {
        return Long.parseLong(get(webResource));
    }

    /**
     * Deprecated: use {@link com.sun.jersey.api.client.WebResource#post()} instead.
     * <p/>
     * <b>This method will be removed in 5.2.</b>
     */
    @Deprecated
    protected void post(WebResource webResource)
    {
        webResource.post();
    }

    /**
     * Deprecated: use {@link WebResource#post(Class, Object)} instead.
     * <p/>
     * <b>This method will be removed in 5.2.</b>
     */
    protected <T> T post(WebResource webResource, Object bean, Class<T> returnClass)
    {
        return webResource.type(MediaType.APPLICATION_JSON_TYPE).post(returnClass, bean);
    }

    /**
     * Deprecated: use {@link WebResource#post(Object)} instead.
     * <p/>
     * <b>This method will be removed in 5.2.</b>
     */
    protected void post(WebResource webResource, Object bean)
    {
        webResource.type(MediaType.APPLICATION_JSON_TYPE).post(bean);
    }

    /**
     * Deprecated: use {@link WebResource#delete()} instead.
     * <p/>
     * <b>This method will be removed in 5.2.</b>
     */
    protected void delete(WebResource webResource)
    {
        webResource.type(MediaType.APPLICATION_JSON_TYPE).delete();
    }

	/**
	 * Creates the resource that corresponds to the root of a REST API. Note that the created {@code WebResource} has
	 * the following properties: <ul> <li>it logs all GET/POST/etc requests made through it</li> <li>it sets the
	 * <code>Content-Type: application/json</code> by default (override with {@link
	 * WebResource#type(javax.ws.rs.core.MediaType)})</li> </ul>
	 *
	 * @param restModulePath a String containing the REST path
	 * @return a WebResource for the the API root at the specified path
	 * @see #getRestModulePath()
	 */
	protected final WebResource createResourceForPath(String restModulePath) {
		WebResource resource = resourceRoot(rootPath).path("rest").path(restModulePath).path("1.0");
		resource.addFilter(new BackdoorLoggingFilter());
		resource.addFilter(new JsonMediaTypeFilter());

		return resource;
	}

	/**
	 * Creates the resource that corresponds to the root of the TestKit REST API, using the values returned by {@link
	 * #getRestModulePath()}. Note that the created {@code WebResource} has the following properties: <ul> <li>it logs
	 * all GET/POST/etc requests made through it</li> <li>it sets the <code>Content-Type: application/json</code> by
	 * default (override with {@link WebResource#type(javax.ws.rs.core.MediaType)})</li> </ul>.
	 * <p/>
	 * To create a WebResource for a different root, use {@link #createResource}
	 *
	 * @return a WebResource for the TestKit REST API root
	 * @see #createResource
	 * @see #getRestModulePath()
	 */
	protected WebResource createResource() {
		return createResourceForPath(getRestModulePath());
	}

	/**
     * Returns the REST path used in this plugin's {@code atlassian-plugin.xml} (e.g. {@code &lt;rest path="..."&gt;}).
     * The default value is "{@value #DEFAULT_REST_PATH}".
     *
     * @return the REST path used in this plugin's {@code atlassian-plugin.xml} (e.g. {@code &lt;rest path="..."&gt;}).
     */
    protected String getRestModulePath()
    {
        return DEFAULT_REST_PATH;
    }

    /**
     * Logs all Backdoor requests using the FuncTestLogger.
     *
     * @see FuncTestLogger#log(Object)
     */
    protected class BackdoorLoggingFilter extends ClientFilter
    {
        @Override
        public ClientResponse handle(ClientRequest request) throws ClientHandlerException
        {
            FuncTestTimer timer = TestInformationKit.pullTimer("Backdoor Shenanigans");
            ClientResponse response = getNext().handle(request);
            logRequest(request, timer.end());

            return response;
        }

        private void logRequest(ClientRequest request, long howLong)
        {
            String relativePath = StringUtils.removeStart(request.getURI().getPath(), createResource().getURI().getPath());
            logger.log(String.format("Backdoor %-6s in %5dms  %s", request.getMethod(), howLong, relativePath));
        }
    }

    /**
     * Sets the {@code Content-Type} header to "{@value MediaType#APPLICATION_JSON}" if not already set.
     */
    protected static class JsonMediaTypeFilter extends ClientFilter
    {
        @Override
        public ClientResponse handle(ClientRequest request) throws ClientHandlerException
        {
            if (request.getEntity() != null && !request.getHeaders().containsKey("Content-Type"))
            {
                request.getHeaders().putSingle("Content-Type", MediaType.APPLICATION_JSON);
            }

            return getNext().handle(request);
        }
    }
}
