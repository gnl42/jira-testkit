package com.atlassian.testkit.tests.mock;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import junit.framework.TestCase;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The first ever {@link JIRAEnvironmentData} implementation that does not throw non-informative exceptions from
 * constructor because some stupid file does not exist. PROGRESS, BABY!
 *
 * @since v5.2
 */
public class MockEnvironmentData implements JIRAEnvironmentData
{
    // feel free to implement currently unsupported methods if your tests need it

    @Override
    public String getContext()
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getTenant()
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean shouldCreateDummyTenant()
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public URL getBaseUrl()
    {
        try
        {
            return new URL("http://localhost:8090/jira");
        }
        catch (MalformedURLException e)
        {
            throw new AssertionError("No, not really", e);
        }
    }

    @Override
    public File getXMLDataLocation() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public File getWorkingDirectory() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public File getJIRAHomeLocation() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getReleaseInfo() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isBundledPluginsOnly() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isAllTests() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isSingleNamedTest() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Class<? extends TestCase> getSingleTestClass() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isTpmLdapTests() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isBlame() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getProperty(String key) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
