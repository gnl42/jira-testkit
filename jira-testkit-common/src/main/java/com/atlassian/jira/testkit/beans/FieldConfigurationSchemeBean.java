package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * JSON representation of a field configuration scheme. The current state is not exhaustive of the scheme's state - add
 * attributes as you need them here.
 *
 * @since 6.3
 */
public class FieldConfigurationSchemeBean
{
    @JsonProperty
    public final Long id;
    @JsonProperty
    public final String name;

    public FieldConfigurationSchemeBean(Long id, String name)
    {
        this.id = id;
        this.name = name;
    }
}
