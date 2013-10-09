/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.util;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;

import java.util.Properties;

public abstract class AbstractEnvironmentData implements JIRAEnvironmentData
{
    private final String releaseInfo;
    private final Properties properties;

    protected AbstractEnvironmentData(Properties properties)
    {
        this.properties = properties;
        releaseInfo = properties.getProperty("jira.release.info");
    }

    public boolean isBundledPluginsOnly()
    {
        return getEdition().toLowerCase().indexOf("bundledplugins") != -1;
    }

    public boolean isTpmLdapTests()
    {
        return getEdition().equalsIgnoreCase("tpm_ldap");
    }

    @Override
    public boolean isBlame()
    {
        return getEdition().equalsIgnoreCase("blame");
    }

    public boolean isAllTests()
    {
        return getEdition().toLowerCase().indexOf("all") != -1;
    }

    protected abstract String getEdition();

    public String getReleaseInfo()
    {
        return releaseInfo;
    }

    public String getProperty(String key)
    {
        if (properties != null)
        {
            return properties.getProperty(key);
        }
        return null;
    }

    protected String getEnvironmentProperty(final String key, final String defaultValue)
    {
        String property = System.getProperty(key);
        if(StringUtils.isEmpty(property))
        {
            if (properties != null)
            {
                property = properties.getProperty(key, defaultValue);
            }
            else
            {
                property = defaultValue;
            }
        }
        return property;
    }

    public boolean isSingleNamedTest()
    {
        return getSingleTestClassName() != null;
    }

    private String getSingleTestClassName()
    {
        final String singleTestclass = getEnvironmentProperty("jira.functest.single.testclass", "");
        return StringUtils.isEmpty(singleTestclass) ? null : singleTestclass;
    }

    @Override
    public String getTenant()
    {
        return getEnvironmentProperty("jira.tenant", null);
    }

    @Override
    public boolean shouldCreateDummyTenant()
    {
        return Boolean.parseBoolean(getEnvironmentProperty("jira.create.dummy.tenant", "false"));
    }

    public Class<? extends TestCase> getSingleTestClass()
    {
        {
            try
            {
                return (Class<? extends TestCase>) Class.forName(getSingleTestClassName());
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Could not construct single testclass" + getSingleTestClassName(), e);
            }
        }
    }

}
