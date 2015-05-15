package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.restclient.ApplicationLinks;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.sun.jersey.api.client.WebResource;

/**
 * @since v6.1
 */
public class ApplicationLinkControl extends BackdoorControl<ApplicationLinkControl>
{
    public ApplicationLinkControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public String addApplicationLink(String type, String name, String url) throws JSONException
    {
        JSONObject query = new JSONObject();
        query.put("name", name);
        query.put("rpcUrl", url);
        query.put("displayUrl", url);
        query.put("typeId", type);

        return createResource().header("Content-Type", "application/json").put(String.class, query.toString());
    }

    public ApplicationLinks getApplicationLinks(final String type) {
        return createResource()
                .path("type").path(type)
                .header("Content-Type", "application/json")
                .get(ApplicationLinks.class);
    }

    public ApplicationLinks getApplicationLinks() {
        return createResource()
                .header("Content-Type", "application/json")
                .get(ApplicationLinks.class);
    }

    @Override
    protected WebResource createResource()
    {
        WebResource resource = resourceRoot(rootPath).path("rest").path("applinks").path("2.0").path("applicationlink");
        resource.addFilter(new BackdoorLoggingFilter());
        resource.addFilter(new JsonMediaTypeFilter());
        return resource;
    }
}
