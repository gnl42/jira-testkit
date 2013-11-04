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

import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.testkit.beans.Status;
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
        final List<Status> statusBeans = Lists.newArrayList();
        for (com.atlassian.jira.issue.status.Status status : statusManager.getStatuses())
        {
            statusBeans.add(create(status));
        }
        return Response.ok(statusBeans).cacheControl(never()).build();
    }
    
    @POST
    public Response createStatus(Status bean)
    {
        com.atlassian.jira.issue.status.Status status =  statusManager.createStatus(bean.getName(), bean.getDescription(), bean.getIconUrl());
        return Response.ok(create(status)).cacheControl(never()).build();
    }
    
    @PUT
    public Response updateStatus(Status bean)
    {
        com.atlassian.jira.issue.status.Status status = statusManager.getStatus(bean.getId());
        statusManager.editStatus(status, bean.getName(), bean.getDescription(), bean.getIconUrl());
        return Response.ok(create(status)).cacheControl(never()).build();
    }
    
    @DELETE
    @Path("{id}")
    public Response deleteStatus(@PathParam("id") long id)
    {
        statusManager.removeStatus(String.valueOf(id));
        return Response.ok().cacheControl(never()).build();
    }
    
    private Status create(com.atlassian.jira.issue.status.Status status) {
    	return new Status(
    			trimToNull(status.getId()), 
    			trimToNull(status.getName()),
    			trimToNull(status.getDescription()),
    			trimToNull(status.getIconUrl()));
    }
}
