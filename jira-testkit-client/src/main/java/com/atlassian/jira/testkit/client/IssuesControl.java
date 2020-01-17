/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.atlassian.jira.rest.api.issue.IssueCreateResponse;
import com.atlassian.jira.rest.api.issue.IssueFields;
import com.atlassian.jira.rest.api.issue.IssueUpdateRequest;
import com.atlassian.jira.rest.api.issue.ResourceRef;
import com.atlassian.jira.testkit.client.IssueTypeControl.IssueType;
import com.atlassian.jira.testkit.client.restclient.Comment;
import com.atlassian.jira.testkit.client.restclient.CommentClient;
import com.atlassian.jira.testkit.client.restclient.Issue;
import com.atlassian.jira.testkit.client.restclient.IssueClient;
import com.atlassian.jira.testkit.client.restclient.IssuesExtClient;
import com.atlassian.jira.testkit.client.restclient.Response;
import com.atlassian.jira.testkit.client.restclient.Visibility;
import com.atlassian.jira.util.collect.MapBuilder;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.atlassian.jira.rest.api.issue.ResourceRef.withId;
import static com.atlassian.jira.rest.api.issue.ResourceRef.withKey;
import static com.atlassian.jira.rest.api.issue.ResourceRef.withName;
import static com.google.common.collect.Iterables.find;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.commons.lang.StringUtils.isNumeric;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.junit.Assert.assertEquals;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Issues.
 *
 * @since v5.0
 */
@SuppressWarnings("unused" /* used in JIRA's func tests */)
@ParametersAreNonnullByDefault
public class IssuesControl extends BackdoorControl<IssuesControl> {
    // Ids of projects pre-imported from testkit-blankprojects.xml; should be present in most tests.
    public static final long HSP_PROJECT_ID = 10000;
    public static final long MKY_PROJECT_ID = 10001;
    private static final String DEFAULT_PRIORITY = "1"; // presumed to be "Blocker"

    private final IssueClient issueClient;
    private final CommentClient commentClient;
    private final IssueTypeControl issueTypeControl;
    private final IssuesExtClient issuesExtClient;

    public IssuesControl(JIRAEnvironmentData environmentData, IssueTypeControl issueTypeControl) {
        super(environmentData);
        this.issueTypeControl = issueTypeControl;
        issueClient = new IssueClient(environmentData);
        commentClient = new CommentClient(environmentData);
        issuesExtClient = new IssuesExtClient(environmentData);
    }

    /**
     * @deprecated use createIssue(projectKey,...); since 7.0
     */
    @Deprecated
    public IssueCreateResponse createIssue(long projectId, String summary) {
        return createIssue(projectId, summary, null);
    }

    public IssueCreateResponse createSubtask(String projectId, String parentKey, String summary) {
        IssueType issueType = find(issueTypeControl.getIssueTypes(), type -> type.getName().equals("Sub-task"));

        IssueFields fields = new IssueFields();
        fields.project(withId("" + projectId)); // 10000
        fields.parent(withKey(parentKey));
        fields.issueType(withId(issueType.getId()));   // Sub-task
        fields.priority(withId("1"));   // Blocker
        fields.summary(summary);

        IssueUpdateRequest issue = new IssueUpdateRequest();
        return issueClient.create(issue.fields(fields));
    }

    public IssuesControl setDescription(String issueKey, String description) {
        IssueUpdateRequest fields = new IssueUpdateRequest().fields(new IssueFields().description(description));
        issueClient.update(issueKey, fields);
        return this;
    }

    public IssueCreateResponse createIssue(String projectKey, String summary) {
        return createIssue(projectKey, summary, null, DEFAULT_PRIORITY, getBestGuessIssueType(Optional.of(projectKey)));
    }

    /**
     * @deprecated use createIssue(projectKey,...); since 7.0
     */
    @Deprecated
    public IssueCreateResponse createIssue(long projectId, String summary, @Nullable String assignee) {
        IssueFields fields = new IssueFields();
        fields.project(withId(String.valueOf(projectId)));
        fields.issueType(withId(getBestGuessIssueType(Optional.of(Long.toString(projectId)))));
        fields.priority(withId(DEFAULT_PRIORITY));
        fields.summary(summary);
        if (assignee != null) {
            fields.assignee(withName(assignee));
        }

        return issueClient.create(new IssueUpdateRequest().fields(fields));
    }

    public IssueCreateResponse createIssue(String projectKey, String summary, String assignee) {
        return createIssue(projectKey, summary, assignee, DEFAULT_PRIORITY, getBestGuessIssueType(Optional.of(projectKey)));
    }

    /**
     * @param projectKey - project key for project that issue will be linked to
     * @param summary    - summary of issue
     * @param assignee   - name of user to assign issue
     * @param priority   - priority of issue can be ether id or name (eg. "Major")
     * @param issueType  - type of issue can be ether id or name (eg. "Bug")
     * @return an {@link IssueCreateResponse}
     */
    public IssueCreateResponse createIssue(String projectKey, String summary, @Nullable String assignee,
                                           String priority, String issueType) {
        return createIssue(projectKey, summary, null, assignee, priority, issueType);
    }

