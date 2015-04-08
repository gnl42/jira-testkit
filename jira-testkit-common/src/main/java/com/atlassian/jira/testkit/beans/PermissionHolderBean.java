package com.atlassian.jira.testkit.beans;

import com.atlassian.jira.testkit.client.restclient.Group;
import com.atlassian.jira.testkit.client.restclient.ProjectRole;
import com.atlassian.jira.testkit.client.restclient.UserBean;
import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public final class PermissionHolderBean
{
    @JsonProperty
    private String type;
    @JsonProperty
    private String parameter;

    @JsonProperty
    private UserBean user;
    @JsonProperty
    private Group group;
    @JsonProperty
    private Field field;
    @JsonProperty
    private ProjectRole projectRole;

    public String getType()
    {
        return type;
    }

    public PermissionHolderBean setType(final String type)
    {
        this.type = type;
        return this;
    }

    public String getParameter()
    {
        return parameter;
    }

    public PermissionHolderBean setParameter(final String parameter)
    {
        this.parameter = parameter;
        return this;
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

    public ProjectRole getProjectRole()
    {
        return projectRole;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("type", type)
                .add("parameter", parameter)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        PermissionHolderBean that = (PermissionHolderBean) o;

        return Objects.equal(this.type, that.type) &&
                Objects.equal(this.parameter, that.parameter);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(type, parameter);
    }
}
