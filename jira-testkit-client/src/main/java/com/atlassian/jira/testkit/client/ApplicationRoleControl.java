package com.atlassian.jira.testkit.client;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.atlassian.jira.testkit.client.restclient.Response;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import static java.util.Collections.singleton;

public class ApplicationRoleControl extends RestApiClient<ApplicationRoleControl>
{
    private final DarkFeaturesControl darkFeature;
    private final RolesEnabledControl rolesEnabled;

    private static final String CORE_KEY = "jira-core";

    public ApplicationRoleControl(JIRAEnvironmentData environmentData, DarkFeaturesControl darkFeature, RolesEnabledControl rolesEnabled)
    {
        super(environmentData);
        this.darkFeature = darkFeature;
        this.rolesEnabled = rolesEnabled;
    }

    public List<ApplicationRoleBean> getRoles()
    {
        return createApplicationRoleResource().get(ApplicationRoleBean.LIST);
    }

    public Map<String, ApplicationRoleBean> getRolesMap()
    {
        return Maps.uniqueIndex(getRoles(), ApplicationRoleBean.GET_KEY);
    }
    
    /*
     * Copied from JIRA 7.0, Doesn't seem to be compatible with java 7 even after converting lambda to anon class.
     *
       
    public Response<List<ApplicationRoleBean>> getRolesResponse()
    {
        return toResponse(new Method() {
            @Override
            public ClientResponse call() {
                return createApplicationRoleResource().get(ClientResponse.class);
            }
        }, ApplicationRoleBean.LIST);
    }
    */
    
    public ApplicationRoleBean getRole(String role)
    {
        return createApplicationRoleResource().path(role).get(ApplicationRoleBean.class);
    }

    public ApplicationRoleBean getCore()
    {
        return getRole(CORE_KEY);
    }

    public Response<ApplicationRoleBean> getRoleResponse(final String role)
    {
        return toResponse(new Method() 
        {
            @Override
            public ClientResponse call() 
            {
                return createApplicationRoleResource().path(role).get(ClientResponse.class);
            }
        }, ApplicationRoleBean.class);
    }

    public ApplicationRoleBean putRole(String role, String... groups)
    {
        return createApplicationRoleResource().path(role)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(ApplicationRoleBean.class, new ApplicationRoleBean(groups));
    }

    public ApplicationRoleBean putRoleWithDefaults(String role, Iterable<String> groups, Iterable<String> defaultGroups)
    {
        return createApplicationRoleResource().path(role)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(ApplicationRoleBean.class, new ApplicationRoleBean(groups)
                        .setDefaultGroups(ImmutableList.copyOf(defaultGroups)));
    }

    public ApplicationRoleBean putRoleSelectedByDefault(final String role, final boolean selectedByDefault)
    {
        return createApplicationRoleResource().path(role)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(ApplicationRoleBean.class, new ApplicationRoleBean(selectedByDefault));
    }

    public ApplicationRoleBean putRoleWithDefaultsSelectedByDefault(final String role, final boolean selectedByDefault,
                                                                    final Iterable<String> groups, final Iterable<String> defaultGroups)
    {
        return createApplicationRoleResource().path(role)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .put(ApplicationRoleBean.class, new ApplicationRoleBean(groups)
                        .setSelectedByDefault(selectedByDefault)
                        .setDefaultGroups(ImmutableList.copyOf(defaultGroups)));
    }

    /**
     * Sets the application role with the passed group. The passed group will also become one of the defaults.
     *
     * @param role the role to update.
     * @param group the group to add to to role.
     * @return the actual data from the server.
     */
    public ApplicationRoleBean putRoleAndSetDefault(String role, String group)
    {
        return putRoleWithDefaults(role, singleton(group), singleton(group));
    }

    public Response<ApplicationRoleBean> putRoleResponse(final String role, final String... groups)
    {
        return toResponse(new Method() 
        {
            @Override
            public ClientResponse call() 
            {
                return createApplicationRoleResource().path(role)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .put(ClientResponse.class, new ApplicationRoleBean(groups));
            }
        }, ApplicationRoleBean.class);
    }

