package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.dump.FuncTestTimer;
import com.atlassian.jira.testkit.client.dump.TestInformationKit;
import com.atlassian.jira.testkit.client.util.TimeBombLicence;
import com.atlassian.jira.testkit.client.xmlbackup.XmlBackupCopier;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.sun.jersey.api.client.UniformInterfaceException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

/**
 * Use this class from func/selenium/page-object tests that need to import data. Which is all of them.
 *
 * See {@link com.atlassian.jira.testkit.plugin.DataImportBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class DataImportControl extends BackdoorControl<DataImportControl>
{
    private static final Logger log = LoggerFactory.getLogger(DataImportControl.class);

    public static final String FS = System.getProperty("file.separator");
    public static final String IMPORT = "import";
    public static final String TESTKIT_BLANKPROJECTS = "testkit-blankprojects-";
    public static final String TESTKIT_BLANKPROJECTS_XML = "xml/testkit-blankprojects-";

    private static final Iterable<Integer> REST_NOT_SETUP_ERROR_CODES = ImmutableList.of(
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
            final Iterable<File> files = ImmutableList.copyOf(xmlDir().listFiles((FileFilter) new PrefixFileFilter(TESTKIT_BLANKPROJECTS)));
            return Ordering.natural().sortedCopy(transform(files, toSupportedBuildNumber()));
        }

        private File xmlDir()
        {
            final URL xmlDirUrl = DataImportControl.class.getClassLoader().getResource("xml");
            if (xmlDirUrl == null)
            {
                throw new IllegalStateException("Could not find the XML dir resource");
            }
            final File xmlDir = new File(xmlDirUrl.getFile());
            if (!xmlDir.isDirectory())
            {
                throw new IllegalStateException("XML resource is not a dir");
            }
            return xmlDir;
        }

        private Function<File,Integer> toSupportedBuildNumber()
        {
            final Pattern pattern = Pattern.compile("testkit-blankprojects-(\\d+)\\.xml");
            return new Function<File, Integer>()
            {
                @Override
                public Integer apply(File file)
                {
                    final Matcher matcher = pattern.matcher(file.getName());
                    if (matcher.matches())
                    {
                        return Integer.parseInt(matcher.group(1));
                    }
                    else
                    {
                        throw new IllegalStateException("Unexpected blank XML resource file name: " + file.getName());
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
            get(resourceRoot(environmentData.getBaseUrl().toExternalForm()).path("rest").path("api").path(REST_VERSION).path("serverInfo"));
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

    private boolean looksLikeNotSetup(UniformInterfaceException interfaceException)
    {
        return Iterables.contains(REST_NOT_SETUP_ERROR_CODES, interfaceException.getResponse().getStatus());
    }

    /**
     * Restores the instance with the default XML file. A commercial license is used.
     */
    public void restoreBlankInstance()
    {
        restoreBlankInstance(TimeBombLicence.LICENCE_FOR_TESTING);
    }

    /**
     * Restores the instance with the default XML file. A commercial license is used.
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
     * <p/>
     * Restores the instance with the specified XML resource on the classpath (as it should to stay independent from
     * the filesystem). A commercial license is used.
     *
     * <p/>
     * This will also try to match a resource with prefix 'xml/'. This is to keep compatibility with the old way
     * of restoring that searched in the 'xml' directory by default. That means that if your classpath resource
     * that you want to restore is in an 'xml' package, this method will work even if you just provide the bare
     * resource name.
     *
     * @param resourcePath path to the class path resource containing the file to restore
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
            config = createResource().path("dataImport/importConfig").get(ImportConfig.class);
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
