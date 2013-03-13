/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;

/**
 * Are you allowed to enable or disable subtasks.
 *
 * @since v5.0.1
 */
@Path ("subtask")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class SubtaskBackdoorResource
{
    private final SubTaskManager subTaskManager;

    public SubtaskBackdoorResource(SubTaskManager subTaskManager)
    {
        this.subTaskManager = subTaskManager;
    }
    
    @GET
    public Response get()
    {
        return Response.ok(subTaskManager.isSubTasksEnabled()).cacheControl(never()).build(); 
    }

    @POST
    public Response set(Boolean enabled)
    {
        if (enabled != subTaskManager.isSubTasksEnabled())
        {
            if (enabled)
            {
                try
                {
                    subTaskManager.enableSubTasks();
                }
                catch (CreateException e)
                {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                subTaskManager.disableSubTasks();
            }
        }
        return Response.ok(subTaskManager.isSubTasksEnabled()).cacheControl(never()).build();
    }
}
