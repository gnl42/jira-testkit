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

import com.atlassian.jira.testkit.beans.Priority;
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
}
