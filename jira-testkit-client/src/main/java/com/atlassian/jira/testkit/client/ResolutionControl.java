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

import com.atlassian.jira.testkit.beans.Resolution;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

/**
 * Some helper methods for Statuses.
 *
 * See {@link com.atlassian.jira.testkit.plugin.ResolutionBackdoor} in jira-testkit-plugin for backend.
 *
 */
public class ResolutionControl extends BackdoorControl<ResolutionControl>
{
    private static final GenericType<List<Resolution>> LIST_GENERIC_TYPE = new GenericType<List<Resolution>>(){};

    public ResolutionControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public Resolution createResolution(String name, String description)
    {
        final Resolution resolution = new Resolution();
        resolution.setName(name);
        resolution.setDescription(description);
        
        return createResolutionResource().post(Resolution.class, resolution);
    }
    
    public Resolution updateResolution(long id, String name, String description)
    {
        final Resolution resolution = new Resolution();
        resolution.setId(String.valueOf(id));
        resolution.setName(name);
        resolution.setDescription(description);
        
        return createResolutionResource().put(Resolution.class, resolution);
    }


    public List<Resolution> getResolutions()
    {
        return createResolutionResource().get(LIST_GENERIC_TYPE);
    }

    public void deleteResolution(long id)
    {
        createResolutionResource().path(valueOf(id)).delete();
    }
    
    public void setDefaultResolution(long id)
    {
        createResolutionResource().path(valueOf(id)).path("default").post();
    }
    
    public void moveResolutionUp(long id)
    {
        createResolutionResource().path(valueOf(id)).path("up").post();
    }
    
    public void moveResolutionDown(long id)
    {
        createResolutionResource().path(valueOf(id)).path("down").post();
    }

    private WebResource createResolutionResource()
    {
        return createResource().path("resolution");
    }
}
