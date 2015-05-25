package com.atlassian.jira.testkit.client;

import java.util.ArrayList;
import java.util.List;

public class FiltersControl  extends BackdoorControl<FiltersControl>
{
    public FiltersControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public String createFilter(String jql, String name)
    {
        return get(createResource().path("filter")
                .path("create")
                .queryParam("jql", jql)
                .queryParam("name", name));
    }

    public String createPublicFilter(String jql, String name)
    {
        return get(createResource().path("filter")
                .path("create")
                .queryParam("jql", jql)
                .queryParam("name", name)
                .queryParam("sharing", "global"));
    }

    public List<String> getColumnsForFilter(String id)
    {
        throw new RuntimeException("This resource not bridged yet");
        //return createResource().path("filter").path("columns").queryParam("id", id).get(ArrayList.class);
    }
}
