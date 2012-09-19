package com.atlassian.jira.testkit.client;


import com.atlassian.jira.testkit.client.restclient.ProjectRoleClient;
import com.atlassian.jira.testkit.client.restclient.SearchClient;

/**
 * Top-level of Backdoor control hierarchy. Use components of this class to
 * manipulate the back-end in func tests without hitting the UI.
 *
 * @since v5.0
 */
public class Backdoor
{
    private final UsersAndGroupsControl usersAndGroups;
    private final IssuesControl issues;
    private final I18nControl i18n;
    private final DarkFeaturesControl darkFeatures;
    private final DataImportControl dataImport;
    private final PermissionsControl permissions;
    private final ApplicationPropertiesControl applicationProperties;
    private final SystemPropertiesControl systemProperties;
    private final ProjectControl project;
    private final PermissionSchemesControl permissionSchemes;
    private final MailServersControl mailServers;
    private final SearchRequestControl searchRequests;
    private final UserProfileControl userProfile;
    private final ServicesControl services;
    private final FieldConfigurationControl fieldConfigurationControl;
    private final IssueTypeControl issueTypeControl;
    private final SubtaskControl subtaskCotnrol;
    private final IssueLinkingControl issueLinkingControl;
    private final ScreensControl screensControl;
    private final ProjectRoleClient projectRoleClient;
    private final WebSudoControl webSudoControl;
    private final DashboardControl dashboardControl;
    private final BarrierControl barrierControl;
    private final IndexingControl indexingControl;
    private final PluginsControl plugins;
    private final GeneralConfigurationControl generalConfigurationControl;
    private final WorkflowsControl workflowsControl;
    private final WorkflowSchemesControl workflowSchemesControl;
    private final AdvancedSettingsControl advancedSettingsControl;
    private final SearchClient searchClient;
    private final CustomFieldsControl customFieldsControl;
	private final AttachmentsControl attachmentsControl;
	private final TimeTrackingControl timeTrackingControl;
	private final LicenseControl licenseControl;

    public Backdoor(JIRAEnvironmentData environmentData)
    {
        this.plugins = new PluginsControl(environmentData);
        this.usersAndGroups = new UsersAndGroupsControl(environmentData);
        this.issues = new IssuesControl(environmentData);
        this.i18n = new I18nControl(environmentData);
        this.darkFeatures = new DarkFeaturesControl(environmentData);
        this.permissions = new PermissionsControl(environmentData);
        this.applicationProperties = new ApplicationPropertiesControl(environmentData);
        this.systemProperties = new SystemPropertiesControl(environmentData);
        this.project = new ProjectControl(environmentData);
        this.permissionSchemes = new PermissionSchemesControl(environmentData);
        this.mailServers = new MailServersControl(environmentData);
        this.searchRequests = new SearchRequestControl(environmentData);
        this.userProfile = new UserProfileControl(environmentData);
        this.dataImport = new DataImportControl(environmentData);
        this.services = new ServicesControl(environmentData);
        this.fieldConfigurationControl = new FieldConfigurationControl(environmentData);
        this.issueTypeControl = new IssueTypeControl(environmentData);
        this.subtaskCotnrol = new SubtaskControl(environmentData);
        this.webSudoControl = new WebSudoControl(environmentData);
        this.issueLinkingControl = new IssueLinkingControl(environmentData);
        this.dashboardControl = new DashboardControl(environmentData);
        this.barrierControl = new BarrierControl(environmentData);
        this.indexingControl = new IndexingControl(environmentData);
        this.generalConfigurationControl = new GeneralConfigurationControl(environmentData);
        this.workflowsControl = new WorkflowsControl(environmentData);
        this.workflowSchemesControl = new WorkflowSchemesControl(environmentData);
        this.advancedSettingsControl = new AdvancedSettingsControl(environmentData);
        this.searchClient = new SearchClient(environmentData);
        this.customFieldsControl = new CustomFieldsControl(environmentData);
        this.screensControl = new ScreensControl(environmentData);
        this.projectRoleClient = new ProjectRoleClient(environmentData);
		this.attachmentsControl = new AttachmentsControl(environmentData);
		this.timeTrackingControl = new TimeTrackingControl(environmentData);
		this.licenseControl = new LicenseControl(environmentData);
    }

