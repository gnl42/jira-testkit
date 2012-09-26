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
public abstract class BackdoorControl<T extends BackdoorControl<T>> extends RestApiClient<T> implements FuncTestLogger
{
    public static final String DEFAULT_REST_PATH = "testkit-test";

    protected String rootPath;
    private FuncTestLoggerImpl logger;

    public BackdoorControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
        this.rootPath = environmentData.getBaseUrl().toExternalForm();
        this.logger = new FuncTestLoggerImpl(2);
    }

    /**
     * Helper method for making easy GET calls. Feel free to add overloads to accept paths and parameter maps.
     *
     * @param webResource web resource
     * @return response string
     */
    protected String get(final WebResource webResource)
    {
        return webResource.get(String.class);
    }

    protected <T> T get(WebResource webResource, GenericType<T> returnType)
    {
        return webResource.get(returnType);
    }

    protected <T> T get(WebResource webResource, Class<T> returnClass)
    {
        return webResource.get(returnClass);
    }

    protected long getId(final WebResource webResource)
    {
        return Long.parseLong(get(webResource));
    }

    protected void post(WebResource webResource)
    {
        webResource.post();
    }

    protected <T> T post(WebResource webResource, Object bean, Class<T> returnClass)
    {
        return webResource.post(returnClass, bean);
    }

    protected <T> T put(WebResource webResource, Object bean, Class<T> returnClass)
    {
        return webResource.put(returnClass, bean);
    }

    protected void post(WebResource webResource, Object bean)
    {
        webResource.post(bean);
    }

    protected void delete(WebResource webResource)
    {
        webResource.delete();
    }

    /**
     * Creates the resource that corresponds to the root of the TestKit REST API. Note that the created
     * {@code WebResource} has the following properties:
     * <ul>
     *     <li>it logs all GET/POST/etc requests made through it</li>
     *     <li>it sets the <code>Content-Type: {@value MediaType#APPLICATION_JSON}</code> by default (override with {@link WebResource#type(javax.ws.rs.core.MediaType)})</li>
     * </ul>
     *
     * @return a WebResource for the TestKit REST API root
     */
    protected WebResource createResource()
    {
        WebResource resource = resourceRoot(rootPath).path("rest").path(getRestModulePath()).path("1.0");
        resource.addFilter(new BackdoorLoggingFilter());
        resource.addFilter(new JsonMediaTypeFilter());

        return resource;
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

    @Override
    public void log(Object logData)
    {
        logger.log(logData);
    }

    @Override
    public void log(Throwable t)
    {
        logger.log(t);
    }

    /**
     * Logs all Backdoor requests using the FuncTestLogger.
     *
     * @see BackdoorControl#log(Object)
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
            log(String.format("Backdoor %-6s in %5dms  %s", request.getMethod(), howLong, relativePath));
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
