package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.beans.ProjectSchemesBean;
import com.atlassian.jira.testkit.client.Backdoor;
import com.google.common.base.CaseFormat;

import java.util.regex.Pattern;

public enum ProjectUpdateField
{
    KEY,
    DESCRIPTION,
    LEAD
            {
                @Override
                protected String getFrom(Project project, Backdoor backdoor)
                {
                    return project.lead.name;
                }
            },
    URL,
    ASSIGNEE_TYPE,
    AVATAR_ID
            {

                private final Pattern avatarIdPattern = Pattern.compile("avatarId=(\\d+)");

                @Override
                protected String getFrom(Project project, Backdoor backdoor)
                {
                    // /secure/projectavatar?size=medium&pid=10010&avatarId=10011
                    java.util.regex.Matcher matcher = avatarIdPattern.matcher(project.avatarUrls.values().iterator().next());
                    matcher.find();
                    return matcher.group(1);
                }
            },
    ISSUE_SECURITY_SCHEME
            {
                @Override
                protected String getFrom(Project project, Backdoor backdoor)
                {
                    ProjectSchemesBean schemes = backdoor.project().getSchemes(project.id);
                    return schemes.issueSecurityScheme != null ? schemes.issueSecurityScheme.id.toString() : null;
                }
            },
    PERMISSION_SCHEME
            {
                @Override
                protected String getFrom(Project project, Backdoor backdoor)
                {
                    ProjectSchemesBean schemes = backdoor.project().getSchemes(project.id);
                    return schemes.permissionScheme != null ? schemes.permissionScheme.id.toString() : null;

                }
            },
    NOTIFICATION_SCHEME
            {
                @Override
                protected String getFrom(Project project, Backdoor backdoor)
                {
                    ProjectSchemesBean schemes = backdoor.project().getSchemes(project.id);
                    return schemes.notificationScheme != null ? schemes.notificationScheme.id.toString() : null;

                }
            },
    CATEGORY_ID
            {
                @Override
                protected String getFrom(Project project, Backdoor backdoor)
                {
                    return project.projectCategory != null ? project.projectCategory.id.toString() : null;
                }
            };

    public String jsonFieldName()
    {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
    }

    protected String getFrom(Project project, Backdoor backdoor)
    {
        try
        {
            Object field = Project.class.getField(jsonFieldName()).get(project);
            return field != null ? field.toString() : null;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(name() + " should implement getFrom method explicitly");
        }
    }
}
