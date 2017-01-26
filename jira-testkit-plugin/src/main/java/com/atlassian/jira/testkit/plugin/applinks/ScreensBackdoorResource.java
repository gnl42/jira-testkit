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
import com.google.common.base.Predicate;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

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

    private Function<FieldScreen, Screen> toScreen() {
        return new Function<FieldScreen, Screen>() {
            @Override
            public Screen apply(final FieldScreen input) {
                return new Screen(input.getId(), input.getName(), transform(input.getTabs(), convertToTab()));
            }
        };
    }

    private Function<FieldScreenTab, Screen.Tab> convertToTab() {
        return new Function<FieldScreenTab, Screen.Tab>()
        {
            @Override
            public Screen.Tab apply(final FieldScreenTab input)
            {
                Iterable<FieldScreenLayoutItem> filtered = filter(input.getFieldScreenLayoutItems(), new Predicate<FieldScreenLayoutItem>() {
                    @Override
                    public boolean apply(FieldScreenLayoutItem field) {
                        return fieldManager.getField(field.getFieldId()) != null;
                    }
                });

                Iterable<Field> fields = transform(filtered, new Function<FieldScreenLayoutItem, Field>() {
                    @Override
                    public Field apply(final FieldScreenLayoutItem input) {
                        com.atlassian.jira.issue.fields.Field field = fieldManager.getField(input.getFieldId());
                        return new Field(field.getId(), field.getName());
                    }
                });
                return new Screen.Tab(input.getId(), input.getName(), fields);
            }
        };
    }

    @GET
    public Response get(@QueryParam ("screen") String nameOrId)
    {
        if (nameOrId == null)
        {
            return ok(copyOf(transform(fieldScreenManager.getFieldScreens(), toScreen())));
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
                return ok(toScreen().apply(fieldScreen));
            }
        }
    }

    @GET
    @Path ("removeField")
    public Response removeFieldFromScreen(@QueryParam ("screen") String screen, @QueryParam ("field") String field)
    {
        OrderableField navigableField = getFieldByName(field);
        if (navigableField != null)
        {
            final FieldScreen fieldScreen = getScreenByName(screen);
            fieldScreen.removeFieldScreenLayoutItem(navigableField.getId());
        }
        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    @GET
    @Path ("addField")
    public Response addFieldToScreen(@QueryParam ("screen") String screen, @QueryParam ("tab") String tab, @QueryParam ("field") String field, @QueryParam ("position") String position)
    {
        FieldScreen fieldScreen = getScreenByName(screen);
        FieldScreenTab screenTab;
        if (StringUtils.isEmpty(tab))
        {
            screenTab = fieldScreen.getTab(0);
        } else
        {
            screenTab = getTab(tab, fieldScreen);
        }

        OrderableField navigableField = getFieldByName(field);
        if (navigableField != null)
        {
            if (StringUtils.isEmpty(position))
            {
                screenTab.addFieldScreenLayoutItem(navigableField.getId());
            } else
            {
                screenTab.addFieldScreenLayoutItem(navigableField.getId(), Integer.valueOf(position));
            }
            screenTab.store();
        }
        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    @GET
    @Path ("setFieldPosition")
    public Response changeFieldPosition(@QueryParam ("screen") String screen, @QueryParam ("field") String field, @QueryParam ("position") String position)
    {
        OrderableField navigableField = getFieldByName(field);
        if (navigableField != null)
        {
            FieldScreenLayoutItem layoutItem = getFieldScreenLayoutItem(getScreenByName(screen), navigableField.getId());
            layoutItem.getFieldScreenTab().moveFieldScreenLayoutItemToPosition(Collections.singletonMap(Integer.valueOf(position), layoutItem));
            fieldScreenManager.updateFieldScreenLayoutItem(layoutItem);
        }
        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    private OrderableField getFieldByName(String fieldName)
    {
        final Set<OrderableField> navigableFields = fieldManager.getOrderableFields();
        for (OrderableField navigableField : navigableFields)
        {
            if (navigableField.getName().equals(fieldName))
            {
                return navigableField;
            }
        }
        return null;
    }

    private FieldScreenLayoutItem getFieldScreenLayoutItem(FieldScreen screen, String fieldId)
    {
        for (FieldScreenTab fieldScreenTab : screen.getTabs())
        {
            FieldScreenLayoutItem fieldScreenLayoutItem = fieldScreenTab.getFieldScreenLayoutItem(fieldId);
            if (fieldScreenLayoutItem != null)
            {
                return fieldScreenLayoutItem;
            }
        }

        return null;
    }

    @GET
    @Path ("addTab")
    public Response addTab(@QueryParam ("screen") String screen, @QueryParam ("name") String name)
    {
        return ok(convertToTab().apply(getScreenByName(screen).addTab(name)));
    }

    @GET
    @Path ("deleteTab")
    public Response deleteTab(@QueryParam ("screen") String screen, @QueryParam ("name") String name)
    {
        final FieldScreen screenByName = getScreenByName(screen);
        FieldScreenTab tab = getTab(name, screenByName);
        if (tab != null)
        {
            tab.remove();
        }
        return Response.ok().cacheControl(CacheControl.never()).build();
    }

    private FieldScreenTab getTab(String name, final FieldScreen screen)
    {
        final List<FieldScreenTab> tabs = screen.getTabs();
        for (FieldScreenTab tab : tabs)
        {
            if (tab.getName().equals(name))
            {
                return tab;
            }
        }
        return null;
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