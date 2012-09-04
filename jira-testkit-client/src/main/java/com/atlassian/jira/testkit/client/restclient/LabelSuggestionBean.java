package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @since v5.0
 */
@JsonIgnoreProperties( ignoreUnknown = true)
public class LabelSuggestionBean
{
    public String label;
}
