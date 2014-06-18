package com.atlassian.jira.service;

import com.atlassian.jira.testkit.service.ServiceManagerAdapter;

public class Jira63ServiceManagerAdapterImpl implements ServiceManagerAdapter
{
    private final ServiceManager delegate;

    public Jira63ServiceManagerAdapterImpl(ServiceManager delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public void runNow(Long serviceId) throws Exception
    {
        delegate.runNow(serviceId);
    }
}
