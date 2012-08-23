package com.atlassian.jira.tests.backdoor;


import com.atlassian.jira.functest.framework.backdoor.*;
import com.atlassian.jira.functest.framework.backdoor.ApplicationPropertiesControl;
import com.atlassian.jira.functest.framework.backdoor.DarkFeaturesControl;
import com.atlassian.jira.functest.framework.backdoor.DashboardControl;
import com.atlassian.jira.functest.framework.backdoor.FieldConfigurationControl;
import com.atlassian.jira.functest.framework.backdoor.GeneralConfigurationControl;
import com.atlassian.jira.functest.framework.backdoor.I18nControl;
import com.atlassian.jira.functest.framework.backdoor.IndexingControl;
import com.atlassian.jira.functest.framework.backdoor.IssueLinkingControl;
import com.atlassian.jira.functest.framework.backdoor.IssueTypeScreenSchemesControl;
import com.atlassian.jira.functest.framework.backdoor.IssuesControl;
import com.atlassian.jira.functest.framework.backdoor.MailServersControl;
import com.atlassian.jira.functest.framework.backdoor.PermissionSchemesControl;
import com.atlassian.jira.functest.framework.backdoor.PermissionsControl;
import com.atlassian.jira.functest.framework.backdoor.PluginsControl;
import com.atlassian.jira.functest.framework.backdoor.ProjectControl;
import com.atlassian.jira.functest.framework.backdoor.SearchRequestControl;
import com.atlassian.jira.functest.framework.backdoor.ServicesControl;
import com.atlassian.jira.functest.framework.backdoor.SubtaskControl;
import com.atlassian.jira.functest.framework.backdoor.TestRunnerControl;
import com.atlassian.jira.functest.framework.backdoor.WebSudoControl;
import com.atlassian.jira.functest.framework.backdoor.WorkflowSchemesControl;
import com.atlassian.jira.functest.framework.backdoor.WorkflowsControl;
import com.atlassian.jira.webtests.util.JIRAEnvironmentData;
import com.atlassian.jira.webtests.ztests.bundledplugins2.rest.client.SearchClient;

/**
 * Top-level of Backdoor control hierarchy. Use components of this class to
 * manipulate the back-end in func tests without hitting the UI.
 *
 * @since v5.0
 */
public class Backdoor
{
    private final UsersAndGroupsControl usersAndGroups;
    private final com.atlassian.jira.tests.backdoor.IssuesControl issues;
    private final com.atlassian.jira.tests.backdoor.I18nControl i18n;
    private final com.atlassian.jira.tests.backdoor.DarkFeaturesControl darkFeatures;
    private final DataImportControl dataImport;
    private final com.atlassian.jira.tests.backdoor.PermissionsControl permissions;
    private final com.atlassian.jira.tests.backdoor.ApplicationPropertiesControl applicationProperties;
    private final com.atlassian.jira.tests.backdoor.ProjectControl project;
    private final com.atlassian.jira.tests.backdoor.PermissionSchemesControl permissionSchemes;
    private final com.atlassian.jira.tests.backdoor.MailServersControl mailServers;
    private final com.atlassian.jira.tests.backdoor.SearchRequestControl searchRequests;
    private final UserProfileControl userProfile;
    private final com.atlassian.jira.tests.backdoor.ServicesControl services;
    private final com.atlassian.jira.tests.backdoor.TestRunnerControl testRunner;
    private final com.atlassian.jira.tests.backdoor.FieldConfigurationControl fieldConfigurationControl;
    private final IssueTypeControl issueTypeControl;
    private final com.atlassian.jira.tests.backdoor.SubtaskControl subtaskCotnrol;
    private final com.atlassian.jira.tests.backdoor.IssueLinkingControl issueLinkingControl;
    private final com.atlassian.jira.tests.backdoor.IssueTypeScreenSchemesControl issueTypeScreenSchemes;
    private final com.atlassian.jira.tests.backdoor.WebSudoControl webSudoControl;
    private final com.atlassian.jira.tests.backdoor.DashboardControl dashboardControl;
    private final com.atlassian.jira.tests.backdoor.PluginsControl plugins;
    private final com.atlassian.jira.tests.backdoor.GeneralConfigurationControl generalConfigurationControl;
    private final com.atlassian.jira.tests.backdoor.WorkflowsControl workflowsControl;
    private final com.atlassian.jira.tests.backdoor.WorkflowSchemesControl workflowSchemesControl;
    private final AdvancedSettingsControl advancedSettingsControl;
    private final SearchClient searchClient;
    private final CustomFieldsControl customFieldsControl;
	private final AttachmentsControl attachmentsControl;
	private final LicenseControl licenseControl;
	private final com.atlassian.jira.tests.backdoor.IndexingControl indexingControl;
	private final TimeTrackingControl timeTrackingControl;