    public ScreensControl screens()
    {
        return screensControl;
    }

    public UsersAndGroupsControl usersAndGroups()
    {
        return usersAndGroups;
    }

    public IssuesControl issues()
    {
        return issues;
    }

    public I18nControl i18n()
    {
        return i18n;
    }

    public DarkFeaturesControl darkFeatures()
    {
        return darkFeatures;
    }

    public PluginsControl plugins()
    {
        return plugins;
    }

    public PermissionsControl permissions()
    {
        return permissions;
    }

    public ApplicationPropertiesControl applicationProperties()
    {
        return applicationProperties;
    }

    public SystemPropertiesControl systemProperties()
    {
        return systemProperties;
    }

    public ProjectControl project()
    {
        return project;
    }

    public PermissionSchemesControl permissionSchemes()
    {
        return permissionSchemes;
    }

    public ScreensControl screensControl()
    {
        return screensControl;
    }


    public MailServersControl mailServers()
    {
        return mailServers;
    }

    public SearchRequestControl searchRequests()
    {
        return searchRequests;
    }

    public UserProfileControl userProfile()
    {
        return userProfile;
    }

    public ServicesControl services()
    {
        return services;
    }

    public DataImportControl dataImport()
    {
        return dataImport;
    }

    public FieldConfigurationControl fieldConfiguration()
    {
        return fieldConfigurationControl;
    }

    public IssueTypeControl issueType()
    {
        return issueTypeControl;
    }

    public SubtaskControl subtask()
    {
        return subtaskCotnrol;
    }

    public WebSudoControl websudo()
    {
        return webSudoControl;
    }

    public DashboardControl dashboard()
    {
        return dashboardControl;
    }

    /**
     * @return a BarrierControl
     * @since v5.2
     */
    public BarrierControl barrier()
    {
        return barrierControl;
    }

    /**
     * @return an IndexingControl
     * @since v5.2
     */
    public IndexingControl indexing()
    {
        return indexingControl;
    }

    public GeneralConfigurationControl generalConfiguration()
    {
        return generalConfigurationControl;
    }

    public AdvancedSettingsControl advancedSettings()
    {
        return advancedSettingsControl;
    }

    public CustomFieldsControl customFields()
    {
        return customFieldsControl;
    }

    /**
     * Deprecated way, does not work well across different environments.
     *
     * @param xmlFileName xml file name
     * @deprecated use {@link #restoreDataFromResource(String, String)} instead
     * @see DataImportControl#restoreData(String,String)
     */
    @Deprecated
    public void restoreData(String xmlFileName, String license)
    {
        dataImport().restoreData(xmlFileName, license);
    }

    /**
     * Restore data from classpath resource.
     *
      * @param resourcePath name of the resource
     * @see DataImportControl#restoreDataFromResource(String,String)
     */
    public void restoreDataFromResource(String resourcePath, String license)
    {
        dataImport().restoreDataFromResource(resourcePath, license);
    }

    public void restoreDataFromResource(String resourcePath)
    {
        dataImport().restoreDataFromResource(resourcePath);
    }

    public void restoreBlankInstance(String license)
    {
        dataImport().restoreBlankInstance(license);
    }

    public void restoreBlankInstance()
    {
        dataImport().restoreBlankInstance();
    }

    public IssueLinkingControl issueLinking()
    {
        return issueLinkingControl;
    }

    public WorkflowsControl workflow()
    {
        return workflowsControl;
    }

    public WorkflowSchemesControl workflowSchemes()
    {
        return workflowSchemesControl;
    }

    public SearchClient search()
    {
        return searchClient;
    }

    public ProjectRoleClient projectRole()
    {
        return projectRoleClient;
    }

	public AttachmentsControl attachments() {
		return attachmentsControl;
	}

	public TimeTrackingControl timeTracking() {
		return timeTrackingControl;
	}

	public LicenseControl license() {
		return licenseControl;
    }
}
