package com.atlassian.jira.testkit.beans;

import com.atlassian.jira.auditing.AssociatedItem;
import com.atlassian.jira.auditing.AuditingCategory;
import com.atlassian.jira.auditing.ChangedValue;
import com.atlassian.jira.user.ApplicationUser;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @since 7.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class AuditEntryBean
{
    @JsonProperty public AuditingCategory category;
    @JsonProperty public String summaryI18nKey;
    @JsonProperty public String eventSource;
    @JsonProperty public ApplicationUser author;
    @JsonProperty public String remoteAddress;
    @JsonProperty public String description;
}
