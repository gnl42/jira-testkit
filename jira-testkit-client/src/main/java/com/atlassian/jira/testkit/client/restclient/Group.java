package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.net.URI;

import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Representation for group in the JIRA REST API.
 *
 * @since v4.3
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class Group
{
    @JsonProperty
    private String name;

    /**
     * @since v6.0
     */
    @JsonProperty
    private URI self;

    public Group()
    {
    }

    public Group(String name)
    {
        this.name = name;
    }

    public Group(final String name, final URI self)
    {
        this.name = name;
        this.self = self;
    }


    public String name()
    {
        return this.name;
    }

    public Group name(String name)
    {
        return new Group(name, self);
    }

    public URI self() {
        return self;
    }

    public Group self(URI self) {
        return new Group(name, self);
    }

    @Override
    public int hashCode()
    {
        return reflectionHashCode(this);
    }

    @SuppressWarnings ("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj)
    {
        return reflectionEquals(this, obj);
    }

    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }
}
