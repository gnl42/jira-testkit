package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.GenericType;

import java.util.List;
import java.util.Map;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate or query Services.
 * <p/>
 *
 * See {@link com.atlassian.jira.testkit.plugin.ServiceBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class ServicesControl extends BackdoorControl<ServicesControl>
{
    public ServicesControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public List<ServiceBean> getServices()
    {
        return get(createResource().path("services"), new GenericType<List<ServiceBean>>() {});
    }

    public ServiceBean getService(long id)
    {
        return get(createResource().path("services/" + id), ServiceBean.class);
    }

    public static class ServiceBean
    {
        public Long id;
        public String name;
        public String serviceClass;
        public boolean usable;
        public Map<String, String> params;
    }
}
