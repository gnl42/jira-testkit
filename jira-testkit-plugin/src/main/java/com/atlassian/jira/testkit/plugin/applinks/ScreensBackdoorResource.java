package com.atlassian.jira.testkit.plugin.applinks;

import com.atlassian.jira.action.screen.AddFieldToScreenUtil;
import com.atlassian.jira.action.screen.AddFieldToScreenUtilImpl;
import com.atlassian.jira.testkit.plugin.util.CacheControl;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.OrderableField;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenTab;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.ErrorCollection;
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
    private final AddFieldToScreenUtil addFieldToScreenUtil;
    private final FieldManager fieldManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;

    public ScreensBackdoorResource(FieldScreenManager fieldScreenManager, AddFieldToScreenUtil addFieldToScreenUtil, FieldManager fieldManager, JiraAuthenticationContext jiraAuthenticationContext)
    {
        this.fieldScreenManager = fieldScreenManager;
        this.addFieldToScreenUtil = addFieldToScreenUtil;
        this.fieldManager = fieldManager;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

    @GET
    @Path ("addField")
    public Response addFieldToScreen(@QueryParam ("screen") String screen, @QueryParam ("tab") String tabName, @QueryParam ("field") String field, @QueryParam ("position") String position)
    {
        final AddFieldToScreenUtil addFieldToScreenUtil = new AddFieldToScreenUtilImpl(jiraAuthenticationContext, fieldManager, fieldScreenManager);
        final Set<OrderableField> navigableFields = fieldManager.getOrderableFields();
        for (OrderableField navigableField : navigableFields)
        {
            if (navigableField.getName().equals(field))
            {
                final FieldScreen fieldScreen = getScreenByName(screen);
                FieldScreenTab tab = null;

                if (tabName != null)
                {
                    final List<FieldScreenTab> tabs = fieldScreen.getTabs();
                    for (FieldScreenTab fieldScreenTab : tabs)
                    {
                        if (fieldScreenTab.getName().equals(tabName))
                        {
                            tab = fieldScreenTab;
                        }
                    }
                }

                if (tab == null)
                {
                    tab = fieldScreen.getTab(0);
                }

                addFieldToScreenUtil.setFieldScreenId(fieldScreen.getId());
                addFieldToScreenUtil.setTabPosition(tab.getPosition());
                addFieldToScreenUtil.setFieldId(new String[] { navigableField.getId() });
                position = position == null ?  "" + (tab.getFieldScreenLayoutItems().size() + 1) : position;
                addFieldToScreenUtil.setFieldPosition(position);
                final ErrorCollection errorCollection = addFieldToScreenUtil.validate();
                if (!errorCollection.hasAnyErrors())
                {
                    addFieldToScreenUtil.execute();
                }
                break;
            }
        }

        return Response.ok().cacheControl(CacheControl.never()).build();
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
