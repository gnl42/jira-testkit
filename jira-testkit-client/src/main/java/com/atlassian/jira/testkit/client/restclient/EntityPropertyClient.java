package com.atlassian.jira.testkit.client.restclient;

import io.atlassian.fugue.Option;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.atlassian.jira.util.json.JSONObject;
import com.sun.jersey.api.client.WebResource;

/**
 * Client for entity property resource.
 *
 * @since v6.2
 */
public class EntityPropertyClient extends RestApiClient<EntityPropertyClient>
{
    protected final String entityName;

    /**
     * Constructs an entity property client for JIRA instance.
     *
     * @param environmentData the JIRA environmental data
     * @param entityName the property name
     */
    public EntityPropertyClient(final JIRAEnvironmentData environmentData, String entityName)
    {
        super(environmentData);
        this.entityName = entityName;
    }

    /**
     * Gets the properties keys for the entity with given id or key.
     *
     * @param entityKeyOrId key or id of an entity.
     * @return list of entity properties keys.
     */
    public EntityPropertyKeys getKeys(String entityKeyOrId)
    {
        return resource(entityKeyOrId).get(EntityPropertyKeys.class);
    }

    /**
     * Returns the property for the given entity and property key.
     *
     * @param entitykeyOrId key or id of an entity.
     * @param propertyKey key of the property to return.
     * @return see above
     */
    public EntityProperty get(String entitykeyOrId, String propertyKey)
    {
        return resource(entitykeyOrId, propertyKey).get(EntityProperty.class);
    }

    /**
     * Sets the value of the property with given key, associated with a given entity.
     *
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
     *
     * @param entityKeyOrId key or id of an entity.
     * @param propertyKey key of the property to remove.
     */
    public void delete(final String entityKeyOrId, final String propertyKey)
    {
        resource(entityKeyOrId, propertyKey).delete();
    }

    public WebResource resource(String entityKeyOrId)
    {
        return resource(Option.<String>some(entityKeyOrId), Option.<String>none());
    }

    public WebResource resource(String entityKeyOrId, String propertyKey)
    {
        return resource(Option.<String>some(entityKeyOrId), Option.some(propertyKey));
    }

    protected WebResource resource(final Option<String> entityKeyOrId, final Option<String> propertyKey)
    {
        WebResource webResource = createResource().path(entityName);

        if ("user".equals(entityName)) {
            webResource = webResource.queryParam("username", entityKeyOrId.getOrElse(""));
        } else {
            webResource = webResource.path(entityKeyOrId.getOrElse(""));
        }

        return webResource.path("properties").path(propertyKey.getOrElse(""));
    }
}
