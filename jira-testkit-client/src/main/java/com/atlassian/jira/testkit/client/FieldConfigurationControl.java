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

    public void changeFieldVisibility(String configurationName, String fieldName, boolean hide)
    {
        createResource().path("fieldConfiguration/changeFieldVisibility")
                .queryParam("fieldConfigurationName", configurationName).queryParam("fieldName", fieldName).queryParam("hide", Boolean.toString(hide)).post();
    }

    public void associateCustomFieldWithProject(String customFieldId, String projectName)
    {
        createResource().path("fieldConfiguration/associateCustomFieldWithProject")
                .queryParam("fieldId", customFieldId).queryParam("projectName", projectName).post();
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
