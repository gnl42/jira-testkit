/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.util;

import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.jira.testkit.client.log.FuncTestOut;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;


public class TestKitLocalEnvironmentData extends AbstractEnvironmentData
{
    public static final DefaultConfiguration DEFAULT_CONFIGURATION = DefaultConfiguration.builder()
            .setProtocol("http")
            .setHost("localhost")
            .setPort("2990")
            .setContextPath("/jira")
            .setEdition("standard")
            .setXmlDataLocation("./src/test/xml")
            .setCreateDummyTenant("false").createDefaultConfiguration();

    private static final String JIRA_PROTOCOL = "jira.protocol";
    private static final String JIRA_HOST = "jira.host";
    private static final String JIRA_PORT = "jira.port";
    private static final String JIRA_XML_DATA_LOCATION = "jira.xml.data.location";
    private static final String JIRA_CONTEXT = "jira.context";
    private static final String JIRA_TENANT = "jira.tenant";
    private static final String JIRA_EDITION = "jira.edition";
    private static final String CREATE_DUMMY_TENANT = "jira.create.dummy.tenant";
    private static final String TEST_SERVER_PROPERTIES = "test.server.properties";
    private static final String DEFAULT_PROPERTIES_FILENAME = "localtest.properties";

    private final String contextPath;
    private final String tenant;
    private final boolean shouldCreateDummyTenant;
    private final URL baseUrl;
    private final File xmlDataLocation;
    private final String edition;

    public TestKitLocalEnvironmentData()
    {
        this(null);
    }

    public TestKitLocalEnvironmentData(final String xmlDataLocation)
    {
        this(loadProperties(TEST_SERVER_PROPERTIES, DEFAULT_PROPERTIES_FILENAME), xmlDataLocation);
    }

    public TestKitLocalEnvironmentData(final Properties properties, final @Nullable String xmlDataLocationOverride)
    {
        super(properties);
        final DefaultConfiguration def = getDefaultConfiguration();
        final String contextPath = getEnvironmentProperty(JIRA_CONTEXT, def.getContextPath(), true); // allow empty context path

        final String xmlDataLocationProperty = getEnvironmentProperty(JIRA_XML_DATA_LOCATION, def.getXmlDataLocation()).trim();
        this.xmlDataLocation = resolveXmlDataLocation(xmlDataLocationOverride, xmlDataLocationProperty);

        final String protocol = getEnvironmentProperty(JIRA_PROTOCOL, def.getProtocol());
        final String host = getEnvironmentProperty(JIRA_HOST, def.getHost());
        final String port = getEnvironmentProperty(JIRA_PORT, def.getPort());
        this.baseUrl = constructBaseUrl(protocol, host, port, contextPath);

        this.contextPath = contextPath;
        this.tenant = getEnvironmentProperty(JIRA_TENANT, null);
        this.shouldCreateDummyTenant = Boolean.parseBoolean(getEnvironmentProperty(CREATE_DUMMY_TENANT, def.getCreateDummyTenant()));
        this.edition = getEnvironmentProperty(JIRA_EDITION, def.getEdition());
    }

    private static URL constructBaseUrl(String protocol, String host, String port, String contextPath)
    {
        final String baseUrl = protocol + "://" + host + ":" + port + contextPath;
        try
        {
            return new URL(baseUrl);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException("Malformed URL " + baseUrl);
        }
    }

    private static File resolveXmlDataLocation(String xmlDataLocationOverride, String xmlDataLocationProperty)
    {
        final File unresolvedLocation = new File(xmlDataLocationOverride != null ? xmlDataLocationOverride : xmlDataLocationProperty);
        try
        {
            final File canonicalLocation = unresolvedLocation.getCanonicalFile();
            if (!canonicalLocation.exists())
            {
                // a hack for IDE - sometimes we're in sub-module, so lets try to find the file in parent directory
                final File locationInParentPath = new File(new File("..").getAbsoluteFile(), unresolvedLocation.getPath());
                if (!locationInParentPath.exists())
                {
                    throw new RuntimeException(String.format("Cannot find xml data location: '%s' or '%s'",
                            canonicalLocation, locationInParentPath.getAbsolutePath()));
                }
                return locationInParentPath;
            }
            return canonicalLocation;
        }
        catch (IOException e)
        {
            throw new RuntimeException("IOException trying to resolve file " + unresolvedLocation);
        }
    }

