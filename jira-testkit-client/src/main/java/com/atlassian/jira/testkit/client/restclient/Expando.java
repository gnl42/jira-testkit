package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
* Expando attribute.
*
* @since v4.3
*/
public class Expando<T>
{
    public long size;
    @JsonProperty("start-index")
    public int start_index;
    @JsonProperty("end-index")
    public int end_index;
    @JsonProperty("max-results")
    public long max_results;
    public List<T> items;
}