    public boolean isEnabled()
    {
        return rolesEnabled.isRolesEnabled() || darkFeature.isGlobalEnabled("com.atlassian.jira.config.CoreFeatures.LICENSE_ROLES_ENABLED");
    }

    public Response<ApplicationRoleBean> putSelectedByDefaultResponse(final String role, final boolean selectedByDefault)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createApplicationRoleResource().path(role)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .put(ClientResponse.class, new ApplicationRoleBean(selectedByDefault));
            }
        }, ApplicationRoleBean.class);
    }

    public void enable()
    {
        darkFeature.enableForSite("com.atlassian.jira.config.CoreFeatures.LICENSE_ROLES_ENABLED");
    }

    public void disable()
    {
        darkFeature.disableForSite("com.atlassian.jira.config.CoreFeatures.LICENSE_ROLES_ENABLED");
    }

    private WebResource createApplicationRoleResource()
    {
        return createResource().path("applicationrole");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApplicationRoleBean
    {
        private static final GenericType<List<ApplicationRoleBean>> LIST = new GenericType<List<ApplicationRoleBean>>()
        {
        };

        private static final Function<ApplicationRoleBean, String> GET_KEY = new Function<ApplicationRoleBean, String>()
        {
            @Override
            public String apply(final ApplicationRoleBean input)
            {
                return input.key;
            }
        };

        @JsonProperty
        private String name;

        @JsonProperty
        private String key;

        @JsonProperty
        private List<String> groups;

        @JsonProperty
        private List<String> defaultGroups;

        @JsonProperty
        private boolean selectedByDefault;

        @JsonProperty
        private Integer numberOfSeats;

        @JsonProperty
        private Integer remainingSeats;

        @JsonProperty
        private Integer userCount;

        @JsonProperty
        private Boolean hasUnlimitedSeats;

        public ApplicationRoleBean()
        {
            groups = Lists.newArrayList();
        }

        private ApplicationRoleBean(String... groups)
        {
            this.groups = ImmutableList.copyOf(groups);
        }

        private ApplicationRoleBean(Iterable<String> groups)
        {
            this.groups = ImmutableList.copyOf(groups);
        }

        private ApplicationRoleBean(boolean selectedByDefault)
        {
            this.selectedByDefault = selectedByDefault;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this)
                    .append("name", name)
                    .append("id", key)
                    .append("groups", groups)
                    .append("selectedByDefault", selectedByDefault)
                    .append("numberOfSeats", numberOfSeats)
                    .append("remainingSeats", remainingSeats)
                    .append("userCount", userCount)
                    .append("hasUnlimitedSeats", hasUnlimitedSeats)
                    .toString();
        }

        public String getName()
        {
            return name;
        }

        public String getKey()
        {
            return key;
        }

        public List<String> getGroups()
        {
            return groups;
        }

        public List<String> getDefaultGroups()
        {
            return defaultGroups;
        }

        public boolean isSelectedByDefault()
        {
            return selectedByDefault;
        }

        public Integer getNumberOfSeats()
        {
            return numberOfSeats;
        }

        public Integer getRemainingSeats()
        {
            return remainingSeats;
        }

        public Integer getUserCount()
        {
            return userCount;
        }

        public Boolean getHasUnlimitedSeats()
        {
            return hasUnlimitedSeats;
        }

        public ApplicationRoleBean setDefaultGroups(List<String> defaultGroups)
        {
            this.defaultGroups = ImmutableList.copyOf(defaultGroups);
            return this;
        }

        public ApplicationRoleBean setSelectedByDefault(final boolean selectedByDefault)
        {
            this.selectedByDefault = selectedByDefault;
            return this;
        }

        public ApplicationRoleBean setNumberOfSeats(final Integer numberOfSeats)
        {
            this.numberOfSeats = numberOfSeats;
            return this;
        }

        public ApplicationRoleBean setRemainingSeats(final Integer remainingSeats)
        {
            this.remainingSeats = remainingSeats;
            return this;
        }

        public ApplicationRoleBean setUserCount(final Integer userCount)
        {
            this.userCount = userCount;
            return this;
        }

        public ApplicationRoleBean setHasUnlimitedSeats(final boolean hasUnlimitedSeats)
        {
            this.hasUnlimitedSeats = hasUnlimitedSeats;
            return this;
        }
    }
}