/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.dump.FuncTestTimer;
import com.atlassian.jira.testkit.client.dump.TestInformationKit;
import com.atlassian.jira.testkit.client.util.TimeBombLicence;
import com.atlassian.jira.testkit.client.xmlbackup.XmlBackupCopier;
import com.atlassian.jira.testkit.util.ClasspathResources;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

/**
 * Use this class from func/selenium/page-object tests that need to import data. Which is all of them.
 *
 * See <code>com.atlassian.jira.testkit.plugin.DataImportBackdoor</code> in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class DataImportControl extends BackdoorControl<DataImportControl>
{
    private static final Logger log = LoggerFactory.getLogger(DataImportControl.class);

    public static final String FS = System.getProperty("file.separator");
    public static final String IMPORT = "import";

    public static final String TESTKIT_XML_PACKAGE = "testkit/xmlresources";
    public static final String TESTKIT_BLANKPROJECTS = "testkit-blankprojects-";
    public static final String TESTKIT_BLANKPROJECTS_XML = TESTKIT_XML_PACKAGE + "/" + TESTKIT_BLANKPROJECTS;
    public static final Pattern TESTKIT_BLANKPROJECTS_XML_PATTERN = Pattern.compile(".*" + TESTKIT_BLANKPROJECTS + "(\\d+)\\.xml");

    // Wait this long before deciding that the startup page really isn't going away...
    private static final long STARTUP_TIMEOUT_NANOS = TimeUnit.MINUTES.toNanos(3L);
    private static final long STARTUP_POLL_INTERVAL_MILLIS = 1000L;

    private static final String RETRY_AFTER = "Retry-After";
    private static final List<Integer> REST_NOT_SETUP_ERROR_CODES = ImmutableList.of(
            Response.Status.NOT_FOUND.getStatusCode(),
            Response.Status.SERVICE_UNAVAILABLE.getStatusCode()
    );


    /**
     * Evil but necessary static field used for caching the import configuration.
     */
    private static final ThreadLocal<ImportConfig> JIRA_CONFIG = new ThreadLocal<ImportConfig>();

    private static final ThreadLocal<List<Integer>> SUPPORTED_BUILD_NUMBERS = new ThreadLocal<List<Integer>>()
    {
        @Override
        protected List<Integer> initialValue()
        {
            final Iterable<String> matchingResources = ClasspathResources.getResources(TESTKIT_XML_PACKAGE, TESTKIT_BLANKPROJECTS_XML_PATTERN);
            return Ordering.natural().sortedCopy(transform(matchingResources, toSupportedBuildNumber()));
        }


        private Function<String,Integer> toSupportedBuildNumber()
        {
            return new Function<String, Integer>()
            {
                @Override
                public Integer apply(String name)
                {
                    final Matcher matcher = TESTKIT_BLANKPROJECTS_XML_PATTERN.matcher(name);
                    if (matcher.matches())
                    {
                        return Integer.parseInt(matcher.group(1));
                    }
                    else
                    {
                        throw new IllegalStateException("Unexpected blank XML resource name: " + name);
                    }
                }
            };
        }
    };

    private JIRAEnvironmentData environmentData;
    private XmlBackupCopier xmlBackupCopier;



    public DataImportControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
        this.environmentData = environmentData;
        this.xmlBackupCopier = new XmlBackupCopier(environmentData.getBaseUrl());
    }

    public boolean isSetUp()
    {
        try
        {
            final WebResource serverInfo = resourceRoot(environmentData.getBaseUrl().toExternalForm())
                    .path("rest").path("api").path(REST_VERSION).path("serverInfo");
            getWithStartupRetry(serverInfo, String.class);
            return true;
        }
        catch (UniformInterfaceException interfaceException)
        {
            if (looksLikeNotSetup(interfaceException))
            {
                return false;
            }
            throw new IllegalStateException("Unknown error when trying to check JIRA setup status", interfaceException);
        }
    }

    private static <T> T getWithStartupRetry(WebResource resource, Class<T> tClass) throws UniformInterfaceException
    {
        final long deadline = System.nanoTime() + STARTUP_TIMEOUT_NANOS;
        UniformInterfaceException lastEx = null;

        do
        {
            try
            {
                return resource.get(tClass);
            }
            catch (UniformInterfaceException uie)
            {
                if (!looksLikeStillStartingUp(uie))
                {
                    throw uie;
                }

                lastEx = uie;
                startupPhaseStall();
            }
        }
        while (System.nanoTime() < deadline);

        throw new IllegalStateException("Timed out while waiting for JIRA startup to complete", lastEx);
    }

    private static void startupPhaseStall()
    {
        try
        {
            Thread.sleep(STARTUP_POLL_INTERVAL_MILLIS);
        }
        catch (InterruptedException ie)
        {
            throw new RuntimeException("Interrupted while waiting for JIRA startup to complete", ie);
        }
    }

    // During the startup, we get 503's with a Retry-After header.
    private static boolean looksLikeStillStartingUp(UniformInterfaceException ex)
    {
        final ClientResponse response = ex.getResponse();
        return response.getStatus() == Response.Status.SERVICE_UNAVAILABLE.getStatusCode() &&
                response.getHeaders().containsKey(RETRY_AFTER);
    }

    private static boolean looksLikeNotSetup(UniformInterfaceException interfaceException)
    {
        return REST_NOT_SETUP_ERROR_CODES.contains(interfaceException.getResponse().getStatus());
    }

    /**
     * Restores the instance with the default XML file. A commercial license is used.
     */
    public void restoreBlankInstance()
    {
        restoreBlankInstance(TimeBombLicence.LICENCE_FOR_TESTING);
    }

    /**
     * Restores the instance with the default XML file, using the given license.
     *
     * @param license the licence to use
     */
    public void restoreBlankInstance(String license)
    {
        final String resource = findMatchingResource(getImportConfig().buildNumber);
        log.info("Restoring blank resource {}", resource);
        restoreDataFromResource(resource, license);
    }

    @VisibleForTesting
    protected String findMatchingResource(int buildNumber)
    {
        final int index = Ordering.natural().binarySearch(SUPPORTED_BUILD_NUMBERS.get(), buildNumber);
        if (index >= 0)
        {
            return blankResourceForBuildNumber(index);
        }
        else
        {
            if (index == -1)
            {
                throw new IllegalStateException("The build number " + buildNumber + " is not supported");
            }
            else
            {
                return blankResourceForBuildNumber(-index-2);
            }
        }
    }

    private String blankResourceForBuildNumber(int index)
    {
        return TESTKIT_BLANKPROJECTS_XML + SUPPORTED_BUILD_NUMBERS.get().get(index) + ".xml";
    }

    /**
     * Restores the instance with the specified XML file. A time bomb license is used.
     *
     * @param xmlFileName the name of the file to import
     * @deprecated this method relies on JIRAEnvironmentData to get the file to restore and thus makes assumptions
     * about the working directory the test process is running in. Use {@link #restoreDataFromResource(String, String)}
     * where possible and provide path to a classpath resource instead to stay independent from the environment
     */
    @Deprecated
    public void restoreData(String xmlFileName)
    {
        restoreData(xmlFileName, TimeBombLicence.LICENCE_FOR_TESTING);
    }

    /**
     * Restores the instance with the specified XML file. A commercial license is used.
     *
     * @param xmlFileName the name of the file to import
     * @param license JIRA licence key
     * @deprecated this method relies on JIRAEnvironmentData to get the file to restore and thus makes assumptions
     * about the working directory the test process is running in. Use {@link #restoreDataFromResource(String, String)}
     * where possible and provide path to a classpath resource instead to stay independent from the environment
     */
    @Deprecated
    public void restoreData(String xmlFileName, String license)
    {
        final FuncTestTimer timer = TestInformationKit.pullTimer("XML Restore");

        // 1. Copy the import file from the test resource directory to the server's import XML directory
        // (i.e. we ain't using no secret sauce).
        // Done at the 'front-end' and not the back-end because of the desire to reuse XmlBackupCopier logic...
        String sourcePath = environmentData.getXMLDataLocation().getAbsolutePath() + FS + xmlFileName;
        String jiraImportPath = getJiraHomePath() + FS + IMPORT + FS + new File(xmlFileName).getName();
        boolean baseUrlReplaced = xmlBackupCopier.copyXmlBackupTo(sourcePath, jiraImportPath);

        DataImportBean importBean = new DataImportBean();
        importBean.filePath = jiraImportPath;
        importBean.licenseString = license;
        importBean.useDefaultPaths = false;
        importBean.quickImport = true;
        importBean.isSetup = false;

        if (baseUrlReplaced)
        {
            importBean.baseUrl = environmentData.getBaseUrl().toString();
        }
        createResource().path("dataImport").post(String.class, importBean);
        timer.end();
    }

    /**
     * @param resourcePath path to the class path resource containing the file to restore
     */
    public void restoreDataFromResource(String resourcePath)
    {
        restoreDataFromResource(resourcePath, TimeBombLicence.LICENCE_FOR_TESTING);
    }

    /**
     * Restores the instance with the specified XML resource on the classpath (as it should to stay independent from
     * the filesystem). A commercial license is used.
     *
     * This will also try to match a resource with prefix 'xml/'. This is to keep compatibility with the old way
     * of restoring that searched in the 'xml' directory by default. That means that if your classpath resource
     * that you want to restore is in an 'xml' package, this method will work even if you just provide the bare
     * resource name.
     *
     * @param resourcePath path to the class path resource containing the file to restore
     * @param license the licence
     */
    public void restoreDataFromResource(String resourcePath, String license)
    {
        final FuncTestTimer timer = TestInformationKit.pullTimer("XML Restore");
        final String targetPath = getImportTargetPath(resourcePath);
        boolean baseUrlReplaced = xmlBackupCopier.copyXmlBackupFromClassPathTo(resourcePath, targetPath,
                Collections.<Pattern, String>emptyMap());
        DataImportBean importBean = new DataImportBean();
        importBean.filePath = targetPath;
        importBean.licenseString = license;
        importBean.useDefaultPaths = false;
        importBean.quickImport = true;
        importBean.isSetup = false;
        if (baseUrlReplaced)
        {
            importBean.baseUrl = environmentData.getBaseUrl().toString();
        }
        createResource().path("dataImport").post(String.class, importBean);
        timer.end();
    }

    private String getImportTargetPath(String resourcePath)
    {
        final String importFileName = importFileNameFor(checkNotNull(resourcePath));
        return getJiraHomePath() + FS + IMPORT + FS + importFileName;
    }

    private String getImportSourcePath(String resourcePath)
    {
        return RestoreDataResources.getResourceUrl(resourcePath).getFile();
    }

    private String importFileNameFor(String resourcePath)
    {
        String extension = FilenameUtils.getExtension(resourcePath);
        return extension != null ? resourcePath.hashCode() + "." + extension : resourcePath.hashCode() + ".xml";
    }

    public void turnOffDangerMode()
    {
        createResource().path("systemproperty").path("jira.dangermode").queryParam("value", "false").post();
    }

    public void turnOnDangerMode()
    {
        createResource().path("systemproperty").path("jira.dangermode").queryParam("value","true").post();
    }

    private String getJiraHomePath()
    {
        return getImportConfig().jiraHome;
    }

    private ImportConfig getImportConfig()
    {
        ImportConfig config = JIRA_CONFIG.get();
        if (config == null)
        {
            final WebResource resource = createResource().path("dataImport/importConfig");
            config = getWithStartupRetry(resource, ImportConfig.class);
            JIRA_CONFIG.set(config);
        }
        return config;
    }

    static class DataImportBean
    {
        public String filePath;
        public String licenseString;
        public boolean quickImport;
        public boolean useDefaultPaths;
        public boolean isSetup;
        public String baseUrl;
    }

    static class ImportConfig
    {
        public String jiraHome;
        public int buildNumber;

    }
}