	public Backdoor(JIRAEnvironmentData environmentData)
    {
        this.plugins = new com.atlassian.jira.tests.backdoor.PluginsControl(environmentData);
        this.usersAndGroups = new UsersAndGroupsControl(environmentData);
        this.issues = new com.atlassian.jira.tests.backdoor.IssuesControl(environmentData);
        this.i18n = new com.atlassian.jira.tests.backdoor.I18nControl(environmentData);
        this.darkFeatures = new com.atlassian.jira.tests.backdoor.DarkFeaturesControl(environmentData);
        this.permissions = new com.atlassian.jira.tests.backdoor.PermissionsControl(environmentData);
        this.applicationProperties = new com.atlassian.jira.tests.backdoor.ApplicationPropertiesControl(environmentData);
        this.project = new com.atlassian.jira.tests.backdoor.ProjectControl(environmentData);
        this.permissionSchemes = new com.atlassian.jira.tests.backdoor.PermissionSchemesControl(environmentData);
        this.issueTypeScreenSchemes = new com.atlassian.jira.tests.backdoor.IssueTypeScreenSchemesControl(environmentData);
        this.mailServers = new com.atlassian.jira.tests.backdoor.MailServersControl(environmentData);
        this.searchRequests = new com.atlassian.jira.tests.backdoor.SearchRequestControl(environmentData);
        this.userProfile = new UserProfileControl(environmentData);
        this.dataImport = new DataImportControl(environmentData);
        this.services = new com.atlassian.jira.tests.backdoor.ServicesControl(environmentData);
        this.testRunner = new com.atlassian.jira.tests.backdoor.TestRunnerControl(environmentData);
        this.fieldConfigurationControl = new com.atlassian.jira.tests.backdoor.FieldConfigurationControl(environmentData);
        this.issueTypeControl = new IssueTypeControl(environmentData);
        this.subtaskCotnrol = new com.atlassian.jira.tests.backdoor.SubtaskControl(environmentData);
        this.webSudoControl = new com.atlassian.jira.tests.backdoor.WebSudoControl(environmentData);
        this.issueLinkingControl = new com.atlassian.jira.tests.backdoor.IssueLinkingControl(environmentData);
        this.dashboardControl = new com.atlassian.jira.tests.backdoor.DashboardControl(environmentData);
        this.generalConfigurationControl = new com.atlassian.jira.tests.backdoor.GeneralConfigurationControl(environmentData);
        this.workflowsControl = new com.atlassian.jira.tests.backdoor.WorkflowsControl(environmentData);
        this.workflowSchemesControl = new com.atlassian.jira.tests.backdoor.WorkflowSchemesControl(environmentData);
        this.advancedSettingsControl = new AdvancedSettingsControl(environmentData);
        this.searchClient = new SearchClient(environmentData);
        this.customFieldsControl = new CustomFieldsControl(environmentData);
		this.attachmentsControl = new AttachmentsControl(environmentData);
		this.licenseControl = new LicenseControl(environmentData);
		this.indexingControl = new com.atlassian.jira.tests.backdoor.IndexingControl(environmentData);
		this.timeTrackingControl = new TimeTrackingControl(environmentData);
    }

