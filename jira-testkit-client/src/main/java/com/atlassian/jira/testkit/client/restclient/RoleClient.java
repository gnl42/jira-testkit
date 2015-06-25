package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import javax.ws.rs.core.MediaType;

/**
 * @since 7.0
 */
public class RoleClient extends RestApiClient<RoleClient>
{
    public RoleClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public ProjectRole get(String roleKey)
    {
        return roles().path(roleKey).get(ProjectRole.class);
    }

    public List<ProjectRole> get()
    {
        return roles().get(new GenericType<List<ProjectRole>>(){});
    }

    public ProjectRole create(String name, String description)
    {
        return roles().type(MediaType.APPLICATION_JSON_TYPE).post(ProjectRole.class, new CreateProjectRoleBean(name, description));
    }

    protected WebResource roles()
    {
        return createResource().path("role");
    }

    private static class CreateProjectRoleBean
    {
        @JsonProperty
        private String name;
        @JsonProperty
        private String description;

        public CreateProjectRoleBean(String name, String description)
        {
            this.name = name;
            this.description = description;
        }
    }
}
