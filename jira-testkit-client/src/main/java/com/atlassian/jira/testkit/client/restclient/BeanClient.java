package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import java.util.Arrays;
import java.util.List;

/**
 * @since v5.0
 */
public class BeanClient extends RestApiClient<BeanClient>
{
    public BeanClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public LabelSuggestionsBean getLabelSuggestionsFromUrl(final String url) throws UniformInterfaceException
    {
        return resourceRoot(url).get(LabelSuggestionsBean.class);
    }

    public IssuePickerBean getIssueSuggestionsFromUrl(final String url) throws UniformInterfaceException
    {
        return resourceRoot(url).get(IssuePickerBean.class);
    }

    public List<User> getUsersFromUrl(String url)
    {
        WebResource resource = resourceRoot(url);
        return Arrays.asList(resource.get(User[].class));
    }

    public UserPickerResultBean getUserPickResultsFromUrl(String url)
    {
       return resourceRoot(url).get(UserPickerResultBean.class);
    }
}
