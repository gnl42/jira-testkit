package com.atlassian.jira.testkit.client;

/**
* REST bean for config-info resource
*
* @since 5.0
*/
public class ConfigInfo
{
    public String jiraHomePath;
    public boolean isSetUp;

    public ConfigInfo() {}

    public ConfigInfo(String home, boolean isSetUp)
    {
        this.jiraHomePath = home;
        this.isSetUp = isSetUp;
    }
}
