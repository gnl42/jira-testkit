package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.beans.PermissionSchemeBean;
import com.atlassian.jira.testkit.beans.PermissionGrantBean;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.core.MediaType;

@ParametersAreNonnullByDefault
public final class PermissionSchemeRestClient extends RestApiClient<PermissionSchemeRestClient>
{
    public PermissionSchemeRestClient(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public Response<List<PermissionSchemeBean>> getSchemes()
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return resource().get(ClientResponse.class);
            }
        }, new GenericType<List<PermissionSchemeBean>>() {});
    }

    public Response<PermissionSchemeBean> createScheme(final PermissionSchemeBean bean)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return resource().type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, bean);
            }
        }, PermissionSchemeBean.class);
    }

    public Response<PermissionSchemeBean> getScheme(final Long id)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return resource().path(id.toString()).get(ClientResponse.class);
            }
        }, PermissionSchemeBean.class);
    }

    public Response<PermissionSchemeBean> updateScheme(final Long id, final PermissionSchemeBean updateBean)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return resource()
                        .path(id.toString())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .put(ClientResponse.class, updateBean);
            }
        }, PermissionSchemeBean.class);
    }

    public Response<?> deleteScheme(final Long id)
    {
        return toResponse(new Method() {
            @Override
            public ClientResponse call()
            {
                return resource().path(id.toString()).delete(ClientResponse.class);
            }
        });
    }

    public Response<List<PermissionGrantBean>> getPermissions(final Long schemeId)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return resource().path(schemeId.toString()).path("permission").get(ClientResponse.class);
            }
        }, new GenericType<List<PermissionGrantBean>>() {});
    }

    public Response<PermissionGrantBean> createPermission(final Long schemeId, final PermissionGrantBean bean)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return resource()
                        .path(schemeId.toString())
                        .path("permission")
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .post(ClientResponse.class, bean);
            }
        }, PermissionGrantBean.class);
    }

    public Response<PermissionGrantBean> getPermission(final Long schemeId, final Long permissionId)
    {
        return toResponse(new Method() {
            @Override
            public ClientResponse call()
            {
                return resource()
                        .path(schemeId.toString())
                        .path("permission")
                        .path(permissionId.toString())
                        .get(ClientResponse.class);
            }
        }, PermissionGrantBean.class);
    }

    public Response<?> deletePermission(final Long schemeId, final Long permissionId)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return resource()
                        .path(schemeId.toString())
                        .path("permission")
                        .path(permissionId.toString())
                        .delete(ClientResponse.class);

            }
        });
    }

    public Response<PermissionSchemeBean> getAssignedScheme(final String projectKeyOrId)
    {
        return toResponse(new Method() {
            @Override
            public ClientResponse call()
            {
                return projectResource(projectKeyOrId).get(ClientResponse.class);
            }
        }, PermissionSchemeBean.class);
    }

    public Response<PermissionSchemeBean> assignScheme(final String projectKeyOrId, final Long schemeId)
    {
        return toResponse(new Method() {
            @Override
            public ClientResponse call()
            {
                return projectResource(projectKeyOrId).type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, new PermissionSchemeBean().setId(schemeId));
            }
        }, PermissionSchemeBean.class);
    }

    private WebResource resource()
    {
        return createResource().path("permissionscheme");
    }

    private WebResource projectResource(String projectKeyOrId)
    {
        return createResource().path("project").path(projectKeyOrId).path("permissionscheme");
    }

}
