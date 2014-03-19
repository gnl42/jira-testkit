package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.fields.layout.field.FieldConfigurationScheme;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.security.IssueSecuritySchemeManager;
import com.atlassian.jira.notification.NotificationSchemeManager;
import com.atlassian.jira.permission.PermissionSchemeManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.testkit.beans.FieldConfigurationSchemeBean;
import com.atlassian.jira.testkit.beans.IssueSecuritySchemeBean;
import com.atlassian.jira.testkit.beans.IssueTypeSchemeBean;
import com.atlassian.jira.testkit.beans.NotificationSchemeBean;
import com.atlassian.jira.testkit.beans.PermissionSchemeBean;
import com.atlassian.jira.testkit.beans.ProjectSchemesBean;
import com.atlassian.jira.testkit.beans.WorkflowSchemeData;
import com.atlassian.jira.testkit.plugin.beanbuilders.FieldConfigurationSchemeBeanFactory;
import com.atlassian.jira.testkit.plugin.beanbuilders.IssueSecuritySchemeBeanFactory;
import com.atlassian.jira.testkit.plugin.beanbuilders.IssueTypeSchemeBeanFactory;
import com.atlassian.jira.testkit.plugin.beanbuilders.NotificationSchemeBeanFactory;
import com.atlassian.jira.testkit.plugin.beanbuilders.PermissionSchemeBeanFactory;
import com.atlassian.jira.testkit.plugin.workflows.WorkflowSchemeDataFactory;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.WorkflowScheme;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import java.util.regex.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Retrieves the schemes for the project specified. This resource may not be exhaustive - it may not return all schemes
 * for the project.
 *
 * @since v6.3
 */
@AnonymousAllowed
@Produces ({ MediaType.APPLICATION_JSON })
@Consumes ({ MediaType.APPLICATION_JSON })
@Path ("project/{projectIdOrKey}/schemes")
public class ProjectSchemesBackdoorResource
{
    private static final Pattern PROJECT_ID_PATTERN = Pattern.compile("^[1-9]\\d{0,17}$");

    private final FieldLayoutManager fieldLayoutManager;
    private final IssueSecuritySchemeManager issueSecuritySchemeManager;
    private final IssueTypeSchemeManager issueTypeSchemeManager;
    private final NotificationSchemeManager notificationSchemeManager;
    private final PermissionSchemeManager permissionSchemeManager;
    private final ProjectManager projectManager;
    private final WorkflowSchemeDataFactory workflowSchemeDataFactory;
    private final WorkflowSchemeManager workflowSchemeManager;

    public ProjectSchemesBackdoorResource(
            final FieldLayoutManager fieldLayoutManager,
            final IssueSecuritySchemeManager issueSecuritySchemeManager,
            final IssueTypeSchemeManager issueTypeSchemeManager,
            final NotificationSchemeManager notificationSchemeManager,
            final PermissionSchemeManager permissionSchemeManager,
            final ProjectManager projectManager,
            final WorkflowSchemeDataFactory workflowSchemeDataFactory,
            final WorkflowSchemeManager workflowSchemeManager)
    {
        this.fieldLayoutManager = fieldLayoutManager;
        this.issueSecuritySchemeManager = issueSecuritySchemeManager;
        this.issueTypeSchemeManager = issueTypeSchemeManager;
        this.notificationSchemeManager = notificationSchemeManager;
        this.permissionSchemeManager = permissionSchemeManager;
        this.projectManager = projectManager;
        this.workflowSchemeDataFactory = workflowSchemeDataFactory;
        this.workflowSchemeManager = workflowSchemeManager;
    }

