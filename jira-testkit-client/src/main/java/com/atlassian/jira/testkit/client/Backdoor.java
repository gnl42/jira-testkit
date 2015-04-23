package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.restclient.AnalyticsClient;
import com.atlassian.jira.testkit.client.restclient.ProjectRoleClient;
import com.atlassian.jira.testkit.client.restclient.SearchClient;

import javax.annotation.Nonnull;

/**
 * Top-level of Backdoor control hierarchy. Use components of this class to
 * manipulate the back-end in func tests without hitting the UI.
 *
 * @since v5.0
 */
@SuppressWarnings("unused")
public class Backdoor
{
    private final AdvancedSettingsControl advancedSettingsControl;
    private final AnalyticsClient analyticsClient;
    private final ApplicationLinkControl applicationLinkControl;
    private final ApplicationPropertiesControl applicationProperties;
    private final AttachmentsControl attachmentsControl;
    private final AuditingControl auditingControl;
    private final CustomFieldsControl customFieldsControl;
    private final DarkFeaturesControl darkFeatures;
    private final DashboardControl dashboardControl;
    private final DataImportControl dataImport;
    private final EntityLinkControl entityLinkControl;
    private final EntityPropertyControl entityPropertyControl;
    private final FieldConfigurationControl fieldConfigurationControl;
    private final FieldConfigurationSchemesControl fieldConfigurationSchemes;
    private final GeneralConfigurationControl generalConfigurationControl;
    private final I18nControl i18n;
    private final IndexingControl indexingControl;
    private final IssueLinkingControl issueLinkingControl;
    private final IssuesControl issues;
    private final IssueSecuritySchemesControl issueSecuritySchemes;
    private final IssueTypeControl issueTypeControl;
    private final LicenseControl licenseControl;
    private final LogControl logControl;
    private final MailServersControl mailServers;
    private final NotificationSchemesControl notificationSchemes;
    private final PermissionSchemesControl permissionSchemes;
    private final PermissionsControl permissions;
    private final PluginsControl plugins;
    private final PriorityControl priorityControl;
    private final ProjectControl project;
    private final ProjectRoleClient projectRoleClient;
    private final RawRestApiControl rawRestApiControl;
    private final ResolutionControl resolutionControl;
    private final ScreensControl screensControl;
    private final SearchClient searchClient;
    private final SearchRequestControl searchRequests;
    private final ServicesControl services;
    private final StatusControl statusControl;
    private final SubtaskControl subtaskControl;
    private final SystemPropertiesControl systemProperties;
    private final TimeTrackingControl timeTrackingControl;
    private final LicenseControl licenseControl;
    private final LogControl logControl;
    private final IndexingControl indexingControl;
    private final ApplicationLinkControl applicationLinkControl;
    private final RawRestApiControl rawRestApiControl;
    private final AuditingControl auditingControl;
    private final IssueSecuritySchemesControl issueSecuritySchemes;
    private final AnalyticsClient analyticsClient;
    private final UserHistoryControl userHistoryControl;
    private final UserProfileControl userProfile;
    private final UsersAndGroupsControl usersAndGroups;
    private final WebSudoControl webSudoControl;
    private final WorkflowSchemesControl workflowSchemesControl;
    private final WorkflowsControl workflowsControl;

