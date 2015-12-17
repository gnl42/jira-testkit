package com.atlassian.jira.testkit.client.restclient;

import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @since v7.0.5
 */
public class DeleteVersionWithCustomFieldParameters {

    private Long moveFixIssuesTo;
    private Long moveAffectedIssuesTo;

    private List<CustomFieldReplacement> customFieldReplacementList;

    public DeleteVersionWithCustomFieldParameters(@Nullable Long moveFixIssuesTo,
                                                  @Nullable Long moveAffectedIssuesTo,
                                                  @Nullable List<CustomFieldReplacement> customFieldReplacementList) {
        this.moveFixIssuesTo = moveFixIssuesTo;
        this.moveAffectedIssuesTo = moveAffectedIssuesTo;
        this.customFieldReplacementList = customFieldReplacementList;
    }

    public Long getMoveFixIssuesTo() {
        return moveFixIssuesTo;
    }

    public Long getMoveAffectedIssuesTo() {
        return moveAffectedIssuesTo;
    }

    public List<CustomFieldReplacement> getCustomFieldReplacementList() {
        return customFieldReplacementList;
    }

    public static class CustomFieldReplacement {
        Long customFieldId;
        Long moveTo;

        public CustomFieldReplacement(@Nullable Long customFieldId, @Nullable Long moveTo) {
            this.customFieldId = customFieldId;
            this.moveTo = moveTo;
        }

        public Long getCustomFieldId() {
            return customFieldId;
        }

        public Long getMoveTo() {
            return moveTo;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private Long moveFixedIssuesTo;
        private Long moveAffectedIssuesTo;
        private List<CustomFieldReplacement> customFieldReplacementList;

        public Builder moveFixIssuesTo(long moveFixIssuesTo) {
            this.moveFixedIssuesTo = moveFixIssuesTo;
            return this;
        }

        public Builder moveAffectedIssuesTo(long moveAffectedIssuesTo) {
            this.moveAffectedIssuesTo = moveAffectedIssuesTo;
            return this;
        }

        public Builder moveCustomFieldTo(long customFieldId, long moveCustomFieldTo) {
            customFieldsReplacements().add(new CustomFieldReplacement(customFieldId, moveCustomFieldTo));
            return this;
        }

        public Builder deleteCustomField(long customFieldId, long moveCustomFieldTo) {
            customFieldsReplacements().add(new CustomFieldReplacement(customFieldId, null));
            return this;
        }

        public DeleteVersionWithCustomFieldParameters build() {
            return new DeleteVersionWithCustomFieldParameters(
                    moveFixedIssuesTo,
                    moveAffectedIssuesTo,
                    customFieldReplacementList);
        }

        private List<CustomFieldReplacement> customFieldsReplacements() {
            if (customFieldReplacementList == null) {
                customFieldReplacementList = Lists.newArrayList();
            }
            return customFieldReplacementList;
        }
    }
}
