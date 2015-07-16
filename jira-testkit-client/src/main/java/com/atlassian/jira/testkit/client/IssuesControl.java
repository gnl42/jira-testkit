/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.rest.api.issue.ResourceRef;
import com.atlassian.jira.testkit.client.restclient.Comment;
import com.atlassian.jira.testkit.client.restclient.CommentClient;
import com.atlassian.jira.testkit.client.restclient.Issue;
import com.atlassian.jira.testkit.client.restclient.IssueClient;
import com.atlassian.jira.testkit.client.restclient.Response;
import com.atlassian.jira.testkit.client.restclient.Visibility;
import com.atlassian.jira.rest.api.issue.IssueCreateResponse;
import com.atlassian.jira.rest.api.issue.IssueFields;
import com.atlassian.jira.rest.api.issue.IssueUpdateRequest;
import com.atlassian.jira.util.collect.MapBuilder;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.sun.jersey.api.client.UniformInterfaceException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.atlassian.jira.rest.api.issue.ResourceRef.withId;
import static com.atlassian.jira.rest.api.issue.ResourceRef.withKey;
import static com.atlassian.jira.rest.api.issue.ResourceRef.withName;
import static org.junit.Assert.assertTrue;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Issues.
 *
 * @since v5.0
 */
public class IssuesControl extends BackdoorControl<IssuesControl>
{
    // Ids of projects pre-imported from testkit-blankprojects.xml; should be present in most tests.
    public static final long HSP_PROJECT_ID = 10000;
    public static final long MKY_PROJECT_ID = 10001;

    private IssueClient issueClient;
    private CommentClient commentClient;
    private final IssueTypeControl issueTypeControl;

    public IssuesControl(JIRAEnvironmentData environmentData, IssueTypeControl issueTypeControl)
    {
        super(environmentData);
        this.issueTypeControl = issueTypeControl;
        issueClient = new IssueClient(environmentData);
        commentClient = new CommentClient(environmentData);
    }

    public IssueCreateResponse createIssue(long projectId, String summary)
    {
        return createIssue(projectId, summary, null);
    }

    public IssueCreateResponse createSubtask(String projectId, String parentKey, String summary)
    {
        final IssueTypeControl.IssueType issueType = Iterables.find(issueTypeControl.getIssueTypes(), hasName("Sub-task"));
        IssueFields fields = new IssueFields();
        fields.project(withId("" + projectId)); // 10000
        fields.parent(withKey(parentKey));
        fields.issueType(withId(issueType.getId()));   // Sub-task
        fields.priority(withId("1"));   // Blocker
        fields.summary(summary);

        IssueUpdateRequest issue = new IssueUpdateRequest();
        return issueClient.create(issue.fields(fields));
    }

    private Predicate<IssueTypeControl.IssueType> hasName(final String anObject)
    {
        return new Predicate<IssueTypeControl.IssueType>()
        {
            @Override
            public boolean apply(final IssueTypeControl.IssueType input)
            {
                return input.getName().equals(anObject);
            }
        };
    }

    public IssueCreateResponse createIssue(String projectKey, String summary)
    {
        return createIssue(projectKey, summary, null);
    }

    public IssuesControl setDescription(String issueKey, String description)
    {
        final IssueUpdateRequest fields = new IssueUpdateRequest().fields(new IssueFields()
                .description(description)
        );
        issueClient.update(issueKey, fields);
        return this;
    }

    public IssueCreateResponse createIssue(long projectId, String summary, String assignee)
    {
        IssueFields fields = new IssueFields();
        fields.project(withId("" + projectId)); // 10000
        fields.issueType(withId("1"));  // Bug
        fields.priority(withId("1"));   // Blocker
        if (assignee != null)
        {
            fields.assignee(withName(assignee));
        }
        fields.summary(summary);

        IssueUpdateRequest issue = new IssueUpdateRequest();
        return issueClient.create(issue.fields(fields));
    }