    public UsersAndGroupsControl usersAndGroups()
    {
        return usersAndGroups;
    }

    public com.atlassian.jira.tests.backdoor.IssuesControl issues()
    {
        return issues;
    }

    public com.atlassian.jira.tests.backdoor.I18nControl i18n()
    {
        return i18n;
    }

    public com.atlassian.jira.tests.backdoor.DarkFeaturesControl darkFeatures()
    {
        return darkFeatures;
    }

    public com.atlassian.jira.tests.backdoor.PluginsControl plugins()
    {
        return plugins;
    }

    public com.atlassian.jira.tests.backdoor.PermissionsControl permissions()
    {
        return permissions;
    }

    public com.atlassian.jira.tests.backdoor.ApplicationPropertiesControl applicationProperties()
    {
        return applicationProperties;
    }

    public com.atlassian.jira.tests.backdoor.ProjectControl project()
    {
        return project;
    }

    public com.atlassian.jira.tests.backdoor.PermissionSchemesControl permissionSchemes()
    {
        return permissionSchemes;
    }

    public com.atlassian.jira.tests.backdoor.IssueTypeScreenSchemesControl issueTypeScreenSchemes()
    {
        return issueTypeScreenSchemes;
    }

    public com.atlassian.jira.tests.backdoor.MailServersControl mailServers()
    {
        return mailServers;
    }

    public com.atlassian.jira.tests.backdoor.SearchRequestControl searchRequests()
    {
        return searchRequests;
    }

    public UserProfileControl userProfile()
    {
        return userProfile;
    }

    public com.atlassian.jira.tests.backdoor.ServicesControl services()
    {
        return services;
    }

    public DataImportControl dataImport()
    {
        return dataImport;
    }

    public com.atlassian.jira.tests.backdoor.TestRunnerControl testRunner()
    {
        return testRunner;
    }

    public com.atlassian.jira.tests.backdoor.FieldConfigurationControl fieldConfiguration()
    {
        return fieldConfigurationControl;
    }

    public IssueTypeControl issueType()
    {
        return issueTypeControl;
    }

    public com.atlassian.jira.tests.backdoor.SubtaskControl subtask()
    {
        return subtaskCotnrol;
    }

    public com.atlassian.jira.tests.backdoor.WebSudoControl websudo()
    {
        return webSudoControl;
    }

    public com.atlassian.jira.tests.backdoor.DashboardControl dashboard()
    {
        return dashboardControl;
    }

    public com.atlassian.jira.tests.backdoor.GeneralConfigurationControl generalConfiguration()
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
     * @deprecated use {@link #restoreDataFromResource(String)} instead
     * @see DataImportControl#restoreData(String)
     */
    @Deprecated
    public void restoreData(String xmlFileName)
    {
        dataImport().restoreData(xmlFileName);
    }

    /**
     * Restore data from classpath resource.
     *
      * @param resourcePath name of the resource
     * @see DataImportControl#restoreDataFromResource(String)
     */
    public void restoreDataFromResource(String resourcePath)
    {
        dataImport().restoreDataFromResource(resourcePath);
    }

    public void restoreBlankInstance()
    {
        dataImport().restoreBlankInstance();
    }

    public com.atlassian.jira.tests.backdoor.IssueLinkingControl issueLinking()
    {
        return issueLinkingControl;
    }

    public com.atlassian.jira.tests.backdoor.WorkflowsControl workflow()
    {
        return workflowsControl;
    }

    public com.atlassian.jira.tests.backdoor.WorkflowSchemesControl workflowSchemes()
    {
        return workflowSchemesControl;
    }

    public SearchClient search()
    {
        return searchClient;
    }

	public AttachmentsControl attachments() {
		return attachmentsControl;
	}

	public LicenseControl license() {
		return licenseControl;
	}

	public com.atlassian.jira.tests.backdoor.IndexingControl indexing() {
		return indexingControl;
	}

	public TimeTrackingControl timeTracking() {
		return timeTrackingControl;
	}
}
