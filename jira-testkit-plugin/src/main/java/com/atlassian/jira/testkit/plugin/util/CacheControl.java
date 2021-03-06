/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin.util;

/**
 * @since v5.0.1
 */
public class CacheControl
{
    private static final int ONE_YEAR = 60 * 60 * 24 * 365;

    /**
     * Returns a CacheControl with noStore and noCache set to true.
     *
     * @return a CacheControl
     */
    public static javax.ws.rs.core.CacheControl never()
    {
        javax.ws.rs.core.CacheControl cacheNever = new javax.ws.rs.core.CacheControl();
        cacheNever.setNoStore(true);
        cacheNever.setNoCache(true);

        return cacheNever;
    }

    /**
     * Returns a CacheControl with a 1 year limit. Effectively forever.
     *
     * @return a CacheControl
     */
    public static javax.ws.rs.core.CacheControl forever()
    {
        javax.ws.rs.core.CacheControl cacheForever = new javax.ws.rs.core.CacheControl();
        cacheForever.setPrivate(false);
        cacheForever.setMaxAge(ONE_YEAR);

        return cacheForever;
    }

    // prevent instantiation
    private CacheControl()
    {
        // empty
    }
}
