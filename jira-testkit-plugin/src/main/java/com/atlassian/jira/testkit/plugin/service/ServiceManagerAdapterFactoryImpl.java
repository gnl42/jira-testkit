package com.atlassian.jira.testkit.plugin.service;

import com.atlassian.jira.service.Jira63ServiceManagerAdapterImpl;
import com.atlassian.jira.service.ServiceManager;
import com.atlassian.jira.testkit.service.ServiceManagerAdapter;
import com.atlassian.pocketknife.api.version.JiraVersionService;
import com.atlassian.pocketknife.api.version.VersionKit;

public class ServiceManagerAdapterFactoryImpl implements ServiceManagerAdapterFactory
{
    private final JiraVersionService jiraVersionService;
    private final ServiceManager serviceManager;

    public ServiceManagerAdapterFactoryImpl(JiraVersionService jiraVersionService, ServiceManager serviceManager)
    {
        this.jiraVersionService = jiraVersionService;
        this.serviceManager = serviceManager;
    }

    @Override
    public boolean isAvailable()
    {
        return jiraVersionService.version().isGreaterThan(VersionKit.parse("6.2")); // cannot check 6.3 because of OD versions
    }

    @Override
    public ServiceManagerAdapter create()
    {
        if (isAvailable())
        {
            return new Jira63ServiceManagerAdapterImpl(serviceManager);
        }
        throw new UnsupportedOperationException("Service Manager not available until JIRA 6.3");
    }
}
