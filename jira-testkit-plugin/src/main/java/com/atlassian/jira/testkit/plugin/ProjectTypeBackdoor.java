package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.project.type.ProjectType;
import com.atlassian.jira.project.type.ProjectTypeKey;
import com.atlassian.jira.project.type.ProjectTypeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.transform;

@Path ("project-type")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class ProjectTypeBackdoor
{
    private static final Supplier<Response> NOT_FOUND = new Supplier<Response>()
    {
        @Override
        public Response get()
        {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .cacheControl(never())
                    .build();
        }
    };

    private static final Function<Object, Response> FOUND = new Function<Object, Response>()
    {
        @Override
        public Response apply(@Nullable final Object input)
        {
            return Response.ok(input).cacheControl(never()).build();
        }
    };

    private final ProjectTypeManager projectTypeManager;

    public ProjectTypeBackdoor(final ProjectTypeManager projectTypeManager)
    {
        this.projectTypeManager = projectTypeManager;
    }

    @GET
    public Response getAllProjectTypes()
    {
        return Response
                .ok(copyOf(transform(projectTypeManager.getAllProjectTypes(), ProjectTypeBean.TO_BEAN)))
                .cacheControl(never())
                .build();
    }

    @GET
    @Path("{key}")
    public Response getByKey(@PathParam ("key") final String projectTypeKey)
    {
        return projectTypeManager
                .getByKey(new ProjectTypeKey(projectTypeKey))
                .map(ProjectTypeBean.TO_BEAN)
                .map(FOUND)
                .getOrElse(NOT_FOUND);
    }

    private static class ProjectTypeBean
    {
        public static final Function<ProjectType, ProjectTypeBean> TO_BEAN = new Function<ProjectType, ProjectTypeBean>()
        {
            @Override
            public ProjectTypeBean apply(@Nullable final ProjectType projectType)
            {
                return new ProjectTypeBean(projectType);
            }
        };

        private final ProjectType projectType;

        public ProjectTypeBean(final ProjectType projectType)
        {
            this.projectType = projectType;
        }

        @JsonProperty
        public String getKey()
        {
            return projectType.getKey().getKey();
        }

        @JsonProperty
        public String getDescriptionI18nKey()
        {
            return projectType.getDescriptionI18nKey();
        }

        @JsonProperty
        public String getIcon()
        {
            return projectType.getIcon();
        }

        @JsonProperty
        public String getColor()
        {
            return projectType.getColor();
        }
    }
}
