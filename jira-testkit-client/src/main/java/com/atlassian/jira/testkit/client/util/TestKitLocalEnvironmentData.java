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
    private static final String DEFAULT_PROTOCOL = "http";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "2990";
    private static final String DEFAULT_CONTEXT = "/jira";
    private static final String DEFAULT_EDITION = "standard";
    private static final String DEFAULT_XML_DATA_LOCATION = "./src/test/xml";
    private static final String DEFAULT_PROPERTIES_FILENAME = "localtest.properties";
    private static final String DEFAULT_CREATE_DUMMY_TENANT = "false";

    private static final String JIRA_PROTOCOL = "jira.protocol";
    private static final String JIRA_HOST = "jira.host";
    private static final String JIRA_PORT = "jira.port";
    private static final String JIRA_XML_DATA_LOCATION = "jira.xml.data.location";
    private static final String JIRA_CONTEXT = "jira.context";
    private static final String JIRA_TENANT = "jira.tenant";
    private static final String JIRA_EDITION = "jira.edition";
    private static final String CREATE_DUMMY_TENANT = "jira.create.dummy.tenant";
    private static final String TEST_SERVER_PROPERTIES = "test.server.properties";

    private final String contextPath;
    private final String tenant;
    private final boolean shouldCreateDummyTenant;
    private final URL baseUrl;
    private final File xmlDataLocation;
    private final String edition;
   
    public TestKitLocalEnvironmentData()
    {
        this(loadProperties(TEST_SERVER_PROPERTIES, DEFAULT_PROPERTIES_FILENAME), null);
    }

    public TestKitLocalEnvironmentData(String xmlDataLocation)
    {
        this(loadProperties(TEST_SERVER_PROPERTIES, DEFAULT_PROPERTIES_FILENAME), xmlDataLocation);
    }

    public TestKitLocalEnvironmentData(Properties properties, @Nullable String xmlDataLocationOverride)
    {
        super(properties);
        this.contextPath = getEnvironmentProperty(JIRA_CONTEXT, DEFAULT_CONTEXT);

        final String xmlDataLocationProperty = getEnvironmentProperty(JIRA_XML_DATA_LOCATION, DEFAULT_XML_DATA_LOCATION).trim();
        this.xmlDataLocation = resolveXmlDataLocation(xmlDataLocationOverride, xmlDataLocationProperty);

        final String protocol = getEnvironmentProperty(JIRA_PROTOCOL, DEFAULT_PROTOCOL);
        final String host = getEnvironmentProperty(JIRA_HOST, DEFAULT_HOST);
        final String port = getEnvironmentProperty(JIRA_PORT, DEFAULT_PORT);
        this.baseUrl = constructBaseUrl(protocol, host, port, contextPath);

        this.tenant = getEnvironmentProperty(JIRA_TENANT, null);
        this.shouldCreateDummyTenant = Boolean.parseBoolean(getEnvironmentProperty(CREATE_DUMMY_TENANT, DEFAULT_CREATE_DUMMY_TENANT));
        this.edition = getEnvironmentProperty(JIRA_EDITION, DEFAULT_EDITION);
    }

    private static URL constructBaseUrl(String protocol, String host, String port, String contextPath) {
        String baseUrl = protocol + "://" + host + ":" + port + contextPath;
        try
        {
            return new URL(baseUrl);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException("Malformed URL " + baseUrl);
        }
    }

    private static File resolveXmlDataLocation(String xmlDataLocationOverride, String xmlDataLocationProperty) {
        File unresolvedLocation = new File(xmlDataLocationOverride != null ? xmlDataLocationOverride : xmlDataLocationProperty);
        try
        {
            File canonicalLocation = unresolvedLocation.getCanonicalFile();
            if (!canonicalLocation.exists())
            {
                throw new RuntimeException(String.format("Cannot find xml data location: '%s'", unresolvedLocation));
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

            InputStream propStream = ClassLoaderUtils
					.getResourceAsStream(propertiesFileName, TestKitLocalEnvironmentData.class);
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

}