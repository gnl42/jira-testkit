/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import java.sql.Timestamp;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;

@Path("auditing")
@AnonymousAllowed
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AuditingBackdoor
{
    private static String ENTITY_NAME = "AuditLog";
    private static String ITEMS_ENTITY_NAME = "AuditItem";
    private static String CHANGED_VALUES_ENTITY_NAME = "AuditChangedValue";

    private final OfBizDelegator ofBizDelegator;

    public AuditingBackdoor(OfBizDelegator ofBizDelegator) {
        this.ofBizDelegator = ofBizDelegator;
    }

    @GET
    @Path("clearAll")
    public Response clearAll() {
        final List<GenericValue> records = ofBizDelegator.findAll (ENTITY_NAME);
        if (records != null)
        {
            for(GenericValue record : records)
            {
                ofBizDelegator.removeRelated("Child" + ITEMS_ENTITY_NAME, record);
                ofBizDelegator.removeRelated("Child" + CHANGED_VALUES_ENTITY_NAME, record);
                ofBizDelegator.removeValue(record);
            }
        }
        return Response.ok().cacheControl(never()).build();
    }

    @GET
    @Path("moveAllRecordsBackInTime")
    public Response moveAllRecordsBackInTime(@QueryParam ("secondsIntoPast") Long secondsIntoPast) throws GenericEntityException
    {
        final List<GenericValue> records = ofBizDelegator.findAll (ENTITY_NAME);
        for (GenericValue record : records)
        {
            record.set("created", new Timestamp(record.getTimestamp("created").getTime() - secondsIntoPast * 1000));
            record.store();
        }
        return Response.ok().cacheControl(never()).build();
    }
}