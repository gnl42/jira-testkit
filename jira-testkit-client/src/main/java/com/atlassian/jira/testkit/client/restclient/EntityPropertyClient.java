package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.fugue.Option;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.atlassian.jira.util.json.JSONObject;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

/**
 * Client for entity property resource.
 *
 * @since v6.2
 */
public class EntityPropertyClient extends RestApiClient<EntityPropertyClient>
{
    private final String propertyName;

    /**
     * Constructs an entity property client for JIRA instance.
     *
     * @param environmentData The JIRA environmental data.
     */
    public EntityPropertyClient(final JIRAEnvironmentData environmentData, String propertyName)
    {
        super(environmentData);
        this.propertyName = propertyName;
    }

    /**
     * Gets the properties keys for the entity with given id or key.
     * @param entityKeyOrId key or id of an entity.
     * @return list of entity properties keys.
     */
    public EntityPropertyKeys getKeys(String entityKeyOrId)
    {
        return resource(entityKeyOrId).get(EntityPropertyKeys.class);
    }

    /**
     * @param entitykeyOrId key or id of an entity.
     * @param propertyKey key of the property to return.
     * @return returns the property for the particular entity and property key.
     */
    public EntityProperty get(String entitykeyOrId, String propertyKey)
    {
        return resource(entitykeyOrId, propertyKey).get(EntityProperty.class);
    }

    /**
     * Sets the value of the property with given key, associated with a given entity.
     * @param entityKeyOrId key or id of an entity.
     * @param propertyKey key of the property.
     * @param value value of the property.
     */
    public void put(final String entityKeyOrId, final String propertyKey, final JSONObject value)
    {
        resource(entityKeyOrId, propertyKey).header("Content-Type", "application/json").put(String.class, value.toString());
    }

    /**
     * Removes the value of the property with given key, associated with a given entity.
     * @param entityKeyOrId key or id of an entity.
     * @param propertyKey key of the property to remove.
     */
    public void delete(final String entityKeyOrId, final String propertyKey)
    {
        resource(entityKeyOrId, propertyKey).delete();
    }

    /**
     * Removes the value of the property with given key for all issues matched by given jql query
     *
     * @param propertyKey key of the property to remove.
     * @param jql         JQL query
     */
    public void deleteByJQL(final String propertyKey, final String jql) {
        resource(Option.<String>none(), Option.some(propertyKey)).queryParam("jql", jql).delete();
    }

    public WebResource resource(String entityKeyOrId)
    {
        return resource(Option.<String>some(entityKeyOrId), Option.<String>none());
    }

    public WebResource resource(String entityKeyOrId, String propertyKey)
    {
        return resource(Option.<String>some(entityKeyOrId), Option.some(propertyKey));
    }

    private WebResource resource(final Option<String> entityKeyOrId, final Option<String> propertyKey)
    {
        final WebResource webResource = createResource().path(propertyName).path(entityKeyOrId.getOrElse("")).path("properties");
        return webResource.path(propertyKey.getOrElse(""));
    }

}
