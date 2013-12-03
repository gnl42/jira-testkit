package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.fugue.Option;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.atlassian.jira.util.json.JSONObject;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.sun.jersey.api.client.WebResource;

/**
 * Client for issue property resource.
 *
 * @since v6.2
 */
public class IssuePropertyClient extends RestApiClient<IssuePropertyClient>
{
    /**
     * Constructs an issue property client for JIRA instance.
     *
     * @param environmentData The JIRA environmental data.
     */
    public IssuePropertyClient(final JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * Gets the properties keys for the issue with given id or key.
     * @param issueKeyOrId key or id of an issue.
     * @return list of issue properties keys.
     */
    public IssuePropertyKeys getKeys(String issueKeyOrId)
    {
        return resource(issueKeyOrId).get(IssuePropertyKeys.class);
    }

    /**
     * @param issueKeyOrId key or id of an issue.
     * @param propertyKey key of the property to return.
     * @return returns the property for the particular issue and property key.
     */
    public IssueProperty get(String issueKeyOrId, String propertyKey)
    {
        return resource(issueKeyOrId, propertyKey).get(IssueProperty.class);
    }

    /**
     * Sets the value of the property with given key, associated with a given issue.
     * @param issueKeyOrId key or id of an issue.
     * @param propertyKey key of the property.
     * @param value value of the property.
     */
    public void put(final String issueKeyOrId, final String propertyKey, final JSONObject value)
    {
        resource(issueKeyOrId, propertyKey).header("Content-Type", "application/json").put(String.class, value.toString());
    }

    /**
     * Removes the value of the property with given key, associated with a given issue.
     * @param issueKeyOrId key or id of an issue.
     * @param propertyKey key of the property to remove.
     */
    public void delete(final String issueKeyOrId, final String propertyKey)
    {
        resource(issueKeyOrId, propertyKey).delete();
    }

    public WebResource resource(String issueKeyOrId)
    {
        return resource(issueKeyOrId, Option.<String>none());
    }

    public WebResource resource(String issueKeyOrId, String propertyKey)
    {
        return resource(issueKeyOrId, Option.some(propertyKey));
    }

    private WebResource resource(final String issueKeyOrId, final Option<String> propertyKey)
    {
        final WebResource webResource = createResource().path("issue").path(issueKeyOrId).path("properties");
        return propertyKey.fold(new Supplier<WebResource>()
        {
            @Override
            public WebResource get()
            {
                return webResource;
            }
        }, new Function<String, WebResource>()
        {
            @Override
            public WebResource apply(final String propertyKey)
            {
                return webResource.path(propertyKey);
            }
        });
    }
}
