/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * Adds the X-Atlassian-Tenant header to all requests
 *
 * @since v4.3
 */
public class AtlassianTenantFilter extends ClientFilter
{
    private final String tenant;

    public AtlassianTenantFilter(String tenant)
    {
        this.tenant = tenant;
    }

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException
    {
        if (!cr.getMetadata().containsKey("X-Atlassian-Tenant"))
        {
            cr.getMetadata().add("X-Atlassian-Tenant", tenant);
        }
        return getNext().handle(cr);
    }
}