    public IssueCreateResponse createIssue(String projectKey, String summary, String assignee)
    {
        IssueFields fields = new IssueFields();
        fields.project(withKey(projectKey)); // MKY
        fields.issueType(withId("1"));  // Bug
        fields.priority(withId("1"));   // Blocker
        if (assignee != null)
        {
            fields.assignee(withName(assignee));
        }
        fields.summary(summary);

        IssueUpdateRequest issue = new IssueUpdateRequest();
        return issueClient.create(issue.fields(fields));
    }

    public Response<Comment> commentIssue(String issueKey, String comment)
    {
        return commentIssueWithVisibility(issueKey, comment, "group", "jira-administrators");
    }

    public Response<Comment> commentIssueWithVisibility(String issueKey, String comment, String restrictedType, String restrictedParam)
    {
        Comment newComment = new Comment();
        newComment.body = comment;
        newComment.visibility = new Visibility(restrictedType, restrictedParam);
        return commentClient.post(issueKey, newComment);
    }


    public void assignIssue(String issueKey, String username)
    {
        IssueUpdateRequest updateSummaryRequest = new IssueUpdateRequest().fields(new IssueFields()
                .assignee(withName(username))
        );

        issueClient.update(issueKey, updateSummaryRequest);
    }

    public Issue getIssue(String issueKey, Issue.Expand... expand)
    {
        return issueClient.get(issueKey, expand);
    }

    public void transitionIssue(String issueKey, int transitionId)
    {
        ResourceRef transition = ResourceRef.withId(String.valueOf(transitionId));
        IssueUpdateRequest updateSummaryRequest = new IssueUpdateRequest().transition(transition);
        issueClient.transition(issueKey, updateSummaryRequest);
    }

    public IssuesControl addLabel(String issueKey, String label)
    {
        final Map<String, String> add = MapBuilder.<String, String>newBuilder().add("add", label).toMap();
        final List<Map<String, String>> addList = Collections.singletonList(add);
        final Map<String, List<Map<String, String>>> labels = MapBuilder.<String, List<Map<String, String>>>newBuilder().add("labels", addList).toMap();
        final Map<String, Map<String, List<Map<String, String>>>> update = MapBuilder.<String, Map<String, List<Map<String, String>>>>newBuilder().add("update", labels).toMap();
        final Response response = issueClient.update(issueKey, update);

        assertTrue("Update failed. " + response.toString(), response.statusCode == 204);
        return this;
    }

    public IssuesControl setSummary(String issueKey, String summary)
    {
        IssueUpdateRequest updateSummaryRequest = new IssueUpdateRequest().fields(new IssueFields()
                .summary(summary));

        final Response response = issueClient.updateResponse(issueKey, updateSummaryRequest);

        assertTrue("Update failed. " + response.toString(), response.statusCode == 204);
        return this;
    }

    public IssuesControl setIssueFields(String issueKey, IssueFields issueFields)
    {
        IssueUpdateRequest updateRequest = new IssueUpdateRequest().fields(issueFields);

        final Response response = issueClient.updateResponse(issueKey, updateRequest);

        assertTrue("Update failed. " + response.toString(), response.statusCode == 204);
        return this;
    }

    /**
     * Deletes an issue. If the issue has subtasks you must set the parameter deleteSubtasks=true to delete the issue.
     * You cannot delete an issue without its subtasks also being deleted.
     * @param issueKey a key of the issue
     * @param deleteSubtasks true to delete also subtasks. If this params is false, and issue has subtasks, then
     *                          delete operation will fail.
     * @return Response from the server (to check the status code)
     * @throws UniformInterfaceException if there's a problem deleting the issue
     */
    public Response deleteIssue(String issueKey, boolean deleteSubtasks) throws UniformInterfaceException
    {
        return issueClient.delete(issueKey, Boolean.toString(deleteSubtasks));
    }

    @Override
    public IssuesControl loginAs(String username)
    {
        issueClient.loginAs(username);
        return super.loginAs(username);
    }

    @Override
    public IssuesControl loginAs(String username, String password)
    {
        issueClient.loginAs(username, password);
        return super.loginAs(username, password);
    }
    
    public Issue getIssue(String key)
    {
        return issueClient.get(key);
    }
}
