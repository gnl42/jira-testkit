package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * JAXB bean for AppLinks ApplicationType.
 *
 * @since v4.3
 */
public class AppTypeBean
{
    @JsonProperty
    private String i18nKey;

    @JsonProperty
    private String iconUrl;

    public AppTypeBean()
    {
    }

    public AppTypeBean(String i18nKey, String iconUrl)
    {
        this.i18nKey = i18nKey;
        this.iconUrl = iconUrl;
    }
}
