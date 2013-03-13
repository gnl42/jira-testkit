/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin.applinks;

import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.OrderableField;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenTab;
import com.atlassian.jira.testkit.plugin.util.CacheControl;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Path ("screens")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class ScreensBackdoorResource
{
    private final FieldScreenManager fieldScreenManager;
    private final FieldManager fieldManager;

    public ScreensBackdoorResource(FieldScreenManager fieldScreenManager, FieldManager fieldManager)
    {
        this.fieldScreenManager = fieldScreenManager;
        this.fieldManager = fieldManager;
    }

    @GET
    @Path ("removeField")
    public Response removeFieldFromScreen(@QueryParam ("screen") String screen, @QueryParam ("field") String field)
    {
        final Set<OrderableField> navigableFields = fieldManager.getOrderableFields();
        for (OrderableField navigableField : navigableFields)
        {
            if (navigableField.getName().equals(field))
            {
                final FieldScreen fieldScreen = getScreenByName(screen);
                fieldScreen.removeFieldScreenLayoutItem(navigableField.getId());
            }
        }
        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    @GET
    @Path ("addTab")
    public Response addTab(@QueryParam ("screen") String screen, @QueryParam ("name") String name)
    {
        getScreenByName(screen).addTab(name);
        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    @GET
    @Path ("deleteTab")
    public Response deleteTab(@QueryParam ("screen") String screen, @QueryParam ("name") String name)
    {
        final FieldScreen screenByName = getScreenByName(screen);
        final List<FieldScreenTab> tabs = screenByName.getTabs();
        for (FieldScreenTab tab : tabs)
        {
            if (tab.getName().equals(name))
            {
                tab.remove();
            }
        }
        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    public FieldScreen getScreenByName(String name)
    {
        final Collection<FieldScreen> fieldScreens = fieldScreenManager.getFieldScreens();
        for (FieldScreen fieldScreen : fieldScreens)
        {
            if (fieldScreen.getName().equals(name))
            {
                return fieldScreen;
            }
        }

        return fieldScreenManager.getFieldScreen(Long.parseLong(name, 10));
    }

}
