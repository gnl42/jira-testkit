package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Issue Type Screen Schemes.
 *
 * See IssueTypeScreenSchemesBackdoor for the code this plugs into at the back-end.
 *
 * @since v5.0
 */
public class IssueTypeScreenSchemesControl extends BackdoorControl<IssueTypeScreenSchemesControl>
{
    public IssueTypeScreenSchemesControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public Long createScheme(String name, String description, Long fieldScreenSchemeId)
    {
        return getId(path().path("create")
                .queryParam("name", name)
                .queryParam("description", description)
                .queryParam("fieldScreenSchemeId", "" + fieldScreenSchemeId));
    }

    public void removeScheme(Long id)
    {
        path().path("remove").queryParam("id", "" + id);
    }
    
    private WebResource path()
    {
        return createResource().path("issueTypeScreenSchemes/");
    }
}
