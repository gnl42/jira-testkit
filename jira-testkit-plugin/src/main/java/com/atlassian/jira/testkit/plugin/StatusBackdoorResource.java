package com.atlassian.jira.testkit.plugin;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;
import static org.apache.commons.lang.StringUtils.trimToNull;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.annotate.JsonProperty;

import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;

@Path ("status")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class StatusBackdoorResource
{
    private final StatusManager statusManager;
    
    public StatusBackdoorResource(StatusManager statusManager)
    {
        this.statusManager = statusManager;
    }

    @GET
    public Response getAllStatuses()
    {
        Collection<Status> statuses = statusManager.getStatuses();
        final List<StatusBean> statusBeans = Lists.newArrayList();
        for (Status status : statuses)
        {
            statusBeans.add(new StatusBean(status));
        }
        return Response.ok(statusBeans).cacheControl(never()).build();
    }
    
    @POST
    public Response createStatus(StatusBean bean)
    {
        Status status =  statusManager.createStatus(bean.name, bean.description, bean.iconUrl);
        return Response.ok(new StatusBean(status)).cacheControl(never()).build();
    }
    
    @PUT
    public Response updateStatus(StatusBean bean)
    {
        Status status = statusManager.getStatus(bean.id);
        statusManager.editStatus(status, bean.name, bean.description, bean.iconUrl);
        return Response.ok(new StatusBean(status)).cacheControl(never()).build();
    }
    
    @DELETE
    @Path("{id}")
    public Response deleteStatus(@PathParam("id") long id)
    {
        statusManager.removeStatus(String.valueOf(id));
        return Response.ok().cacheControl(never()).build();
    }
    
    public static class StatusBean
    {
        @JsonProperty
        private String id;

        @JsonProperty
        private String description;

        @JsonProperty
        private String iconUrl;

        @JsonProperty
        private String name;


        public StatusBean()
        {
        }

        public StatusBean(Status status)
        {
            id = trimToNull(status.getId());
            name = trimToNull(status.getName());
            iconUrl = trimToNull(status.getIconUrl());
            description = trimToNull(status.getDescription());
        }
    }
}
