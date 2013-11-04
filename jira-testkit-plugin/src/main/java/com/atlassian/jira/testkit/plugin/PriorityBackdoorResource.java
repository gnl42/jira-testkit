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

import com.atlassian.jira.config.PriorityManager;
import com.atlassian.jira.testkit.beans.Priority;
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
        final List<Priority> priorityBeans = Lists.newArrayList();
        for (com.atlassian.jira.issue.priority.Priority priority: priorityManager.getPriorities())
        {
            priorityBeans.add(create(priority));
        }
        return Response.ok(priorityBeans).cacheControl(never()).build();
    }
    
    @POST
    public Response createPriority(Priority bean)
    {
        com.atlassian.jira.issue.priority.Priority priority =  priorityManager.createPriority(bean.getName(), bean.getDescription(), bean.getIconUrl(), bean.getColor());
        return Response.ok(create(priority)).cacheControl(never()).build();
    }
    
    @PUT
    public Response updatePriority(Priority bean)
    {
        com.atlassian.jira.issue.priority.Priority priority = priorityManager.getPriority(bean.getId());
        priorityManager.editPriority(priority, bean.getName(), bean.getDescription(), bean.getIconUrl(), bean.getColor());
        return Response.ok(create(priority)).cacheControl(never()).build();
    }

    @DELETE
    @Path("{id}")
    public Response deletePriority(@PathParam("id") long id)
    {
        com.atlassian.jira.issue.priority.Priority defaultPriority = priorityManager.getDefaultPriority();
        if(defaultPriority == null) {
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
    
    private Priority create(com.atlassian.jira.issue.priority.Priority priority) {
    	return new Priority(
    			trimToNull(priority.getId()), 
    			trimToNull(priority.getDescription()),
    			trimToNull(priority.getName()), 
    			priority.getSequence(), 
    			trimToNull(priority.getStatusColor()), 
    			trimToNull(priority.getIconUrl()));
    }
}
