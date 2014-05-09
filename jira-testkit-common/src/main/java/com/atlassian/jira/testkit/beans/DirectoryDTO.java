package com.atlassian.jira.testkit.beans;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A Data Transfer Object for serialization of a {@link com.atlassian.crowd.embedded.api.Directory} to and from JSON.
 */
public class DirectoryDTO
{
    @JsonProperty
    private final Long id;

    @JsonProperty
    private final String name;

    @JsonProperty
    private final boolean active;

    @JsonProperty
    private final String encryptionType;

    @JsonProperty
    private final Map<String, String> attributes;

    @JsonProperty
    private final Set<OperationType> allowedOperations;

    @JsonProperty
    private final String description;

    @JsonProperty
    private final DirectoryType type;

    @JsonProperty
    private final String implementationClass;

    @JsonProperty
    private final Date createdDate;

    @JsonProperty
    private final Date updatedDate;

    @JsonCreator
    public DirectoryDTO(
            @JsonProperty("id") final Long id,
            @JsonProperty("name") final String name,
            @JsonProperty("active") final boolean active,
            @JsonProperty("encryptionType") final String encryptionType,
            @JsonProperty("attributes") final Map<String, String> attributes,
            @JsonProperty("allowedOperations") final Set<OperationType> allowedOperations,
            @JsonProperty("description") final String description,
            @JsonProperty("type") final DirectoryType type,
            @JsonProperty("implementationClass") final String implementationClass,
            @JsonProperty("createdDate") final Date createdDate,
            @JsonProperty("updatedDate") final Date updatedDate
    )
    {
        this.id = id;
        this.name = name;
        this.active = active;
        this.encryptionType = encryptionType;
        this.attributes = attributes;
        this.allowedOperations = allowedOperations;
        this.description = description;
        this.type = type;
        this.implementationClass = implementationClass;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public DirectoryDTO(final Directory directory)
    {
        this(
                directory.getId(),
                directory.getName(),
                directory.isActive(),
                directory.getEncryptionType(),
                directory.getAttributes(),
                directory.getAllowedOperations(),
                directory.getDescription(),
                directory.getType(),
                directory.getImplementationClass(),
                directory.getCreatedDate(),
                directory.getUpdatedDate()
        );
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public boolean isActive()
    {
        return active;
    }

    public String getEncryptionType()
    {
        return encryptionType;
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public Set<OperationType> getAllowedOperations()
    {
        return allowedOperations;
    }

    public String getDescription()
    {
        return description;
    }

    public DirectoryType getType()
    {
        return type;
    }

    public String getImplementationClass()
    {
        return implementationClass;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public Date getUpdatedDate()
    {
        return updatedDate;
    }
}
