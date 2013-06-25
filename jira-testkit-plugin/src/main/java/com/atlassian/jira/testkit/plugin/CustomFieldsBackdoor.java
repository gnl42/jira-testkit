/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.RemoveException;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.context.manager.JiraContextTreeManager;
import com.atlassian.jira.issue.customfields.CustomFieldSearcher;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.CustomFieldUtils;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.testkit.beans.CustomFieldRequest;
import com.atlassian.jira.testkit.beans.CustomFieldResponse;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.ofbiz.core.entity.GenericEntityException;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A backdoor for manipulating custom fields.
 *
 * @since v5.1
 */
@Path("customFields")
@Produces ({ MediaType.APPLICATION_JSON})
@Consumes ({MediaType.APPLICATION_JSON})
public class CustomFieldsBackdoor
{
    private final CustomFieldManager customFieldManager;
    private final JiraContextTreeManager treeManager;

    public CustomFieldsBackdoor(CustomFieldManager customFieldManager)
    {
        this.customFieldManager = customFieldManager;
        this.treeManager = ComponentAccessor.getComponent(JiraContextTreeManager.class);
    }

    @POST
    @AnonymousAllowed
    @Path("create")
    public Response createCustomField(final CustomFieldRequest field)
    {
        final CustomFieldType<?,?> type = customFieldManager.getCustomFieldType(field.type);
        if (type == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Custom field type with key '" + field.type + "' does not exist").build();
        }
        CustomFieldSearcher searcher = null;
        if (field.searcherKey != null)
        {
            final List<CustomFieldSearcher> searchers = customFieldManager.getCustomFieldSearchers(type);
            try{
                searcher = Iterables.find(searchers, new Predicate<CustomFieldSearcher>()
                {
                    @Override
                    public boolean apply(@Nullable CustomFieldSearcher customFieldSearcher)
                    {
                        return field.searcherKey.equals(customFieldSearcher.getDescriptor().getCompleteKey());
                    }
                });
            }
            catch (NoSuchElementException e)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("Searcher with key " + field.searcherKey
                        + " not found for type '" + field.type + "'").build();
            }
        }
        // global context
        final List<JiraContextNode> contexts = CustomFieldUtils.buildJiraIssueContexts(true, null, null, treeManager);
        final List<IssueType> allTypes = Collections.singletonList(null);
        try
        {
            CustomField result = customFieldManager.createCustomField(field.name, field.description, type, searcher, contexts, allTypes);
            return Response.ok(new CustomFieldResponse(result.getName(), result.getId(), result.getCustomFieldType().getKey())).build();
        }
        catch (GenericEntityException e)
        {
            throw new IllegalStateException("Something went really wrong", e);
        }
    }

    @GET
    @AnonymousAllowed
    @Path("get")
    public Response getCustomFields()
    {
        return Response.ok(Lists.newArrayList(Iterables.transform(customFieldManager.getCustomFieldObjects(), new Function<CustomField, Object>()
        {
            @Override
            public CustomFieldResponse apply(final CustomField input)
            {
                return new CustomFieldResponse(input.getName(), input.getId(), input.getCustomFieldType().getKey());
            }
        }))).build();
    }

    @DELETE
    @AnonymousAllowed
    @Path("delete/{id}")
    public Response deleteCustomField(@PathParam("id") String customFieldId)
    {
        if(customFieldId == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Please supply custom field id").build();
        }

        CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);
        if(customField == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Custom field with id " + customFieldId + " does not exist").build();
        }

        try
        {
            customFieldManager.removeCustomField(customField);
            return Response.ok().build();
        }
        catch (RemoveException e)
        {
            throw new IllegalStateException("Something went wrong: ", e);
        }

    }
}
