package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;

/**
 * Allows you to enable/disable issue links.
 *
 * See {@link com.atlassian.jira.testkit.plugin.IssueLinkingBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class IssueLinkingControl extends BackdoorControl<IssueLinkingControl>
{
    public IssueLinkingControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }
    
    public boolean isEnabled()
    {
        return createResource().get(Boolean.class);
    }

    public boolean enable()
    {
        return createResource().post(Boolean.class, true);
    }

    public boolean disable()
    {
        return createResource().post(Boolean.class, false);
    }

    @Override
    public WebResource createResource()
    {
        return super.createResource().path("issueLinking");
    }

    public void createIssueLinkType(String name, String outward, String inward) {
        get(createResource().path("create")
                .queryParam("name", name)
                .queryParam("outward", outward)
                .queryParam("inward", inward)
        );
    }

    public void createIssueLinkType(String name, String outward, String inward, String style)
    {
        get(createResource().path("create")
                .queryParam("name", name)
                .queryParam("outward", outward)
                .queryParam("inward", inward)
                .queryParam("style", style)
        );
    }

	public void deleteIssueLinkType(String name)
	{
		get(createResource().path("delete")
				.queryParam("name", name));
	}
}
