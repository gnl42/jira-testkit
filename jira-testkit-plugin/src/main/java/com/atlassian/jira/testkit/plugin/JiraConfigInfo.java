/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Provides information about JIRA instance configuration.
 *
 * @since 4.4
 */
@Path ("config-info")
@Produces ({ MediaType.APPLICATION_JSON })
public class JiraConfigInfo
{
    private final JiraHome jiraHome;
    private final ApplicationProperties applicationProperties;

    public JiraConfigInfo(JiraHome jiraHome, ApplicationProperties applicationProperties)
    {
        this.jiraHome = checkNotNull(jiraHome);
        this.applicationProperties = checkNotNull(applicationProperties);
    }

    @GET
    @AnonymousAllowed
    public Response getConfigInfo()
    {
        return Response.ok(new ConfigInfoBean(jiraHome, applicationProperties)).build();
    }

    @XmlRootElement
    public static class ConfigInfoBean
    {
        @XmlElement
        private String jiraHomePath;

        @XmlElement
        private boolean isSetUp;

        public ConfigInfoBean() {}

        public ConfigInfoBean(JiraHome home, ApplicationProperties props)
        {
            this.jiraHomePath = home.getHomePath();
            this.isSetUp = props.getOption(APKeys.JIRA_SETUP);
        }
    }

}
