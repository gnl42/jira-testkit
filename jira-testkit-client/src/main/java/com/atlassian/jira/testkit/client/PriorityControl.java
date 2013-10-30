/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import static java.lang.String.valueOf;

import java.util.List;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

/**
 * Some helper methods for Statuses.
 *
 * See {@link com.atlassian.jira.testkit.plugin.PriorityBackdoor} in jira-testkit-plugin for backend.
 *
 */
public class PriorityControl extends BackdoorControl<PriorityControl>
{
    private static final GenericType<List<Priority>> LIST_GENERIC_TYPE = new GenericType<List<Priority>>(){};

    public PriorityControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public Priority createPriority(String name, String description, String color, String iconUrl)
    {
        final Priority priority = new Priority();
        priority.setName(name);
        priority.setDescription(description);
        priority.setColor(color);
        priority.setIconUrl(iconUrl);
        
        return createPriorityResource().post(Priority.class, priority);
    }
    
    public Priority editPriority(long id, String name, String description, String color, String iconUrl)
    {
        final Priority priority = new Priority();
        priority.setId(String.valueOf(id));        
        priority.setName(name);
        priority.setDescription(description);
        priority.setColor(color);
        priority.setIconUrl(iconUrl);
        
        return createPriorityResource().put(Priority.class, priority);
    }


    public List<Priority> getPriorities()
    {
        return createPriorityResource().get(LIST_GENERIC_TYPE);
    }

    public void deletePriority(long id)
    {
        createPriorityResource().path(valueOf(id)).delete();
    }
    
    public void setDefaultPriority(long id)
    {
        createPriorityResource().path(valueOf(id)).path("default").post();
    }
    
    public void movePriorityUp(long id)
    {
        createPriorityResource().path(valueOf(id)).path("up").post();
    }
    
    public void movePriorityDown(long id)
    {
        createPriorityResource().path(valueOf(id)).path("down").post();
    }

    private WebResource createPriorityResource()
    {
        return createResource().path("priority");
    }

    public static class Priority
    {
        private String id;
        private String description;
        private String name;
        private String iconUrl;
        private String color;
        private Long sequence;

        public Priority()
        {
        }

        public Priority(String id, String name, String description)
        {
            this.id = id;
            this.description = description;
            this.name = name;
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public Long getSequence() {
            return sequence;
        }

        public void setSequence(Long sequence)
        {
            this.sequence = sequence;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color)
        {
            this.color = color;
        }

        public String getIconUrl()
        {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl)
        {
            this.iconUrl = iconUrl;
        }
    }
}