    /**
     * @param projectKey  - project key for project that issue will be linked to
     * @param summary     - summary of issue
     * @param description - description of issue
     * @param assignee    - name of user to assign issue
     * @param priority    - priority of issue can be ether id or name (eg. "Major")
     * @param issueType   - type of issue can be ether id or name (eg. "Bug")
     * @return an {@link IssueCreateResponse}
     */
    public IssueCreateResponse createIssue(String projectKey, String summary, @Nullable String description,
                                           @Nullable String assignee, String priority, String issueType) {
        return createIssue(projectKey, summary, description, assignee, priority, issueType, false);
    }

    /**
     * @param projectKey    - project key for project that issue will be linked to
     * @param summary       - summary of issue
     * @param description   - description of issue
     * @param assignee      - name of user to assign issue
     * @param priority      - priority of issue can be ether id or name (eg. "Major")
     * @param issueType     - type of issue can be ether id or name (eg. "Bug")
     * @param updateHistory - if true then the user's project history is updated
     * @return an {@link IssueCreateResponse}
     */
    public IssueCreateResponse createIssue(String projectKey, String summary, @Nullable String description,
                                           @Nullable String assignee, String priority, String issueType,
                                           boolean updateHistory) {
        IssueFields fields = new IssueFields();
        fields.project(withKey(projectKey));
        fields.issueType(isNumeric(issueType) ? withId(issueType) : withName(issueType));
        fields.priority(isNumeric(priority) ? withId(priority) : withName(priority));
        fields.summary(summary);
        if (description != null) {
            fields.description(description);
        }
        if (assignee != null) {
            fields.assignee(withName(assignee));
        }

        return issueClient.create(new IssueUpdateRequest().fields(fields), updateHistory);
    }

    /**
     * Compatibility fallback used to handle the case where clients want to create an issue, but don't care what
     * type it is. Heuristic is to filter out all the issue types with known required extras (such as epic or sub-task),
     * returning the first issue type found. Fails fast if no issue types found.
     */
    @Nonnull
    private String getBestGuessIssueType(Optional<String> projectIdOrKey) {
        final List<IssueType> issueTypes = projectIdOrKey
                .map(issueTypeControl::getIssueTypesForProject)
                .orElse(issueTypeControl.getIssueTypes());
        final Optional<IssueType> issueType = issueTypes
                .stream()
                .filter(type -> !type.isSubtask() && !type.getName().equals("Epic"))
                .findFirst();

        if (!issueType.isPresent()) {
            // no other issue types, so fail fast on the client rather than returning the error response to make it
            // easier to debug.
            throw new UnsupportedOperationException("Can't create issue due to no default issue types");
        }

        return issueType.get().getId();
    }

    public Response<Comment> commentIssue(String issueKey, String comment) {
        return commentIssueWithVisibility(issueKey, comment, "group", "jira-administrators");
    }

    public Response<Comment> commentIssueWithVisibility(String issueKey, String comment, String restrictedType, String restrictedParam) {
        Comment newComment = new Comment();
        newComment.body = comment;
        newComment.visibility = new Visibility(restrictedType, restrictedParam);
        return assertStatusCode(SC_CREATED, commentClient.post(issueKey, newComment));
    }

    public Response<Comment> updateComment(String issueKey, String commentId, String comment, String restrictedType, String restrictedParam) {
        Comment update = new Comment();
        update.id = commentId;
        update.body = comment;
        if (!isEmpty(restrictedType) && !isEmpty(restrictedParam)) {
            update.visibility = new Visibility(restrictedType, restrictedParam);
        }
        return assertStatusCode(SC_OK, commentClient.put(issueKey, update));
    }

    public Response deleteComment(String issueKey, String commentId) {
        return assertStatusCode(SC_NO_CONTENT, commentClient.delete(issueKey, commentId));
    }

    public void assignIssue(String issueKey, String username) {
        IssueUpdateRequest updateSummaryRequest = new IssueUpdateRequest().fields(new IssueFields()
                .assignee(withName(username))
        );

        issueClient.update(issueKey, updateSummaryRequest);
    }

    public Issue getIssue(String issueKey, Issue.Expand... expand) {
        return getIssue(issueKey, false, expand);
    }

    public Issue getIssue(String issueKey, boolean updateHistory, Issue.Expand... expand) {
        return issueClient.get(issueKey, updateHistory, expand);
    }

    public void transitionIssue(String issueKey, int transitionId) {
        ResourceRef transition = ResourceRef.withId(String.valueOf(transitionId));
        IssueUpdateRequest updateSummaryRequest = new IssueUpdateRequest().transition(transition);
        assertStatusCode(SC_NO_CONTENT, issueClient.transition(issueKey, updateSummaryRequest));
    }

