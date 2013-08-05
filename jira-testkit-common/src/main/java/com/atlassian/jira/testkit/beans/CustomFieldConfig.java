package com.atlassian.jira.testkit.beans;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Set;

/**
 * @since v6.0.36
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomFieldConfig
{
    public static Predicate<CustomFieldConfig> isGlobalPredicate()
    {
        return new Predicate<CustomFieldConfig>()
        {
            @Override
            public boolean apply(final CustomFieldConfig input)
            {
                return input.isGlobal();
            }
        };
    }

    private Set<String> projects = Sets.newHashSet();
    private Set<String> issueTypes = Sets.newHashSet();
    private List<CustomFieldOption> options = Lists.newArrayList();

    @JsonProperty
    public Set<String> getIssueTypes()
    {
        return issueTypes;
    }

    public void setIssueTypes(final Set<String> issueTypes)
    {
        this.issueTypes = issueTypes;
    }

    @JsonProperty
    public List<CustomFieldOption> getOptions()
    {
        return options;
    }

    public void setOptions(final List<CustomFieldOption> options)
    {
        this.options = options;
    }

    @JsonProperty
    public Set<String> getProjects()
    {
        return projects;
    }

    public void setProjects(final Set<String> projects)
    {
        this.projects = projects;
    }

    @JsonIgnore
    public boolean isGlobal()
    {
        return projects.isEmpty() && issueTypes.isEmpty();
    }
}
