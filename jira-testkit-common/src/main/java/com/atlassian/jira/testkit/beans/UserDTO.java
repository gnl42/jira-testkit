package com.atlassian.jira.testkit.beans;

import com.atlassian.jira.user.ApplicationUser;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A Data Transfer Object for serialization of an ApplicationUser into JSON.
 */
@SuppressWarnings("all")
public class UserDTO
{
    @JsonProperty
    private final boolean active;

    @JsonProperty
    private final long directoryId;

    @JsonProperty
    private final String displayName;

    @JsonProperty
    private final String email;

    @JsonProperty
    private final String key;

    @JsonProperty
    private final String name;

    @JsonProperty
    private final String username;

    public UserDTO(final ApplicationUser user)
    {
        this.active = user.isActive();
        this.directoryId = user.getDirectoryId();
        this.displayName = user.getDisplayName();
        this.email = user.getEmailAddress();
        this.key = user.getKey();
        this.name = user.getName();
        this.username = user.getUsername();
    }
}
