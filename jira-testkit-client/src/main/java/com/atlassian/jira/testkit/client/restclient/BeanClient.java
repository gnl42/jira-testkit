/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

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
