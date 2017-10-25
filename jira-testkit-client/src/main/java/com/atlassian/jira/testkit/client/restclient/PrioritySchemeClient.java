package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;

public class PrioritySchemeClient extends RestApiClient<PrioritySchemeClient> {

    public PrioritySchemeClient(JIRAEnvironmentData environmentData) {
        super(environmentData);
    }

    public Response<PrioritySchemeBean> createWithDefaultMapping(PrioritySchemeUpdateBean bean) {
        return toResponse(
                () -> resource()
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .post(ClientResponse.class, bean),
                PrioritySchemeBean.class
        );
    }

    public Response<PrioritySchemeBean> updateWithDefaulyMapping(PrioritySchemeUpdateBean bean) {
        return toResponse(
                () -> schemeResource(bean.getId())
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .put(ClientResponse.class, bean),
                PrioritySchemeBean.class
        );
    }

    public Response delete(long schemeId) {
        return toResponse(() -> schemeResource(schemeId).delete(ClientResponse.class));
    }

    public Response<PrioritySchemeBean> get(long schemeId) {
        return toResponse(
                () -> schemeResource(schemeId).get(ClientResponse.class),
                PrioritySchemeBean.class
        );
    }

    public Response<PrioritySchemeGetAllResponseBean> getAll(boolean includeProjectKeys) {
        return toResponse(
                () -> resource().queryParam("expand", includeProjectKeys ? "projectKeys" : "").get(ClientResponse.class),
                PrioritySchemeGetAllResponseBean.class
        );
    }

    public Response<PrioritySchemeBean> assign(long schemeId, String projectKey) {
        return toResponse(
                () -> schemeResource(schemeId).path(projectKey).put(ClientResponse.class),
                PrioritySchemeBean.class
        );
    }

    public Response<PrioritySchemeBean> unassign(long schemeId, String projectKey) {
        return toResponse(
                () -> schemeResource(schemeId).path(projectKey).delete(ClientResponse.class),
                PrioritySchemeBean.class
        );
    }

    public Response<PrioritySchemeBean> getForProject(String projectKey) {
        return toResponse(
                () -> resource().path("project").path(projectKey).get(ClientResponse.class),
                PrioritySchemeBean.class
        );
    }

    private WebResource resource() {
        return createResource().path("priorityschemes");
    }

    private WebResource schemeResource(long schemeId) {
        return resource().path(Long.toString(schemeId));
    }
}