package com.atlassian.jira.testkit.beans;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.net.URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class PermissionGrantBean
{
    @JsonProperty
    private Long id;
    @JsonProperty
    private URI self;
    @JsonProperty
    private PermissionHolderBean holder;
    @JsonProperty
    private String permission;

    public Long getId()
    {
        return id;
    }

    public URI getSelf()
    {
        return self;
    }

    public PermissionHolderBean getHolder()
    {
        return holder;
    }

    public PermissionGrantBean setHolder(final PermissionHolderBean holder)
    {
        this.holder = holder;
        return this;
    }

    public String getPermission()
    {
        return permission;
    }

    public PermissionGrantBean setPermission(final String permission)
    {
        this.permission = permission;
        return this;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("self", self)
                .add("holder", holder)
                .add("permission", permission)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionGrantBean that = (PermissionGrantBean) o;

        return Objects.equal(this.holder, that.holder) &&
                Objects.equal(this.permission, that.permission);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(holder, permission);
    }
}
