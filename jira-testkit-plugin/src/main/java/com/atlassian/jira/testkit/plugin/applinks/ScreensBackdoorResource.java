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
import com.atlassian.jira.issue.fields.screen.FieldScreenLayoutItem;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenTab;
import com.atlassian.jira.testkit.beans.Field;
import com.atlassian.jira.testkit.beans.Screen;
import com.atlassian.jira.testkit.plugin.util.CacheControl;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Function;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.transform;

@Path ("screens")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class ScreensBackdoorResource
{
    private static final Function<FieldScreen, Screen> toScreen = new Function<FieldScreen, Screen>()
    {
        @Override
        public Screen apply(final FieldScreen input)
        {
            return new Screen(input.getId(), input.getName(), transform(input.getTabs(), toTab));
        }
    };

    public static final Function<FieldScreenTab, Screen.Tab> toTab = new Function<FieldScreenTab, Screen.Tab>()
    {
        @Override
        public Screen.Tab apply(final FieldScreenTab input)
        {
            return new Screen.Tab(input.getName(), transform(input.getFieldScreenLayoutItems(), new Function<FieldScreenLayoutItem, Field>()
            {
                @Override
                public Field apply(final FieldScreenLayoutItem input)
                {
                    return new Field(input.getFieldId(), input.getOrderableField().getName());
                }
            }));
        }
    };

    private final FieldScreenManager fieldScreenManager;
    private final FieldManager fieldManager;

    public ScreensBackdoorResource(FieldScreenManager fieldScreenManager, FieldManager fieldManager)
    {
        this.fieldScreenManager = fieldScreenManager;
        this.fieldManager = fieldManager;
    }

    @GET
    public Response get(@QueryParam("screen") String nameOrId)
    {
        if (nameOrId == null)
        {
            return ok(copyOf(transform(fieldScreenManager.getFieldScreens(), toScreen)));
        }
        else
        {
            final FieldScreen fieldScreen = getScreenByName(nameOrId);
            if (fieldScreen == null)
            {
                return Response.status(Response.Status.NOT_FOUND)
                        .cacheControl(CacheControl.never())
                        .build();
            }
            else
            {
                return ok(toScreen.apply(fieldScreen));
            }
        }
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

        return fieldScreenManager.getFieldScreen(Long.parseLong(name));
    }

    private static Response ok(final Object body)
    {
        return Response.ok()
                .cacheControl(CacheControl.never())
                .entity(body)
                .build();
    }
}
