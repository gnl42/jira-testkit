/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.jira.service.JiraServiceContainer;
import com.atlassian.jira.service.ServiceManager;
import com.atlassian.jira.testkit.plugin.service.ServiceManagerAdapterFactory;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Use this backdoor to manipulate Services as part of setup for tests.
 * <p/>
 * This class should only be called by the {@link com.atlassian.jira.testkit.client.ServicesControl}.
 *
 * @since v5.0
 */
@Path ("services")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class ServicesBackdoor
{
    private final ServiceManager serviceManager;
    private final ServiceManagerAdapterFactory serviceManagerAdapterFactory;

    public ServicesBackdoor(ServiceManager serviceManager, ServiceManagerAdapterFactory serviceManagerAdapterFactory)
    {
        this.serviceManager = serviceManager;
        this.serviceManagerAdapterFactory = serviceManagerAdapterFactory;
    }

    @GET
    public Response getServices() throws Exception
    {
        final List<ServiceBean> services = Lists.newArrayList();
        for (JiraServiceContainer serviceContainer : serviceManager.getServices())
        {
            services.add(new ServiceBean(serviceContainer));
        }
        return Response.ok(services).build();
    }

    @GET
    @Path ("/{id}")
    public Response getService(@PathParam ("id") Long id) throws Exception
    {
        final JiraServiceContainer container = serviceManager.getServiceWithId(id);
        return container == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(new ServiceBean(container)).build();
    }

    @POST
    @Path ("/{id}/run")
    public void runService(@PathParam ("id") Long id) throws Exception
    {
        if (serviceManagerAdapterFactory.isAvailable())
        {
            serviceManagerAdapterFactory.create().runNow(id);
        }
    }

    public static class ServiceBean
    {
        public Long id;
        public String name;
        public String serviceClass;
        public boolean usable;
        public Map<String, String> params;

        public ServiceBean()
        {
        }

        public ServiceBean(JiraServiceContainer serviceContainer)
        {
            id = serviceContainer.getId();
            name = serviceContainer.getName();
            serviceClass = serviceContainer.getServiceClass();
            usable = serviceContainer.isUsable();
            try
            {
                if (usable && serviceContainer.getProperties() != null)
                {
                    params = Maps.newHashMap();
                    for (Object o : serviceContainer.getProperties().getKeys())
                    {
                        String key = (String) o;
                        params.put(key, serviceContainer.getProperty(key));
                    }
                }
            }
            catch (ObjectConfigurationException e)
            {
                // ignore
            }
        }
    }
}
