/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.jersey;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.List;

/**
 * Filter factory for automatically handling cache headers.
 *
 * @since v5.2
 */
@Provider
public class TestKitResourceFilterFactory implements ResourceFilterFactory
{
    private static final Logger log = LoggerFactory.getLogger(TestKitResourceFilterFactory.class);

    public TestKitResourceFilterFactory()
    {
        // empty
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod am)
    {
        return Collections.<ResourceFilter>singletonList(new ResourceFilter()
        {
            @Override
            public ContainerRequestFilter getRequestFilter()
            {
                return null;
            }

            @Override
            public ContainerResponseFilter getResponseFilter()
            {
                return new CacheControlResponseFilter();
            }
        });
    }

    /**
     * Disables caching unless there are already caching headers.
     */
    private static class CacheControlResponseFilter implements ContainerResponseFilter
    {
        @Override
        public ContainerResponse filter(ContainerRequest request, ContainerResponse response)
        {
            MultivaluedMap<String,Object> headers = response.getHttpHeaders();
            if (!headers.containsKey("Cache-Control") && !headers.containsKey("Expires"))
            {
                log.trace("Response does not have caching headers, adding 'Cache-Control: no-cache, no-store'");
                headers.putSingle("Cache-Control", "no-cache, no-store");
            }

            return response;
        }
    }
}
