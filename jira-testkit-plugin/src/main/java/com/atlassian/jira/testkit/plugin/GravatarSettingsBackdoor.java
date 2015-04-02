/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.avatar.GravatarSettings;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces ({MediaType.APPLICATION_JSON})
@Consumes ({MediaType.APPLICATION_JSON})
@Path ("gravatarSettings")
public class GravatarSettingsBackdoor
{
    private final GravatarSettings gravatarSettings;

    public GravatarSettingsBackdoor(GravatarSettings gravatarSettings)
    {
        this.gravatarSettings = gravatarSettings;
    }

    @POST
    @AnonymousAllowed
    @Path("allowGravatars")
    public Response setAllowGravatars(boolean allow)
    {
        gravatarSettings.setAllowGravatars(allow);
        return Response.ok(null).build();
    }
}
