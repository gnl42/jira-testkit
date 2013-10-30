package com.atlassian.jira.testkit.plugin;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;
import static org.apache.commons.lang.StringUtils.trimToNull;

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

import com.atlassian.jira.config.PriorityManager;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;

@Path ("priority")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class PriorityBackdoorResource
{
    private final PriorityManager priorityManager;
    
    public PriorityBackdoorResource(PriorityManager priorityManager)
    {
        this.priorityManager = priorityManager;
    }

    @GET
    public Response getAllPriorities()
    {
        List<Priority> priorities = priorityManager.getPriorities();
        final List<PriorityBean> priorityBeans = Lists.newArrayList();
        for (Priority priority: priorities)
        {
            priorityBeans.add(new PriorityBean(priority));
        }
        return Response.ok(priorityBeans).cacheControl(never()).build();
    }
    
    @POST
    public Response createPriority(PriorityBean bean)
    {
        Priority priority =  priorityManager.createPriority(bean.name, bean.description, bean.iconUrl, bean.color);
        return Response.ok(new PriorityBean(priority)).cacheControl(never()).build();
    }
    
    @PUT
    public Response updatePriority(PriorityBean bean)
    {
        Priority priority = priorityManager.getPriority(bean.id);
        priorityManager.editPriority(priority, bean.name, bean.description, bean.iconUrl, bean.color);
        return Response.ok(new PriorityBean(priority)).cacheControl(never()).build();
    }

    @DELETE
    @Path("{id}")
    public Response deletePriority(@PathParam("id") long id)
    {
        Priority defaultPriority = priorityManager.getDefaultPriority();
        if(defaultPriority==null) {
            defaultPriority = priorityManager.getPriorities().get(0);
        }
        priorityManager.removePriority(String.valueOf(id), defaultPriority.getId());
        return Response.ok().cacheControl(never()).build();
    }
    
    @POST
    @Path ("{id}/up")
    public Response moveUp(@PathParam("id") long id)
    {
        priorityManager.movePriorityUp(String.valueOf(id));
        return Response.ok().cacheControl(never()).build();
    }
    
    @POST
    @Path ("{id}/down")
    public Response moveDown(@PathParam("id") long id)
    {
        priorityManager.movePriorityDown(String.valueOf(id));
        return Response.ok().cacheControl(never()).build();
    }
    
    @POST
    @Path ("{id}/default")
    public Response setDefault(@PathParam("id") long id)
    {
        priorityManager.setDefaultPriority(String.valueOf(id));
        return Response.ok().cacheControl(never()).build();
    }

    public static class PriorityBean
    {
        @JsonProperty
        private String id;

        @JsonProperty
        private String description;

        @JsonProperty
        private String name;

        @JsonProperty
        private Long sequence;

        @JsonProperty
        private String color;

        @JsonProperty
        private String iconUrl;


        public PriorityBean()
        {
        }

        public PriorityBean(Priority priority)
        {
            id = trimToNull(priority.getId());
            name = trimToNull(priority.getName());
            iconUrl = trimToNull(priority.getIconUrl());
            description = trimToNull(priority.getDescription());
            color = trimToNull(priority.getStatusColor());
            sequence = priority.getSequence();
        }
    }
}
