package com.atlassian.jira.testkit.beans;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.net.URI;
import java.util.Collection;
import java.util.List;

@JsonIgnoreProperties (ignoreUnknown = true)
public final class PermissionSchemeBean
{
    @JsonProperty
    public Long id;
    @JsonProperty
    public URI self;
    @JsonProperty
    public String name;
    @JsonProperty
    public String description;
    @JsonProperty
    public List<PermissionGrantBean> permissions;

    public PermissionSchemeBean()
    {
    }

    public PermissionSchemeBean(final Long id, final String name)
    {
        this.name = name;
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public URI getSelf()
    {
        return self;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public List<PermissionGrantBean> getPermissions()
    {
        return permissions;
    }

    public PermissionSchemeBean setId(final Long id)
    {
        this.id = id;
        return this;
    }

    public PermissionSchemeBean setName(final String name)
    {
        this.name = name;
        return this;
    }

    public PermissionSchemeBean setDescription(final String description)
    {
        this.description = description;
        return this;
    }

    public PermissionSchemeBean setPermissions(final List<PermissionGrantBean> permissions)
    {
        this.permissions = permissions;
        return this;
    }

    public PermissionSchemeBean addPermissions(final Collection<PermissionGrantBean> permissions)
    {
        initPermissions();
        this.permissions.addAll(permissions);
        return this;
    }

    public PermissionSchemeBean addPermission(final PermissionGrantBean permission)
    {
        initPermissions();
        this.permissions.add(permission);
        return this;
    }

    private void initPermissions()
    {
        if (this.permissions == null) {
            this.permissions = Lists.newArrayList();
        }
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("self", self)
                .add("name", name)
                .add("description", description)
                .add("permissions", permissions)
                .toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionSchemeBean that = (PermissionSchemeBean) o;

        return Objects.equal(this.name, that.name) &&
                Objects.equal(this.description, that.description) &&
                Objects.equal(this.permissions, that.permissions);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(name, description, permissions);
    }
}
