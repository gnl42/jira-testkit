package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;
import java.util.List;

public class ScreensClient extends RestApiClient<ScreensClient>
{
    private final Long id;

    public ScreensClient(JIRAEnvironmentData environmentData, Long id)
    {
        super(environmentData);
        this.id = id;
    }

    public List<ScreenTab> getAllTabs()
    {
        return getTabsResource().get(ScreenTab.LIST);
    }


    public Response getAllTabsResponse()
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return getTabsResource().get(ClientResponse.class);
            }
        });
    }

    public ScreenTab createTab(String name)
    {
        final ScreenTab screenTab = new ScreenTab();
        screenTab.name = name;
        return getTabsResource()
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ScreenTab.class, screenTab);
    }

    public Response createTabWithResponse(String name)
    {
        final ScreenTab screenTab = new ScreenTab();
        screenTab.name = name;
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return getTabsResource()
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .post(ClientResponse.class, screenTab);
            }
        });
    }


    public void deleteTab(Long id)
    {
        getTabsResource()
                .path("" + id)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .delete();
    }

    public Response deleteTabWithResponse(final Long id)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return getTabsResource()
                        .path("" + id)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .delete(ClientResponse.class);
            }
        });
    }

    public ScreenTab renameTab(Long id, String name)
    {
        final ScreenTab screenTab = new ScreenTab();
        screenTab.name = name;
        return getTabsResource()
                .path("" + id)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(ScreenTab.class, screenTab);
    }

    public Response renameTabWithResponse(final Long id, final String name)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                final ScreenTab screenTab = new ScreenTab();
                screenTab.name = name;
                return getTabsResource()
                        .path("" + id)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .put(ClientResponse.class, screenTab);
            }
        });
    }

    public void moveTab(Long id, Integer pos)
    {
        getTabsResource()
                .path("" + id)
                .path("move")
                .path("" + pos)
                .post();
    }

    public Response moveTabWithResponse(final Long id, final Integer pos)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return getTabsResource()
                        .path("" + id)
                        .path("move")
                        .path("" + pos)
                        .post(ClientResponse.class);
            }
        });
    }

    public ScreenField addField(Long tab, String field)
    {
        final AddField screenField = new AddField();
        screenField.fieldId = field;
        return getTabsResource()
                .path("" + tab)
                .path("fields")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ScreenField.class, screenField);
    }

    public Response addFieldWithResponse(final Long tab, final String field)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                final AddField screenField = new AddField();
                screenField.fieldId = field;
                return getTabsResource()
                        .path("" + tab)
                        .path("fields")
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .post(ClientResponse.class, screenField);
            }
        });
    }

    public void moveField(Long tab, String fieldId, MoveField moveField)
    {
        getTabsResource()
                .path("" + tab)
                .path("fields")
                .path(fieldId)
                .path("move")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(moveField);
    }

    public Response moveFieldWithResponse(final Long tab, final String fieldId, final MoveField moveField)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return getTabsResource()
                        .path("" + tab)
                        .path("fields")
                        .path(fieldId)
                        .path("move")
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .post(ClientResponse.class, moveField);
            }
        });
    }

    public void removeField(Long tab, String field)
    {
        getTabsResource()
                .path("" + tab)
                .path("fields")
                .path(field).delete();
    }

    public Response removeFieldWithResponse(final Long tab, final String field)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return getTabsResource()
                        .path("" + tab)
                        .path("fields")
                        .path(field).delete(ClientResponse.class);
            }
        });
    }

    public List<ScreenField> getAvailableFields()
    {
        return screen()
                .path("availableFields")
                .get(ScreenField.LIST);
    }

    public List<ScreenField> getFields(Long tab)
    {
        return getTabsResource()
                .path("" + tab)
                .path("fields")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .get(ScreenField.LIST);
    }

    /**
     * Returns a WebResource for priorities.
     *
     * @return a WebResource
     */

    protected WebResource screen()
    {
        return createResource().path("screens").path("" + this.id);
    }

    private WebResource getTabsResource() {return screen().path("tabs");}
}
