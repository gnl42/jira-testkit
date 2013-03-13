/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Custom fields.
 *
 * See {@link com.atlassian.jira.testkit.plugin.CustomFieldsBackdoor} in jira-testkit-plugin for backend.
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
     * Deletes a custom field
     *
     * @param customFieldId The id of the custom field (e.g. customfield_10000)
     */
    public void deleteCustomField(String customFieldId)
    {
        createResource().path("customFields").path("delete").path(customFieldId).delete();
    }

    public static class CustomFieldRequest
    {
        public String name;
        public String description;
        public String type;
        public String searcherKey;
    }

    public static class CustomFieldResponse
    {
        public CustomFieldResponse(String name, String id)
        {
            this.name = name;
            this.id = id;
        }

        public CustomFieldResponse()
        {
        }

        public String name;
        public String id;
    }
}
