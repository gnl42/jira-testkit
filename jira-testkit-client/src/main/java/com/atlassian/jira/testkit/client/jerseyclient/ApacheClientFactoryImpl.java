/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.jerseyclient;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.ApacheHttpClientHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

/**
 * Factory for Jersey clients that use Apache HttpClient.
 *
 * @since v4.3
 */
public class ApacheClientFactoryImpl implements JerseyClientFactory
{
    /**
     * The configuration used for creating the Jersey client.
     */
    private final ClientConfig config;

    /**
     * Creates a ClientFactory with the default configuration, which uses Jackson as the JSON marshaller.
     */
    public ApacheClientFactoryImpl()
    {
        this(new DefaultClientConfig());
        config.getClasses().add(JacksonJsonProvider.class);
    }

    /**
     * Creates a ClientFactory with the provided configuration.
     *
     * @param config a ClientConfig
     */
    public ApacheClientFactoryImpl(ClientConfig config)
    {
        this.config = config;
    }

    /**
     * Creates a Jersey client.
     *
     * @return a new Client instance
     */
    @Override
    public Client create()
    {
        final MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(20);
        connectionManager.getParams().setMaxTotalConnections(100);

        final HttpClient httpClient = new HttpClient(connectionManager);
        final ApacheHttpClientHandler clientHandler = new ApacheHttpClientHandler(httpClient, config);

        return new ApacheHttpClient(clientHandler);
    }
}
