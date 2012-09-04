package com.atlassian.jira.testkit.client;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Field Configurations.
 *
 * See FieldConfigurationBackdoor for the code this plugs into at the back-end.
 *
 * @since v5.0.1
 * @author mtokar
 */
public class FieldConfigurationControl extends BackdoorControl<FieldConfigurationControl>
{
    public FieldConfigurationControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * Copies an existing field configuration. The new field configuration will have the name
     * <code>"Copy of " + original name</code> unless specified.
     *
     * @param name the name of the existing field configuration
     * @param copyName the name of the new field configuration
     */
    public void copyFieldConfiguration(String name, String copyName)
    {
        get(createResource().path("fieldConfiguration/copy")
                .queryParam("name", name)
                .queryParam("copyName", copyName)
        );
    }

    public void changeFieldVisibility(String configurationName, String fieldName, boolean hide)
    {
        post(createResource().path("fieldConfiguration/changeFieldVisibility")
                .queryParam("fieldConfigurationName", configurationName).queryParam("fieldName", fieldName).queryParam("hide", Boolean.toString(hide)));
    }

    public void associateCustomFieldWithProject(String customFieldId, String projectName)
    {
        post(createResource().path("fieldConfiguration/associateCustomFieldWithProject")
                .queryParam("fieldId", customFieldId).queryParam("projectName", projectName));
    }

    public void setFieldRenderer(String fieldConfirationName, String fieldId, String renderer) {
        get(createResource().path("fieldConfiguration/renderer")
                .queryParam("fieldConfigurationName", fieldConfirationName)
                .queryParam("fieldId", fieldId)
                .queryParam("renderer", renderer));
    }
    
    /**
     * Hides field in given configuration
     * @param name the name of the existing field configuration
     * @param fieldId the field id
     */
    public void hideField(String name, String fieldId)
    {
        get(createResource().path("fieldConfiguration/hideField")
                .queryParam("name", name)
                .queryParam("fieldId", fieldId)
        );
    }

    /**
     * Shows field in given configuration
     * @param name the name of the existing field configuration
     * @param fieldId the field id
     */
    public void showField(String name, String fieldId)
    {
        get(createResource().path("fieldConfiguration/showField")
                .queryParam("name", name)
                .queryParam("fieldId", fieldId)
        );
    }

}
