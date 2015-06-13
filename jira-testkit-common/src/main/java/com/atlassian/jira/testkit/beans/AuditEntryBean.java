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
    @JsonProperty public boolean isAuthorSysAdmin;
    @JsonProperty public String categoryName;
    @JsonProperty public ApplicationUser author;
    @JsonProperty public String remoteAddress;
    @JsonDeserialize(as=DummyAssociatedItem.class)
    @JsonProperty public AssociatedItem objectItem = new DummyAssociatedItem();
    @JsonProperty public Iterable<ChangedValue> changedValues;
    @JsonProperty public Iterable<AssociatedItem> associatedItems;
    @JsonProperty public String description;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DummyAssociatedItem implements AssociatedItem
    {

        @Nonnull
        @Override
        public String getObjectName()
        {
            return "dummy";
        }

        @Nullable
        @Override
        public String getObjectId()
        {
            return null;
        }

        @Nullable
        @Override
        public String getParentName()
        {
            return null;
        }

        @Nullable
        @Override
        public String getParentId()
        {
            return null;
        }

        @Nonnull
        @Override
        public Type getObjectType()
        {
            return Type.APPLICATION_ROLE;
        }
    }
}
