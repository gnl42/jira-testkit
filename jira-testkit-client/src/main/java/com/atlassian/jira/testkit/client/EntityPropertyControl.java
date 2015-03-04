package com.atlassian.jira.testkit.client;

import com.atlassian.jira.entity.property.EntityPropertyType;
import com.sun.jersey.api.client.WebResource;

public class EntityPropertyControl extends BackdoorControl<EntityPropertyControl> {

    public EntityPropertyControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    public String getProperty(EntityPropertyType type, String entityId, String key)
    {
        return propertyWebResource(type.getDbEntityName(), entityId).path(key).get(String.class);
    }

    public String getProperties(EntityPropertyType type, String entityId, String key)
    {
        return propertyWebResource(type.getDbEntityName(), entityId).get(String.class);
    }

    public void putProperty(EntityPropertyType type, String entityId, String key, String value)
    {
        propertyWebResource(type.getDbEntityName(), entityId).path(key).put(value);
    }

    public void deleteProperty(EntityPropertyType type, String entityId, String key)
    {
        propertyWebResource(type.getDbEntityName(), entityId).path(key).delete();
    }

    private WebResource propertyWebResource(String entityKey, String entityId) {
        return createResource().path("entityproperties").path(entityKey).path(entityId).path("properties");
    }
}
