/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.dataimport.DataImportParams;
import com.atlassian.jira.bc.dataimport.DataImportService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.task.TaskProgressSink;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.BuildUtilsInfo;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Use this backdoor to import data. It's even faster than the UI!
 * <p/>
 *
 * @since v5.0
 */
@Path ("dataImport")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class DataImportBackdoor
{
    private final UserUtil userUtil;
    private final BuildUtilsInfo buildInfo;
    private final ApplicationProperties applicationProperties;
    private final JiraHome jiraHome;
    private volatile DataImportService dataImportService;

    public DataImportBackdoor(UserUtil userUtil, BuildUtilsInfo buildInfo, JiraHome jiraHome,
                              ApplicationProperties applicationProperties)
    {
        this.userUtil = userUtil;
        this.buildInfo = buildInfo;
        this.applicationProperties = applicationProperties;
        this.jiraHome = jiraHome;
    }

    @POST
    public Response importData(DataImportBean importBean)
    {
        String licenseString = importBean.licenseString;
        boolean useDefaultPaths = importBean.useDefaultPaths;
        boolean quickImport = importBean.quickImport;
        DataImportParams params = new DataImportParams.Builder(importBean.filePath)
                .setLicenseString(licenseString)
                .setUseDefaultPaths(useDefaultPaths)
                .setQuickImport(quickImport)
                .build();

        User sysadmin = Iterables.get(userUtil.getJiraSystemAdministrators(), 0);
        DataImportService.ImportValidationResult result = getDataImportService().validateImport(sysadmin, params);
        DataImportService.ImportResult importResult = getDataImportService().doImport(sysadmin, result, TaskProgressSink.NULL_SINK);
        if (!importResult.isValid())
        {
            // Something went wrong. Die!
            throw new IllegalStateException("Restore failed!: " + importResult.getSpecificErrorMessage());
        }

        if (StringUtils.isNotBlank(importBean.baseUrl))
        {
            applicationProperties.setString(APKeys.JIRA_BASEURL, importBean.baseUrl);
        }

        return Response.ok(null).build();
    }

    @GET
    @Path("jiraHomePath")
    public Response getJiraHomePath()
    {
        String jiraHomePath = jiraHome.getHomePath();
        return Response.ok(jiraHomePath).build();
    }

    @GET
    @Path("importConfig")
    public Response getImportConfig()
    {
        final String jiraHomePath = jiraHome.getHomePath();
        final int buildNumber = buildInfo.getApplicationBuildNumber();
        return Response.ok(new ImportConfig(jiraHomePath, buildNumber)).build();
    }

    public DataImportService getDataImportService()
    {
        if (dataImportService == null)
        {
            dataImportService = ComponentAccessor.getComponent(DataImportService.class);
        }
        return dataImportService;
    }

    public static final class ImportConfig
    {
        public String jiraHome;
        public int buildNumber;

        public ImportConfig(String jiraHome, int buildNumber)
        {
            this.jiraHome = jiraHome;
            this.buildNumber = buildNumber;
        }
    }
}
