package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @since v5.0
 */
public class FieldOperation
{
    @JsonProperty
    private String oper;

    @JsonProperty
    private String[] val;

    public FieldOperation(String oper, String... v)
    {
        this.oper = oper;
        this.val = v;
    }
}
