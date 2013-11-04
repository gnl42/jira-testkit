package com.atlassian.jira.testkit.plugin;

import static org.apache.commons.lang.StringUtils.trimToNull;
import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;

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

import com.atlassian.jira.config.ResolutionManager;
import com.atlassian.jira.testkit.beans.Resolution;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Lists;

@Path ("resolution")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class ResolutionBackdoorResource
{
    private final ResolutionManager resolutionManager;
    
    public ResolutionBackdoorResource(ResolutionManager resolutionManager)
    {
        this.resolutionManager = resolutionManager;
    }

    @GET
    public Response getAllResolutions()
    {
        final List<Resolution> resolutionBeans = Lists.newArrayList();
        for (com.atlassian.jira.issue.resolution.Resolution resolution: resolutionManager.getResolutions())
        {
            resolutionBeans.add(create(resolution));
        }
        return Response.ok(resolutionBeans).cacheControl(never()).build();
    }
    
    @POST
    public Response createResolution(Resolution bean)
    {
        com.atlassian.jira.issue.resolution.Resolution resolution =  resolutionManager.createResolution(bean.getName(), bean.getDescription());
        return Response.ok(create(resolution)).cacheControl(never()).build();
    }
    
    @PUT
    public Response updateResolution(Resolution bean)
    {
        com.atlassian.jira.issue.resolution.Resolution resolution =  resolutionManager.getResolution(bean.getId());
        resolutionManager.editResolution(resolution, bean.getName(), bean.getDescription());
        return Response.ok(create(resolution)).cacheControl(never()).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteResolution(@PathParam("id") long id)
    {
        resolutionManager.removeResolution(String.valueOf(id), resolutionManager.getDefaultResolution().getId());
        return Response.ok().cacheControl(never()).build();
    }
    
    @POST
    @Path ("{id}/up")
    public Response moveUp(@PathParam("id") long id)
    {
        resolutionManager.moveResolutionUp(String.valueOf(id));
        return Response.ok().cacheControl(never()).build();
    }
    
    @POST
    @Path ("{id}/down")
    public Response moveDown(@PathParam("id") long id)
    {
        resolutionManager.moveResolutionDown(String.valueOf(id));
        return Response.ok().cacheControl(never()).build();
    }
    
    @POST
    @Path ("{id}/default")
    public Response setDefault(@PathParam("id") long id)
    {
        resolutionManager.setDefaultResolution(String.valueOf(id));
        return Response.ok().cacheControl(never()).build();
    }
    
    private Resolution create(com.atlassian.jira.issue.resolution.Resolution resolution) {
    	return new Resolution(
    			trimToNull(resolution.getId()), 
    			trimToNull(resolution.getName()), 
    			trimToNull(resolution.getDescription()), 
    			resolution.getSequence());
    }
}
