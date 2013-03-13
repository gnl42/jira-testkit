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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import java.util.List;

/**
 * See {@link com.atlassian.jira.testkit.plugin.DashboardBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class DashboardControl extends BackdoorControl<DashboardControl>
{
    private static final GenericType<List<Dashboard>> DASHBOARD_LIST_GENERIC_TYPE = new GenericType<List<Dashboard>>(){};

    public DashboardControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }
    
    public List<Dashboard> getOwnedDashboard(String username)
    {
        return createResource().path("dashboard").path("my").queryParam("username", username).get(DASHBOARD_LIST_GENERIC_TYPE);
    }

	public DashboardControl emptySystemDashboard() {
		createResource().path("dashboard").path("emptySystemDashboard").get(String.class);
		return this;
	}

    @JsonAutoDetect
    public static class Dashboard
    {
        private long id;
        private String name;
        private String owner;
        private String description;
        private long favouriteCount;
        private boolean favourite;

        public Dashboard()
        {
        }

        public Long getId()
        {
            return id;
        }

        public Dashboard setId(Long id)
        {
            this.id = id;
            return this;
        }

        public String getName()
        {
            return name;
        }

        public Dashboard setName(String name)
        {
            this.name = name;
            return this;
        }

        public String getOwner()
        {
            return owner;
        }

        public Dashboard setOwner(String owner)
        {
            this.owner = owner;
            return this;
        }

        public String getDescription()
        {
            return description;
        }

        public Dashboard setDescription(String description)
        {
            this.description = description;
            return this;
        }

        public long getFavouriteCount()
        {
            return favouriteCount;
        }

        public Dashboard setFavouriteCount(long favouriteCount)
        {
            this.favouriteCount = favouriteCount;
            return this;
        }

        public boolean isFavourite()
        {
            return favourite;
        }

        public Dashboard setFavourite(boolean favourite)
        {
            this.favourite = favourite;
            return this;
        }

        @Override
        public String toString()
        {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
