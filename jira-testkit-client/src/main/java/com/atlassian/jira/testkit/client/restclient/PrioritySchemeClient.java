package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;
import java.util.Objects;

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

    public Response<PrioritySchemeBean> updateWithDefaultMapping(PrioritySchemeUpdateBean bean, PrioritySchemeBean.Expand... expand) {
        return toResponse(
                () -> schemeResource(bean.getId(), expand)
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .put(ClientResponse.class, bean),
                PrioritySchemeBean.class
        );
    }

    public Response delete(long schemeId) {
        return toResponse(() -> schemeResource(schemeId).delete(ClientResponse.class));
    }

    public Response<PrioritySchemeBean> get(long schemeId, PrioritySchemeBean.Expand... expand) {
        return toResponse(
                () -> schemeResource(schemeId, expand).get(ClientResponse.class),
                PrioritySchemeBean.class
        );
    }

    public Response<PrioritySchemeGetAllResponseBean> getAll(final Long startAt, final Integer maxResults, PrioritySchemeGetAllResponseBean.Expand... expand) {
        return toResponse(
                () -> {
                    WebResource webResource = expandedResource(expand);
                    if (Objects.nonNull(startAt)) {
                        webResource = webResource.queryParam("startAt", startAt.toString());
                    }
                    if (Objects.nonNull(maxResults)) {
                        webResource = webResource.queryParam("maxResults", maxResults.toString());
                    }
                    return webResource.get(ClientResponse.class);
                },
                PrioritySchemeGetAllResponseBean.class
        );
    }

    public Response<PrioritySchemeBean> assign(long schemeId, String projectKey, PrioritySchemeBean.Expand... expand) {
        return toResponse(
                () -> expanded(projectSchemeResource(projectKey), setOf(PrioritySchemeBean.Expand.class, expand))
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .put(ClientResponse.class, ImmutableMap.of("id", schemeId)),
                PrioritySchemeBean.class
        );
    }

    public Response<PrioritySchemeBean> unassign(long schemeId, String projectKey, PrioritySchemeBean.Expand... expand) {
        return toResponse(
                () -> expanded(projectSchemeResource(projectKey).path(Long.toString(schemeId)), setOf(PrioritySchemeBean.Expand.class, expand)).delete(ClientResponse.class),
                PrioritySchemeBean.class
        );
    }

    public Response<PrioritySchemeBean> getForProject(String projectKey, PrioritySchemeBean.Expand... expand) {
        return toResponse(
                () -> expanded(projectSchemeResource(projectKey), setOf(PrioritySchemeBean.Expand.class, expand)).get(ClientResponse.class),
                PrioritySchemeBean.class
        );
    }

    private WebResource expandedResource(PrioritySchemeBean.Expand... expand) {
        return expanded(resource(), setOf(PrioritySchemeBean.Expand.class, expand));
    }

    private WebResource expandedResource(PrioritySchemeGetAllResponseBean.Expand... expand) {
        return expanded(resource(), setOf(PrioritySchemeGetAllResponseBean.Expand.class, expand));
    }

    private WebResource resource() {
        return createResource().path("priorityschemes");
    }

    private WebResource schemeResource(long schemeId, PrioritySchemeBean.Expand... expand) {
        return expanded(resource().path(Long.toString(schemeId)), setOf(PrioritySchemeBean.Expand.class, expand));
    }

    private WebResource projectSchemeResource(String projectKey) {
        return createResource().path("project").path(projectKey).path("priorityscheme");
    }
}
