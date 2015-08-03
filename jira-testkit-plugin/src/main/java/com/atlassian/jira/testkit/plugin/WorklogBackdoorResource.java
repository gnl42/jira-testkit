package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.ofbiz.FieldMap;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.ofbiz.core.entity.EntityFieldMap;
import org.ofbiz.core.entity.EntityOperator;
import org.ofbiz.core.entity.GenericValue;

import java.sql.Timestamp;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path ("worklog")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class WorklogBackdoorResource
{

    @Path("updatedtime")
    @PUT
    public Response setUpdatedTime(@QueryParam("worklogId") final Long worklogId,
            @QueryParam("timestamp") final Long timestamp)
    {
        final OfBizDelegator ofBizDelegator = ComponentAccessor.getOfBizDelegator();
        final GenericValue worklog = ofBizDelegator.findById("Worklog", worklogId);
        if (worklog.get("updated") == null)
        {
            throw new AssertionError("Updated field was expected to be filled");
        }
        ofBizDelegator.bulkUpdateByPrimaryKey("Worklog", ImmutableMap.of("updated", new Timestamp(timestamp)), Lists.newArrayList(worklogId));
        return Response.ok().build();
    }

    @Path("deletedtime")
    @PUT
    public Response setDeletedChangeGroupTime(@QueryParam("worklogId") final Long worklogId,
            @QueryParam("timestamp") final Long timestamp)
    {
        final OfBizDelegator ofBizDelegator = ComponentAccessor.getOfBizDelegator();
        final GenericValue genericValue = ofBizDelegator.findByCondition("ChangeItem",
                new EntityFieldMap(FieldMap.build("field", "WorklogId", "oldvalue", worklogId.toString()), EntityOperator.AND),
                Lists.newArrayList("group")).get(0);

        final Long id = genericValue.getLong("group");

        ofBizDelegator.bulkUpdateByPrimaryKey("ChangeGroup", ImmutableMap.of("created", new Timestamp(timestamp)), Lists.newArrayList(id));
        return Response.ok().build();
    }
}
