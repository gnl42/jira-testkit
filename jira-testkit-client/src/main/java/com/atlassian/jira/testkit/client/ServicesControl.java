/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.GenericType;

import java.util.List;
import java.util.Map;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate or query Services.
 *
 * See <code>com.atlassian.jira.testkit.plugin.ServiceBackdoor</code> in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class ServicesControl extends BackdoorControl<ServicesControl>
{
    public ServicesControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public List<ServiceBean> getServices()
    {
        return createResource().path("services").get(new GenericType<List<ServiceBean>>()
        {
        });
    }

    public ServiceBean getService(long id)
    {
        return createResource().path("services/" + id).get(ServiceBean.class);
    }

    public void runService(long id)
    {
        createResource().path("services/" + id + "/run").post();
    }

    public static class ServiceBean
    {
        public Long id;
        public String name;
        public String serviceClass;
        public boolean usable;
        public Map<String, String> params;
    }
}
