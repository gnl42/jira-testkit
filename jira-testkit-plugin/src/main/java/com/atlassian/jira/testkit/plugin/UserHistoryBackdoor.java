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
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.ApplicationUsers;
import com.atlassian.jira.user.UserIssueHistoryManager;
import com.atlassian.jira.user.UserQueryHistoryManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @since v6.4
 */
@Path ("userhistory")
public class UserHistoryBackdoor
{
    private final UserIssueHistoryManager userIssueHistoryManager;
    private final UserManager userManager;
    private final IssueManager issueManager;
    private final UserQueryHistoryManager userQueryHistoryManager;

    public UserHistoryBackdoor(UserIssueHistoryManager userIssueHistoryManager,
            UserManager userManager,
            IssueManager issueManager,
            UserQueryHistoryManager userQueryHistoryManager)
    {
        this.userIssueHistoryManager = userIssueHistoryManager;
        this.userManager = userManager;
        this.issueManager = issueManager;
        this.userQueryHistoryManager = userQueryHistoryManager;
    }

    @GET
    @AnonymousAllowed
    @Path("issue/add")
    public Response addIssue(@QueryParam ("user") String userName, @QueryParam ("key") String issueKey)
    {
        final ApplicationUser user = userManager.getUserByName(userName);
        final Issue issue = issueManager.getIssueByKeyIgnoreCase(issueKey);
        userIssueHistoryManager.addIssueToHistory(user, issue);
        return Response.ok(null).build();
    }


    @GET
    @AnonymousAllowed
    @Path("jqlquery/add")
    public Response addJQLQuery(@QueryParam ("user") String userName, @QueryParam ("query") String query)
    {
        final User user = ApplicationUsers.toDirectoryUser(userManager.getUserByName(userName));
        userQueryHistoryManager.addQueryToHistory(user, query);
        return Response.ok(null).build();
    }
}
