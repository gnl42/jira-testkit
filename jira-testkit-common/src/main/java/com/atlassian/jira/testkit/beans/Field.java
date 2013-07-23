package com.atlassian.jira.testkit.beans;

import com.google.common.base.Function;
import org.codehaus.jackson.annotate.JsonProperty;

public class Field
{
    public static final Function<Field, String> GET_NAME = new Function<Field, String>()
    {
        @Override
        public String apply(final Field input)
        {
            return input.getName();
        }
    };

    @JsonProperty
    private String fieldId;

    @JsonProperty
    private String name;

    public Field() {}

    public Field(final String fieldId, final String name)
    {
        this.fieldId = fieldId;
        this.name = name;
    }

    public String getFieldId()
    {
        return fieldId;
    }

    public String getName()
    {
        return name;
    }
}
