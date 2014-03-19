package com.atlassian.jira.testkit.beans;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.user.ApplicationUser;
import org.apache.commons.lang3.Validate;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * A Data Transfer Object for serialization of an ApplicationUser to and from JSON.
 */
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

    @JsonCreator
    public UserDTO(
            @JsonProperty("active") final boolean active,
            @JsonProperty("directoryId") final long directoryId,
            @JsonProperty("displayName") final String displayName,
            @JsonProperty("email") final String email,
            @JsonProperty("key") final String key,
            @JsonProperty("name") final String name,
            @JsonProperty("username") final String username)
    {
        Validate.notBlank(key);
        Validate.notBlank(username);
        this.active = active;
        this.directoryId = directoryId;
        this.displayName = displayName;
        this.email = email;
        this.key = key;
        this.name = name;
        this.username = username;
    }

    public UserDTO(final ApplicationUser user)
    {
        this(
                user.isActive(),
                user.getDirectoryId(),
                user.getDisplayName(),
                user.getEmailAddress(),
                user.getKey(),
                user.getName(),
                user.getUsername()
        );
    }

    @SuppressWarnings("unused")
    public long getDirectoryId()
    {
        return directoryId;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getEmail()
    {
        return email;
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getUsername()
    {
        return username;
    }

    @SuppressWarnings("unused")
    public boolean isActive()
    {
        return active;
    }

    // For debugging only
    @Override
    public String toString()
    {
        return reflectionToString(this, SHORT_PREFIX_STYLE);
    }

    /**
     * Returns an ApplicationUser whose values reflect those of this DTO.
     *
     * @param directoryUser the directory user to which to delegate as
     * necessary (required)
     * @return a non-null result
     */
    public ApplicationUser asApplicationUser(final User directoryUser)
    {
        Validate.notNull(directoryUser);
        return new ApplicationUser()
        {
            @Override
            public String getKey()
            {
                return key;
            }

            @Override
            public String getUsername()
            {
                return username;
            }

            @Override
            public String getName()
            {
                return name;
            }

            @Override
            public long getDirectoryId()
            {
                return directoryId;
            }

            @Override
            public boolean isActive()
            {
                return active;
            }

            @Override
            public String getEmailAddress()
            {
                return email;
            }

            @Override
            public String getDisplayName()
            {
                return displayName;
            }

            @Override
            public User getDirectoryUser()
            {
                // Sadly, ApplicationUser and User share all these properties
                return new User() {

                    @Override
                    public long getDirectoryId()
                    {
                        return directoryId;
                    }

                    @Override
                    public String getDisplayName()
                    {
                        return displayName;
                    }

                    @Override
                    public String getEmailAddress()
                    {
                        return email;
                    }

                    @Override
                    public String getName()
                    {
                        return name;
                    }

                    @Override
                    public boolean isActive()
                    {
                        return active;
                    }

                    @Override
                    public int compareTo(final User user)
                    {
                        return directoryUser.compareTo(user);
                    }
                };
            }
        };
    }
}