    public Backdoor(JIRAEnvironmentData environmentData)
    {
        this.advancedSettingsControl = new AdvancedSettingsControl(environmentData);
        this.analyticsClient = new AnalyticsClient(environmentData);
        this.applicationLinkControl = new ApplicationLinkControl(environmentData);
        this.applicationProperties = new ApplicationPropertiesControl(environmentData);
        this.attachmentsControl = new AttachmentsControl(environmentData);
        this.auditingControl = new AuditingControl(environmentData);
        this.customFieldsControl = new CustomFieldsControl(environmentData);
        this.darkFeatures = new DarkFeaturesControl(environmentData);
        this.dashboardControl = new DashboardControl(environmentData);
        this.dataImport = new DataImportControl(environmentData);
        this.entityLinkControl = new EntityLinkControl(environmentData);
        this.entityPropertyControl = new EntityPropertyControl(environmentData);
        this.fieldConfigurationControl = new FieldConfigurationControl(environmentData);
        this.fieldConfigurationSchemes = new FieldConfigurationSchemesControl(environmentData);
        this.generalConfigurationControl = new GeneralConfigurationControl(environmentData);
        this.i18n = new I18nControl(environmentData);
        this.indexingControl = new IndexingControl(environmentData);
        this.issueLinkingControl = new IssueLinkingControl(environmentData);
        this.issueSecuritySchemes = new IssueSecuritySchemesControl(environmentData);
        this.issueTypeControl = new IssueTypeControl(environmentData);
        this.issues = new IssuesControl(environmentData, issueTypeControl);
        this.licenseControl = new LicenseControl(environmentData);
        this.logControl = new LogControl(environmentData);
        this.mailServers = new MailServersControl(environmentData);
        this.notificationSchemes = new NotificationSchemesControl(environmentData);
        this.permissions = new PermissionsControl(environmentData);
        this.permissionSchemes = new PermissionSchemesControl(environmentData);
        this.plugins = new PluginsControl(environmentData);
        this.priorityControl = new PriorityControl(environmentData);
        this.project = new ProjectControl(environmentData);
        this.projectRoleClient = new ProjectRoleClient(environmentData);
        this.rawRestApiControl = new RawRestApiControl(environmentData);
        this.statusControl = new StatusControl(environmentData);
        this.resolutionControl = new ResolutionControl(environmentData);
        this.screensControl = new ScreensControl(environmentData);
        this.searchClient = new SearchClient(environmentData);
        this.searchRequests = new SearchRequestControl(environmentData);
        this.services = new ServicesControl(environmentData);
        this.statusControl = new StatusControl(environmentData);
        this.subtaskControl = new SubtaskControl(environmentData);
        this.systemProperties = new SystemPropertiesControl(environmentData);
        this.timeTrackingControl = new TimeTrackingControl(environmentData);
        this.userHistoryControl = new UserHistoryControl(environmentData);
        this.userProfile = new UserProfileControl(environmentData);
        this.usersAndGroups = new UsersAndGroupsControl(environmentData);
        this.webSudoControl = new WebSudoControl(environmentData);
        this.workflowSchemesControl = new WorkflowSchemesControl(environmentData);
        this.workflowsControl = new WorkflowsControl(environmentData);
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

    public NotificationSchemesControl notificationSchemes()
    {
        return notificationSchemes;
    }

    public IssueSecuritySchemesControl issueSecuritySchemes()
    {
        return issueSecuritySchemes;
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
    
    public StatusControl status()
    {
        return statusControl;
    }
    
    public ResolutionControl resolutions()
    {
        return resolutionControl;
    }

    public PriorityControl priorities()
    {
        return priorityControl;
    }

    public SubtaskControl subtask()
    {
        return subtaskControl;
    }

    public WebSudoControl websudo()
    {
        return webSudoControl;
    }

    public DashboardControl dashboard()
    {
        return dashboardControl;
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
     * @param license the licence
     * @deprecated use {@link #restoreDataFromResource(String, String)} instead
     * @see DataImportControl#restoreData(String,String)
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public void restoreData(String xmlFileName, String license)
    {
        dataImport().restoreData(xmlFileName, license);
    }

    /**
     * Restore data from classpath resource.
     *
     * @param resourcePath name of the resource
     * @param license the licence
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

    public AttachmentsControl attachments()
    {
        return attachmentsControl;
    }

    public TimeTrackingControl timeTracking()
    {
        return timeTrackingControl;
    }

    public LicenseControl license()
    {
        return licenseControl;
    }

    public ProjectRoleClient projectRoles()
    {
        return projectRoleClient;
    }

    public LogControl logControl()
    {
        return logControl;
    }

    public IndexingControl indexing()
    {
        return indexingControl;
    }

    public ApplicationLinkControl applicationLink()
    {
        return applicationLinkControl;
    }

    public RawRestApiControl rawRestApiControl()
    {
        return rawRestApiControl;
    }

    public AuditingControl auditing()
    {
        return auditingControl;
    }

    public AnalyticsClient analytics() { return analyticsClient; }

    public UserHistoryControl userHistory()
    {
        return userHistoryControl;
    }

    public FieldConfigurationSchemesControl fieldConfigurationSchemes()
    {
        return fieldConfigurationSchemes;
    }

    public IssueSecuritySchemesControl issueSecuritySchemesControl()
    {
        return issueSecuritySchemes;
    }

    public EntityPropertyControl getEntityPropertyControl()
    {
        return entityPropertyControl;
    }

    @Nonnull
    public EntityLinkControl getEntityLinkControl()
    {
        return entityLinkControl;
    }
}