    @GET
    public Response getProjectSchemes(@PathParam ("projectIdOrKey") String projectIdOrKey)
    {
        if (projectIdOrKey == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("projectIdOrKey must be provided.").build();
        }
        Project project = getProject(projectIdOrKey);
        if (project == null)
        {
            return Response.status(Response.Status.NOT_FOUND).entity("The specified project does not exist.").build();

        }
        return Response.ok(new ProjectSchemesBeanBuilder()
                .fieldConfigurationScheme(getFieldConfigurationScheme(project))
                .issueSecurityScheme(getIssueSecurityScheme(project))
                .issueTypeScheme(getIssueTypeScheme(project))
                .notificationScheme(getNotificationScheme(project))
                .permissionScheme(getPermissionScheme(project))
                .workflowScheme(getWorkflowScheme(project))
                .build()
        ).build();
    }

    private Project getProject(final String projectIdOrKey)
    {
        return PROJECT_ID_PATTERN.matcher(projectIdOrKey).matches()
                ? projectManager.getProjectObj(Long.parseLong(projectIdOrKey))
                : projectManager.getProjectObjByKey(projectIdOrKey);
    }

    private FieldConfigurationScheme getFieldConfigurationScheme(final Project project)
    {
        return fieldLayoutManager.getFieldConfigurationScheme(project);
    }

    private Scheme getIssueSecurityScheme(final Project project)
    {
        return issueSecuritySchemeManager.getSchemeFor(project);
    }

    private FieldConfigScheme getIssueTypeScheme(final Project project)
    {
        return issueTypeSchemeManager.getConfigScheme(project);
    }

    private Scheme getNotificationScheme(final Project project)
    {
        return notificationSchemeManager.getSchemeFor(project);
    }

    private Scheme getPermissionScheme(final Project project)
    {
        return permissionSchemeManager.getSchemeFor(project);
    }

    private AssignableWorkflowScheme getWorkflowScheme(final Project project)
    {
        return workflowSchemeManager.getWorkflowSchemeObj(project);
    }

    class ProjectSchemesBeanBuilder {
        private FieldConfigurationSchemeBean fieldConfigurationSchemeBean;
        private IssueSecuritySchemeBean issueSecuritySchemeBean;
        private IssueTypeSchemeBean issueTypeSchemeBean;
        private NotificationSchemeBean notificationSchemeBean;
        private PermissionSchemeBean permissionSchemeBean;
        private WorkflowSchemeData workflowSchemeBean;

        public ProjectSchemesBeanBuilder fieldConfigurationScheme(FieldConfigurationScheme fieldConfigurationScheme)
        {
            fieldConfigurationSchemeBean = FieldConfigurationSchemeBeanFactory.toFieldConfigurationSchemeBean(fieldConfigurationScheme);
            return this;
        }

        public ProjectSchemesBeanBuilder issueSecurityScheme(Scheme issueSecurityScheme)
        {
            issueSecuritySchemeBean = IssueSecuritySchemeBeanFactory.toIssueSecuritySchemeBean(issueSecurityScheme);
            return this;
        }

        public ProjectSchemesBeanBuilder issueTypeScheme(FieldConfigScheme issueTypeScheme)
        {
            issueTypeSchemeBean = IssueTypeSchemeBeanFactory.toIssueTypeSchemeBean(issueTypeScheme);
            return this;
        }

        public ProjectSchemesBeanBuilder notificationScheme(Scheme notificationScheme)
        {
            notificationSchemeBean = NotificationSchemeBeanFactory.toNotificationSchemeBean(notificationScheme);
            return this;
        }

        public ProjectSchemesBeanBuilder permissionScheme(Scheme permissionScheme)
        {
            permissionSchemeBean = PermissionSchemeBeanFactory.toPermissionSchemeBean(permissionScheme);
            return this;
        }

        public ProjectSchemesBeanBuilder workflowScheme(WorkflowScheme workflowScheme)
        {
            workflowSchemeBean = workflowSchemeDataFactory.toData(workflowScheme);
            return this;
        }

        public ProjectSchemesBean build()
        {
            return new ProjectSchemesBean(
                    fieldConfigurationSchemeBean,
                    issueSecuritySchemeBean,
                    issueTypeSchemeBean,
                    notificationSchemeBean,
                    permissionSchemeBean,
                    workflowSchemeBean
            );
        }
    }
}
