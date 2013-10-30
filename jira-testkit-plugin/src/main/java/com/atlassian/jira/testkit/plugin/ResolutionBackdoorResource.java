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

import com.atlassian.jira.config.ResolutionManager;
import com.atlassian.jira.issue.resolution.Resolution;
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
        List<Resolution> resolutions = resolutionManager.getResolutions();
        final List<ResolutionBean> resolutionBeans = Lists.newArrayList();
        for (Resolution resolution: resolutions)
        {
            resolutionBeans.add(new ResolutionBean(resolution));
        }
        return Response.ok(resolutionBeans).cacheControl(never()).build();
    }
    
    @POST
    public Response createResolution(ResolutionBean bean)
    {
        Resolution resolution =  resolutionManager.createResolution(bean.name, bean.description);
        return Response.ok(new ResolutionBean(resolution)).cacheControl(never()).build();
    }
    
    @PUT
    public Response updateResolution(ResolutionBean bean)
    {
        Resolution resolution =  resolutionManager.getResolution(bean.id);
        resolutionManager.editResolution(resolution, bean.name, bean.description);
        return Response.ok(new ResolutionBean(resolution)).cacheControl(never()).build();
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

    public static class ResolutionBean
    {
        @JsonProperty
        private String id;

        @JsonProperty
        private String description;

        @JsonProperty
        private String name;

        @JsonProperty
        private Long sequence;

        public ResolutionBean()
        {
        }

        public ResolutionBean(Resolution resolution)
        {
            id = trimToNull(resolution.getId());
            name = trimToNull(resolution.getName());
            description = trimToNull(resolution.getDescription());
            sequence = resolution.getSequence();
        }
    }
}
