/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.jira.config.properties.JiraSystemProperties;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Gets or sets system properties with GET or POST. Always returns latest value for named property.
 *
 * @since v5.0
 */
@AnonymousAllowed
@Path ("systemproperty")
public class SystemPropertyBackdoor
{
    private static final Logger log = LoggerFactory.getLogger(SystemPropertyBackdoor.class);

    @GET
    @Path ("{name}")
    public String get(@PathParam ("name") final String propertyName)
    {
        validateNotBlank(propertyName);
        return System.getProperty(propertyName);
    }

    @POST
    @Path ("{name}")
    @XsrfProtectionExcluded // Only available during testing.
    public String set(@PathParam ("name") final String propertyName, @QueryParam ("value") String propertyValue)
    {
        validateNotBlank(propertyName);

        if (propertyValue != null)
        {
            log.info("Setting system property '{}' to: {}", propertyName, propertyValue);
            System.setProperty(propertyName, propertyValue);
            JiraSystemProperties.resetReferences();
        }
        else
        {
            log.warn("cannot set null property: '" + propertyName + "=" + propertyValue + "'");
        }

        return get(propertyName);
    }

    @DELETE
    @Path ("{name}")
    public void unset(@PathParam ("name") String propertyName)
    {
        validateNotBlank(propertyName);

        log.info("Unsetting system property '{}'", propertyName);
        System.getProperties().remove(propertyName);
        JiraSystemProperties.resetReferences();
    }

    private void validateNotBlank(String propertyName)
    {
        if (isBlank(propertyName))
        {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Property name must not be blank").build());
        }
    }
}
