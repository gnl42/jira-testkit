/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.beans.CustomFieldRequest;
import com.atlassian.jira.testkit.beans.CustomFieldResponse;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;

import java.util.List;

import com.sun.jersey.api.client.WebResource;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Custom fields.
 *
 * See <code>com.atlassian.jira.testkit.plugin.CustomFieldsBackdoor</code> in jira-testkit-plugin for backend.
 *
 * @since v5.0
 * @author esusatyo
 */
public class CustomFieldsControl extends BackdoorControl<CustomFieldsControl>
{
    public CustomFieldsControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * Creates a new custom field
     *
     * @param name the name of the custom field
     * @param description the name of the custom field
     * @param type the type of the custom field
     * @param searcherKey the searcher key of the custom field
     * @return the response ID
     * @see CustomFieldResponse#id
     */
    public String createCustomField(String name, String description, String type, String searcherKey)
    {
        CustomFieldRequest request = new CustomFieldRequest();
        request.name = name;
        request.description = description;
        request.type = type;
        request.searcherKey = searcherKey;

        CustomFieldResponse response = createResource().path("customFields/create").post(CustomFieldResponse.class, request);
        return response.id;
    }

    /**
     * Updates a custom field
     *
     * @param id the id of the custom field
     * @param name the new name of the custom field
     * @param description the new description of the custom field
     * @param searcherKey the new searcher key of the custom field
     */
    public void updateCustomField(String id, String name, String description, String searcherKey)
    {
        CustomFieldRequest request = new CustomFieldRequest();
        request.name = name;
        request.description = description;
        request.searcherKey = searcherKey;

        createResource().path("customFields").path(id).put(request);
    }

    /**
     * Deletes a custom field
     *
     * @param customFieldId The id of the custom field (e.g. customfield_10000)
     */
    public void deleteCustomField(String customFieldId)
    {
        createResource().path("customFields").path("delete").path(customFieldId).delete();
    }

    /**
     * Creates custom field option. Method will just silently exit if custom field can't contain any options.
     * @param customFieldId is of the custom field to create option.
     * @param optionValue option value. May fail in case if such option already exists
     */
    public void addOption(String customFieldId, String optionValue)
    {
        createResource().path("customFields/addOption").path(customFieldId).post(optionValue);
    }

    /**
     * Deletes custom field option. Method will just silently exit if custom field can't contain any options
     * or it doesn't contain deleted one.
     * @param customFieldId is of the custom field which holds deleted option.
     * @param optionValue value of option to delete.
     */
    public void deleteOption(String customFieldId, String optionValue)
    {
        createResource().path("customFields/deleteOption").path(customFieldId).delete(optionValue);
    }

    /**
     * List the non-config custom fields registered in the system.
     *
     * @return see above
     */
    public List<CustomFieldResponse> getCustomFields()
    {
        return getCustomFields(false);
    }

    /**
     * List custom fields registered in the system
     *
     * @param config whether to get config fields
     * @return see above
     */
    public List<CustomFieldResponse> getCustomFields(boolean config)
    {
        return createResource().path("customFields").path("get")
                .queryParam("config", String.valueOf(config))
                .get(new GenericType<List<CustomFieldResponse>>(){});
    }

    /**
     * Creates configuration context for given customField
     * @return id of created custom field
     */
    public long createCustomFieldContext(String customFieldId, List<Long> projectIds, List<Long> issueTypeIds) {
        WebResource r = createResource()
                .path("customFields")
                .path("addCustomFieldContext")
                .queryParam("customFieldId", String.valueOf(customFieldId));

        for (Long projectId : projectIds) {
            r = r.queryParam("projectIds", String.valueOf(projectId));
        }

        for (Long id : issueTypeIds) {
            r = r.queryParam("issueTypeIds", String.valueOf(id));
        }

        return r.post(ClientResponse.class).getEntity(Long.class);
    }

    public void deleteCustomFieldContext(String contextId) {
        WebResource r = createResource()
                .path("customFields")
                .path("deleteCustomFieldContext")
                .queryParam("contextId", String.valueOf(contextId));

        r.delete(ClientResponse.class);
    }
}
