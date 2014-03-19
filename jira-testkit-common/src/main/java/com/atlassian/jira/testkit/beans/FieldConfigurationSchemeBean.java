package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * JSON representation of a field configuration scheme. The current state is not exhaustive of the scheme's state - add
 * attributes as you need them here.
 *
 * @since 6.3
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class FieldConfigurationSchemeBean
{
    @JsonProperty
    public Long id;
    @JsonProperty
    public String name;

    public FieldConfigurationSchemeBean()
    {
    }

    public FieldConfigurationSchemeBean(Long id, String name)
    {
        this.id = id;
        this.name = name;
    }
}
