/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Allows access
 *
 * @since v4.3
 */
@Path ("log")
public class LogAccess
{
    private static final Logger log = LoggerFactory.getLogger(LogAccess.class);

    /**
     * Causes an error log message to be placed in the JIRA log.
     * It's invoked using a URL like <code>/jira/rest/testkit-test/1.0/log/error?msg=hi</code>
     *
     * @param msg the message to go into the application log
     * @return nothing meaningful but GET requires it
     */
    @GET
    @AnonymousAllowed
    @Path ("error")
    public Response logMessage(@QueryParam ("msg") String msg)
    {
        log.error(msg);
        return Response.ok(null).build();
    }

    /**
     * Causes an info log message to be placed in the JIRA log.
     * It's invoked using a URL like <code>/jira/rest/testkit-test/1.0/log/info?msg=hi</code>
     *
     * @param msg the message to go into the application log
     * @return nothing meaningful but GET requires it
     */
    @GET
    @AnonymousAllowed
    @Path ("info")
    public Response logInfoMessage(@QueryParam ("msg") String msg)
    {
        log.info(msg);
        return Response.ok(null).build();
    }

}
