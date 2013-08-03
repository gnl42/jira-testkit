package com.atlassian.jira.testkit.beans;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;

import java.util.List;

/**
 * @since v6.0.36
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class CustomFieldOption
{
    public static Function<CustomFieldOption, String> getNameFunction()
    {
        return new Function<CustomFieldOption, String>()
        {
            @Override
            public String apply(final CustomFieldOption input)
            {
                return input.getName();
            }
        };
    }

    private Long id;
    private String name;
    private List<CustomFieldOption> children = Lists.newArrayList();

    @JsonProperty
    public List<CustomFieldOption> getChildren()
    {
        return children;
    }

    public void setChildren(final List<CustomFieldOption> children)
    {
        this.children = children;
    }

    @JsonProperty
    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
