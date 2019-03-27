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

    public void changeFieldDescription(String configurationName, String fieldName, String description)
    {
        createResource().path("fieldConfiguration/changeFieldDescription")
                .queryParam("fieldConfigurationName", configurationName).queryParam("fieldName", fieldName).queryParam("description", description).post();
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

    /**
     * Makes field optional in given configuration
     *
     * @param name    the name of the existing field configuration
     * @param fieldId the field id
     */
    public void makeFieldOptional(String name, String fieldId) {
        get(createResource().path("fieldConfiguration/makeFieldOptional")
            .queryParam("name", name)
            .queryParam("fieldId", fieldId));
    }

    /**
     * Makes field required in given configuration
     *
     * @param name    the name of the existing field configuration
     * @param fieldId the field id
     */
    public void makeFieldRequired(String name, String fieldId) {
        get(createResource().path("fieldConfiguration/makeFieldRequired")
            .queryParam("name", name)
            .queryParam("fieldId", fieldId));
    }

}
