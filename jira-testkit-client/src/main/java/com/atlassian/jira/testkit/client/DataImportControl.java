package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.dump.FuncTestTimer;
import com.atlassian.jira.testkit.client.dump.TestInformationKit;
import com.atlassian.jira.testkit.client.util.TimeBombLicence;
import com.atlassian.jira.testkit.client.xmlbackup.XmlBackupCopier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.sun.jersey.api.client.UniformInterfaceException;
import org.apache.commons.io.FilenameUtils;

import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Collections;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Use this class from func/selenium/page-object tests that need to import data. Which is all of them.
 *
 * See DataImportBackdoor for the code this plugs into at the back-end.
 *
 * @since v5.0
 */
public class DataImportControl extends BackdoorControl<DataImportControl>
{
    public static final String FS = System.getProperty("file.separator");
    public static final String IMPORT = "import";

    private static final Iterable<Integer> REST_NOT_SETUP_ERROR_CODES = ImmutableList.of(
            Response.Status.NOT_FOUND.getStatusCode(),
            Response.Status.SERVICE_UNAVAILABLE.getStatusCode()
    );

    private JIRAEnvironmentData environmentData;
    private XmlBackupCopier xmlBackupCopier;
    /**
     * Evil but necessary static field used for caching the JIRA_HOME during func test runs.
     */
    private static final ThreadLocal<String> JIRA_HOME_DIR = new ThreadLocal<String>();

    public DataImportControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
        this.environmentData = environmentData;
        this.xmlBackupCopier = new XmlBackupCopier(environmentData.getBaseUrl());
    }

    private boolean looksLikeNotSetup(UniformInterfaceException interfaceException)
    {
        return Iterables.contains(REST_NOT_SETUP_ERROR_CODES, interfaceException.getResponse().getStatus());
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
        restoreDataFromResource("xml/testkit-blankprojects.xml", license);
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
        post(createResource().path("dataImport"), importBean, String.class);
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
        post(createResource().path("dataImport"), importBean, String.class);
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
        post(createResource().path("systemproperty").path("jira.dangermode").queryParam("value", "false"));
    }

    public void turnOnDangerMode()
    {
        post(createResource().path("systemproperty").path("jira.dangermode").queryParam("value","true"));
    }

    private String getJiraHomePath()
    {
        String jiraHomeDir = JIRA_HOME_DIR.get();
        if (jiraHomeDir == null)
        {
            jiraHomeDir = get(createResource().path("dataImport/jiraHomePath"));
            JIRA_HOME_DIR.set(jiraHomeDir);
        }
        return jiraHomeDir;
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
}
