package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * JSON representation of a projects' schemes. The current state is not exhaustive of the project's state - add
 * attributes as you need them here.
 *
 * @since 6.3
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class ProjectSchemesBean
{
    @JsonProperty
    public final FieldConfigurationSchemeBean fieldConfigurationScheme;
    @JsonProperty
    public final IssueSecuritySchemeBean issueSecurityScheme;
    @JsonProperty
    public final IssueTypeSchemeBean issueTypeScheme;
    @JsonProperty
    public final NotificationSchemeBean notificationScheme;
    @JsonProperty
    public final PermissionSchemeBean permissionScheme;
    @JsonProperty
    public WorkflowSchemeData workflowScheme;

    public ProjectSchemesBean(final FieldConfigurationSchemeBean fieldConfigurationSchemeBean,
            final IssueSecuritySchemeBean issueSecuritySchemeBean,
            final IssueTypeSchemeBean issueTypeSchemeBean,
            final NotificationSchemeBean notificationSchemeBean,
            final PermissionSchemeBean permissionSchemeBean,
            final WorkflowSchemeData workflowSchemeData)
    {
        this.fieldConfigurationScheme = fieldConfigurationSchemeBean;
        this.issueSecurityScheme = issueSecuritySchemeBean;
        this.issueTypeScheme = issueTypeSchemeBean;
        this.notificationScheme = notificationSchemeBean;
        this.permissionScheme = permissionSchemeBean;
        this.workflowScheme = workflowSchemeData;
    }
}