    public IssuesControl addLabel(String issueKey, String label) {
        final Map<String, String> add = MapBuilder.<String, String>newBuilder().add("add", label).toMap();
        final List<Map<String, String>> addList = Collections.singletonList(add);
        final Map<String, List<Map<String, String>>> labels = MapBuilder.<String, List<Map<String, String>>>newBuilder().add("labels", addList).toMap();
        final Map<String, Map<String, List<Map<String, String>>>> update = MapBuilder.<String, Map<String, List<Map<String, String>>>>newBuilder().add("update", labels).toMap();
        final Response response = issueClient.update(issueKey, update);

        assertStatusCode(204, response);
        return this;
    }

    public IssuesControl setSummary(String issueKey, String summary) {
        IssueUpdateRequest updateSummaryRequest = new IssueUpdateRequest().fields(new IssueFields()
                .summary(summary));

        final Response response = issueClient.updateResponse(issueKey, updateSummaryRequest);

        assertStatusCode(204, response);
        return this;
    }

    public IssuesControl setIssueFields(String issueKey, IssueFields issueFields) {
        IssueUpdateRequest updateRequest = new IssueUpdateRequest().fields(issueFields);

        final Response response = issueClient.updateResponse(issueKey, updateRequest);

        assertStatusCode(204, response);
        return this;
    }

    /**
     * Deletes an issue. If the issue has subtasks you must set the parameter deleteSubtasks=true to delete the issue.
     * You cannot delete an issue without its subtasks also being deleted.
     *
     * @param issueKey       a key of the issue
     * @param deleteSubtasks true to delete also subtasks. If this params is false, and issue has subtasks, then
     *                       delete operation will fail.
     * @return Response from the server (to check the status code)
     * @throws UniformInterfaceException if there's a problem deleting the issue
     */
    public Response deleteIssue(String issueKey, boolean deleteSubtasks) throws UniformInterfaceException {
        return assertStatusCode(SC_NO_CONTENT, issueClient.delete(issueKey, Boolean.toString(deleteSubtasks)));
    }

    public ClientResponse archiveIssue(String issueIdOrKey) {
        return assertStatusCode(SC_NO_CONTENT, issueClient.archive(issueIdOrKey));
    }

    public ClientResponse restoreIssue(String issueIdOrKey) {
        return assertStatusCode(SC_NO_CONTENT, issueClient.restore(issueIdOrKey));
    }

    public void touch(final String key) {
        issuesExtClient.touch(key);
    }

    public void changeUpdated(final String key, final Date date) {
        issuesExtClient.changeUpdated(key, date);
    }

    public void changeCreated(final String key, final Date date) {
        issuesExtClient.changeCreated(key, date);
    }

    @Override
    public IssuesControl loginAs(String username) {
        commentClient.loginAs(username);
        issueClient.loginAs(username);
        return super.loginAs(username);
    }

    @Override
    public IssuesControl loginAs(String username, String password) {
        commentClient.loginAs(username, password);
        issueClient.loginAs(username, password);
        return super.loginAs(username, password);
    }

    @Override
    public IssuesControl anonymous() {
        super.anonymous();
        issueClient.anonymous();

        return this;
    }

    public Issue getIssue(String key) {
        return issueClient.get(key);
    }

    public IssuesControl addFixVersion(String issueKey, String version) {
        return updateVersionField(issueKey, "fixVersions", version, "add");
    }

    public IssuesControl removeFixVersion(String issueKey, String version) {
        return updateVersionField(issueKey, "fixVersions", version, "remove");
    }

    public IssuesControl addAffectsVersion(String issueKey, String version) {
        return updateVersionField(issueKey, "versions", version, "add");
    }

    private IssuesControl updateVersionField(String issueKey, String fieldId, String version, String operation) {
        final Map<String, Map<String, String>> add = MapBuilder.<String, Map<String, String>>newBuilder().add(operation, MapBuilder.<String, String>newBuilder().add("name", version).toMap()).toMap();
        final List<Map<String, Map<String, String>>> addList = Collections.singletonList(add);
        final Map<String, List<Map<String, Map<String, String>>>> versions = MapBuilder.<String, List<Map<String, Map<String, String>>>>newBuilder().add(fieldId, addList).toMap();
        final Map<String, Map<String, List<Map<String, Map<String, String>>>>> update = MapBuilder.<String, Map<String, List<Map<String, Map<String, String>>>>>newBuilder().add("update", versions).toMap();
        final Response response = issueClient.update(issueKey, update);

        assertStatusCode(SC_NO_CONTENT, response);
        return this;
    }

    private <T extends Response> T assertStatusCode(int expectedStatusCode, T response) {
        assertEquals("Request failed: " + response, expectedStatusCode, response.statusCode);
        return response;
    }

    private <T extends ClientResponse> T assertStatusCode(int expectedStatusCode, T response) {
        assertEquals("Request failed: " + response, expectedStatusCode, response.getStatus());
        return response;
    }
}