    public static Properties loadProperties(String key, String def)
    {
        Properties properties = new Properties();
        String propertiesFileName = "";
        try
        {
            propertiesFileName = System.getProperty(key, def);

            InputStream propStream = ClassLoaderUtils.getResourceAsStream(propertiesFileName, TestKitLocalEnvironmentData.class);
            if (propStream == null)
            {
                // The resource was not found on the classpath. Try opening as a file
                propStream = new FileInputStream(propertiesFileName);
            }
            try
            {
                properties.load(propStream);
                return properties;
            }
            finally
            {
                IOUtils.closeQuietly(propStream);
            }
        }
        catch (IOException e)
        {
            FuncTestOut.out.println("Cannot load file " + propertiesFileName + " from CLASSPATH.");
            e.printStackTrace(FuncTestOut.out);
            throw new IllegalArgumentException("Could not load properties file " + propertiesFileName + " from classpath");
        }
    }

    public String getContext()
    {
        return contextPath;
    }

    public String getTenant()
    {
        return tenant;
    }

    public boolean shouldCreateDummyTenant()
    {
        return shouldCreateDummyTenant;
    }

    public URL getBaseUrl()
    {
        return baseUrl;
    }

    public File getXMLDataLocation()
    {
        return xmlDataLocation;
    }

    public File getWorkingDirectory()
    {
        File file = new File(System.getProperty("java.io.tmpdir"), "jira_autotest");
        try
        {
            return file.getCanonicalFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Could not create JIRA home dir " + file);
        }
    }

    public File getJIRAHomeLocation()
    {
        return getWorkingDirectory();
    }

    public String getEdition()
    {
        return edition;
    }

    /**
     * Override this method if you REALLY need to override some default values (normally you want just set
     * properties in system properties or in file pointed out by DEFAULT_PROPERTIES_FILENAME).
     *
     * This method is called by constructor, so don't do any nasty things when override it!
     *
     * @return Default configuration for TestKitLocalEnvironmentData.
     */
    protected DefaultConfiguration getDefaultConfiguration()
    {
        return DEFAULT_CONFIGURATION;
    }

    protected static class DefaultConfiguration {
        private final String protocol;
        private final String host;
        private final String port;
        private final String contextPath;
        private final String edition;
        private final String xmlDataLocation;
        private final String createDummyTenant;

        public static DefaultConfigurationBuilder builder()
        {
            return new DefaultConfigurationBuilder();
        }

        public DefaultConfiguration(final String protocol, final String host, final String port,
                final String contextPath, final String edition, final String xmlDataLocation,
                final String createDummyTenant)
        {
            this.protocol = protocol;
            this.host = host;
            this.port = port;
            this.contextPath = contextPath;
            this.edition = edition;
            this.xmlDataLocation = xmlDataLocation;
            this.createDummyTenant = createDummyTenant;
        }

        public String getProtocol()
        {
            return protocol;
        }

        public String getHost()
        {
            return host;
        }

        public String getPort()
        {
            return port;
        }

        public String getContextPath()
        {
            return contextPath;
        }

        public String getEdition()
        {
            return edition;
        }

        public String getXmlDataLocation()
        {
            return xmlDataLocation;
        }

        public String getCreateDummyTenant()
        {
            return createDummyTenant;
        }

        public static class DefaultConfigurationBuilder
        {
            private String protocol;
            private String host;
            private String port;
            private String contextPath;
            private String edition;
            private String xmlDataLocation;
            private String createDummyTenant;

            public DefaultConfigurationBuilder copyFrom(TestKitLocalEnvironmentData.DefaultConfiguration cfg)
            {
                this.setContextPath(cfg.getContextPath())
                        .setCreateDummyTenant(cfg.getCreateDummyTenant())
                        .setEdition(cfg.getEdition())
                        .setHost(cfg.getHost())
                        .setPort(cfg.getPort())
                        .setProtocol(cfg.getProtocol())
                        .setXmlDataLocation(cfg.getXmlDataLocation());
                return this;
            }

            public DefaultConfigurationBuilder setProtocol(final String protocol)
            {
                this.protocol = protocol;
                return this;
            }

            public DefaultConfigurationBuilder setHost(final String host)
            {
                this.host = host;
                return this;
            }

            public DefaultConfigurationBuilder setPort(final String port)
            {
                this.port = port;
                return this;
            }

            public DefaultConfigurationBuilder setContextPath(final String contextPath)
            {
                this.contextPath = contextPath;
                return this;
            }

            public DefaultConfigurationBuilder setEdition(final String edition)
            {
                this.edition = edition;
                return this;
            }

            public DefaultConfigurationBuilder setXmlDataLocation(final String xmlDataLocation)
            {
                this.xmlDataLocation = xmlDataLocation;
                return this;
            }

            public DefaultConfigurationBuilder setCreateDummyTenant(final String createDummyTenant)
            {
                this.createDummyTenant = createDummyTenant;
                return this;
            }

            public TestKitLocalEnvironmentData.DefaultConfiguration createDefaultConfiguration()
            {
                return new TestKitLocalEnvironmentData.DefaultConfiguration(protocol, host, port, contextPath, edition,
                        xmlDataLocation, createDummyTenant);
            }
        }
    }
}