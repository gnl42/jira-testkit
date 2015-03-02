package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;

/**
 * JSON representation of an issue security scheme. The current state is not exhaustive of the scheme's state - add
 * attributes as you need them here.
 *
 * @since 6.3
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class IssueSecuritySchemeBean
{
    @JsonProperty
    public Long id;
    @JsonProperty
    public String name;

    @JsonProperty
    public String description;

    @JsonProperty
    public Collection<IssueSecurityType> levels;

    public IssueSecuritySchemeBean()
    {
    }

    public IssueSecuritySchemeBean(final Long id, final String name)
    {
        this.id = id;
        this.name = name;
    }
}
