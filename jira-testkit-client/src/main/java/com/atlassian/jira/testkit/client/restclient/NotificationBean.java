package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties (ignoreUnknown = true)
public class NotificationBean
{
    @JsonProperty
    private Long id;
    @JsonProperty
    private String notificationType;
    @JsonProperty
    private String parameter;
    @JsonProperty
    private UserBean user;
    @JsonProperty
    private Group group;
    @JsonProperty
    private Field field;
    @JsonProperty
    private String emailAddress;
    @JsonProperty
    private ProjectRole projectRole;

    public NotificationBean()
    {
    }

    public NotificationBean(final Long id, final String notificationType, final String parameter, final UserBean user, final Group group, final Field field, final String emailAddress, final ProjectRole projectRole)
    {
        this.id = id;
        this.notificationType = notificationType;
        this.parameter = parameter;
        this.user = user;
        this.group = group;
        this.field = field;
        this.emailAddress = emailAddress;
        this.projectRole = projectRole;
    }

    public Long getId()
    {
        return id;
    }

    public String getNotificationType()
    {
        return notificationType;
    }

    public UserBean getUser()
    {
        return user;
    }

    public Group getGroup()
    {
        return group;
    }

    public Field getField()
    {
        return field;
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public ProjectRole getProjectRole()
    {
        return projectRole;
    }

    public String getParameter()
    {
        return parameter;
    }
}
