package com.atlassian.jira.testkit.beans;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public final class PermissionHolderBean
{
    @JsonProperty
    private String type;
    @JsonProperty
    private String parameter;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

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
