package com.atlassian.jira.testkit.beans;

import com.atlassian.jira.util.Named;
import com.google.common.base.Objects;

public class CustomFieldResponse implements Named
{
    public CustomFieldResponse(String name, String id, String type)
    {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public CustomFieldResponse()
    {
    }

    public String name;
    public String id;
    public String type;

    @Override
    public boolean equals(final Object o)
    {
        if (o instanceof CustomFieldResponse)
        {
            return Objects.equal(this.name, ((CustomFieldResponse) o).name)
                    && Objects.equal(this.id, ((CustomFieldResponse) o).id)
                    && Objects.equal(this.type, ((CustomFieldResponse) o).type);
        }
        return false;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("name", this.name).add("id", this.id).add("type", this.type).toString();
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(name, id, type);
    }

    @Override
    public String getName()
    {
        return name;
    }
}