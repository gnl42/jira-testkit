package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

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

    public IssueSecuritySchemeBean()
    {
    }

    public IssueSecuritySchemeBean(final Long id, final String name)
    {
        this.id = id;
        this.name = name;
    }
}
