package com.atlassian.jira.testkit.beans;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public class FieldBean
{

    private String id;
    private String name;
    private boolean custom;

    @Deprecated
    public FieldBean() {}

    private FieldBean(String id, String name, boolean custom)
    {
        this.id = id;
        this.name = name;
        this.custom = custom;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean getCustom()
    {
        return custom;
    }

    public void setCustom(boolean custom)
    {
        this.custom = custom;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static Builder builder(FieldBean data)
    {
        return new Builder(data);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        FieldBean that = (FieldBean) o;

        return Objects.equal(this.id, that.id) &&
                Objects.equal(this.name, that.name) &&
                Objects.equal(this.custom, that.custom);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(id, name, custom);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("custom", custom)
                .toString();
    }

    public static final class Builder
    {

        private String id;
        private String name;
        private boolean custom;

        private Builder() {}

        private Builder(FieldBean initialData)
        {

            this.id = initialData.id;
            this.name = initialData.name;
            this.custom = initialData.custom;
        }

        public Builder setId(String id)
        {
            this.id = id;
            return this;
        }

        public Builder setName(String name)
        {
            this.name = name;
            return this;
        }

        public Builder setCustom(boolean custom)
        {
            this.custom = custom;
            return this;
        }

        public FieldBean build()
        {
            return new FieldBean(id, name, custom);
        }
    }
}
