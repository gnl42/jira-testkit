package com.atlassian.jira.testkit.plugin;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.Suppliers;
import com.atlassian.jira.entity.property.EntityProperty;
import com.atlassian.jira.entity.property.JsonEntityPropertyManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Function;

/**
 * Backdoor for accessing entity properties
 *
 * @since 6.5
 */
@Path("entityproperties/{entityType}/{entityId}/properties")
@AnonymousAllowed
@Produces(MediaType.APPLICATION_JSON)
@Consumes ({ MediaType.APPLICATION_JSON })
public class EntityPropertyBackdoor {
    private final JsonEntityPropertyManager propertyManager;

    public EntityPropertyBackdoor(JsonEntityPropertyManager propertyManager)
    {
        this.propertyManager = propertyManager;
    }

    @GET
    public Response getAllProperties(
            @PathParam("entityType") String type,
            @PathParam("entityId") Long entityId)
    {
        List<String> keys = propertyManager.findKeys(type, entityId);
        return Response.ok().entity(keys).build();
    }

    @GET
    @Path("{propertyKey}")
    public Response getProperty(
            @PathParam("entityType") String type,
            @PathParam("entityId") Long entityId,
            @PathParam("propertyKey") String key)
    {

        EntityProperty entityProperty = propertyManager.get(type, entityId, key);
        return Option.option(entityProperty).fold(Suppliers.ofInstance(Response.status(Response.Status.NOT_FOUND).build()), new Function<EntityProperty, Response>() {
            @Override
            public Response apply(EntityProperty entityProperty) {
                return Response.ok().entity(entityProperty.getValue()).build();
            }
        });
    }

    @PUT
    @Path("{propertyKey}")
    public Response putProperty(
            @PathParam("entityType") String type,
            @PathParam("entityId") Long entityId,
            @PathParam("propertyKey") String key,
            String value)
    {
        propertyManager.put(type, entityId, key, value);
        return Response.ok().build();
    }

    @DELETE
    @Path("{propertyKey}")
    public Response deleteProperty(
            @PathParam("entityType") String type,
            @PathParam("entityId") Long entityId,
            @PathParam("propertyKey") String key)
    {
        propertyManager.delete(type, entityId, key);
        return Response.ok().build();
    }
}
