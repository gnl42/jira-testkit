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
