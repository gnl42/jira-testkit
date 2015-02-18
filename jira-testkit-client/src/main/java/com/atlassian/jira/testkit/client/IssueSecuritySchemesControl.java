/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing notifications and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Issue Security Schemes.
 *
 * See com.atlassian.jira.testkit.plugin.IssueSecuritySchemesBackdoor in jira-testkit-plugin for backend.
 *
 * @since v6.2.19
 */
public class IssueSecuritySchemesControl extends BackdoorControl<IssueSecuritySchemesControl>
{
    public IssueSecuritySchemesControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * @param schemeName the name of the new scheme
     * @param description can be null
     * @return {Long} the schemeId of the created scheme
     */
    public Long createScheme(String schemeName, String description)
    {
        return Long.parseLong(createResource().path("issueSecuritySchemes/create")
                .queryParam("schemeName", schemeName).queryParam("schemeDescription", description).get(String.class));
    }

    public void deleteScheme(long schemeId)
    {
        createResource().path("issueSecuritySchemes").path(String.valueOf(schemeId)).delete();
    }

    public Long addSecurityLevel(long schemeId, String name, String description)
    {
        return Long.parseLong(createResource().path("issueSecuritySchemes").path(String.valueOf(schemeId))
                .queryParam("name", name).queryParam("description", description).post(String.class));
    }

    public void addUserToSecurityLevel(long schemeId, long levelId, String userKey)
    {
        createResource().path("issueSecuritySchemes").path(String.valueOf(schemeId)).path(String.valueOf(levelId)).queryParam("userKey", userKey).post();
    }
}
