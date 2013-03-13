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

import javax.ws.rs.core.MediaType;

/**
 * Client for the site dark features resource.
 *
 * @since v5.2
 */
public class SiteDarkFeaturesClient extends RestApiClient<SiteDarkFeaturesClient>
{
    /**
     * Constructs a new SiteDarkFeaturesClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public SiteDarkFeaturesClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * PUTs the feature key and enablement
     *
     * @param featureKey feature key
     * @param enabled whether to enable or disable the dark feature
     * @throws com.sun.jersey.api.client.UniformInterfaceException if there's a problem enabling the dark feature
     */
    public void put(String featureKey, boolean enabled) throws UniformInterfaceException
    {
        siteDarkFeaturesWithKey(featureKey).type(MediaType.APPLICATION_JSON_TYPE).put(new DarkFeature(enabled));
    }

    /**
     * PUTs the featureKey and enablement, returning a Response object.
     *
     * @param featureKey feature key
     * @param enabled whether to enable or disable the dark feature
     * @return a Response
     */
    public Response putResponse(final String featureKey, final boolean enabled)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return siteDarkFeaturesWithKey(featureKey).type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, new DarkFeature(enabled));
            }
        });
    }

    /**
     * GETs the feature key
     *
     * @param featureKey feature key
     * @throws com.sun.jersey.api.client.UniformInterfaceException if there's a problem enabling the dark feature
     */
    public DarkFeature get(String featureKey) throws UniformInterfaceException
    {
        return siteDarkFeaturesWithKey(featureKey).get(DarkFeature.class);
    }

    /**
     * GETs the featureKey, returning a Response object.
     *
     * @param featureKey feature key
     * @return a Response
     */
    public Response getResponse(final String featureKey)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return siteDarkFeaturesWithKey(featureKey).get(ClientResponse.class);
            }
        });
    }

    /**
     * Returns a WebResource for site dark features.
     *
     * @return a WebResource
     */
    protected WebResource siteDarkFeatures()
    {
        return createResourceInternal().path("darkFeatures");
    }

    /**
     * Returns a WebResource for site dark features for the given key.
     *
     * @param featureKey feature key
     * @return a WebResource
     */
    protected WebResource siteDarkFeaturesWithKey(String featureKey)
    {
        return siteDarkFeatures().path(featureKey);
    }
}
