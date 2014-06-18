package com.atlassian.jira.testkit.service;

public interface ServiceManagerAdapter
{
    /**
     * @since JIRA 6.3
     */
    void runNow(Long id) throws Exception;
}
