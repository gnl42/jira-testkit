package com.atlassian.jira.testkit.client;

import com.atlassian.jira.testkit.client.restclient.Project;
import com.google.common.base.Charsets;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Class for performing HTTP requests to the JIRA server.
 *
 * <p>Sometimes in your functional tests you need to assume some actions
 * taken by the user. Those actions are things users do in a browser.
 * This class can be thought of as a simplified browser that
 * allows you to perform certain actions in JIRA.</p>
 *
 * <p>Each instance maintains HTTP session.</p>
 *
 * @since v7.0
 */
public class JiraHttpClient
{
    private final HttpClient httpClient;
    private final String baseUrl;

    public JiraHttpClient(final String baseUrl)
    {
        this.baseUrl = baseUrl;
        this.httpClient = new HttpClient();
    }

    public JiraHttpClient loginAs(String namePass)
    {
        httpClient.getParams().setAuthenticationPreemptive(true);
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(namePass, namePass));
        return this;
    }

    /**
     * Perform GET http method to the arbitrary URL.
     * @param path relative path
     * @return response body (e.g. HTML, JSON)
     */
    public String get(String path)
    {
        HttpMethod method = new GetMethod(baseUrl + path);
        try
        {
            httpClient.executeMethod(method);
            byte[] responseBody = method.getResponseBody();
            return new String(responseBody, Charsets.UTF_8);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            method.releaseConnection();
        }
    }

    /**
     * Simulates viewing a specific project by the logged in user.
     *
     * @param projectKey key of the project to view
     * @return this instance, so that methods can be chained fluently
     */
    public JiraHttpClient browseProject(String projectKey) {
        get("/browse/" + projectKey);
        return this;
    }

    public List<Project> getRecentProjectsViaRestAPI() {
        String projectsJSON = get("/rest/api/2/project?recent=true");
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            return asList(mapper.readValue(projectsJSON, Project[].class));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
