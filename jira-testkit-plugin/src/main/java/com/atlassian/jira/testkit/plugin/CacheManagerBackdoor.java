/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.cache.CacheManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Backdoor for hacking caches.
 *
 * @since v7.0
 */

@Path ("caches")
public class CacheManagerBackdoor
{

    private final CacheManager cacheManager;

    public CacheManagerBackdoor(final CacheManager cacheManager)
    {
        this.cacheManager = cacheManager;
    }

    @DELETE
    @AnonymousAllowed
    @Path ("/")
    public Response flushCaches()
    {
        cacheManager.flushCaches();
        return Response.ok().build();
    }


}
