package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Arrays;
import java.util.Collection;
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

    public ProjectRole updatePartial(Long id, String name, String description)
    {
        return roles().path(String.valueOf(id)).type(MediaType.APPLICATION_JSON_TYPE).post(ProjectRole.class, new CreateProjectRoleBean(name, description));
    }

    public ProjectRole updateFull(Long id, String name, String description)
    {
        return roles().path(String.valueOf(id)).type(MediaType.APPLICATION_JSON_TYPE).put(ProjectRole.class, new CreateProjectRoleBean(name, description));
    }

    public void deleteProjectRole(Long id)
    {
        roles().path(String.valueOf(id)).delete();
    }

    public void deleteProjectRole(Long id, Long replacementId)
    {
        roles().path(String.valueOf(id)).queryParam("swap", String.valueOf(replacementId)).delete();
    }

    public ProjectRoleActorsBean getDefaultActorsForRole(Long id)
    {
        return roles().path(String.valueOf(id)).path("actors").get(ProjectRoleActorsBean.class);
    }

    public ProjectRoleActorsBean addDefaultActorsToRole(Long id, String[] usernames, String[] groupnames)
    {
        return roles()
                .path(String.valueOf(id)).path("actors")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ProjectRoleActorsBean.class,
                        new ActorInputBean(
                                usernames == null ? null : Arrays.asList(usernames),
                                groupnames == null? null : Arrays.asList(groupnames)));
    }

    public ProjectRoleActorsBean deleteDefaultActorsToRole(Long id, String username, String groupname)
    {
        WebResource actors = roles().path(String.valueOf(id)).path("actors");
        if (username != null)
        {
            actors = actors.queryParam("user", username);
        }
        else if (groupname != null)
        {
            actors = actors.queryParam("group", groupname);
        }
        return actors.delete(ProjectRoleActorsBean.class);
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

    private static class ActorInputBean
    {
        @JsonProperty
        private Collection<String> user;
        @JsonProperty
        private Collection<String> group;

        @JsonCreator
        public ActorInputBean(@JsonProperty("user") Collection<String> usernames, @JsonProperty("group") Collection<String> groupnames)
        {
            this.user = usernames;
            this.group = groupnames;
        }
    }
}
