/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.net.URI;
import java.util.List;

/**
 * Representation of a version in the JIRA REST API.
 *
 * @since v4.4
 */
public class VersionIssueCounts {
    public String self;
    public long issuesFixedCount;
    public long issuesAffectedCount;
    public long issueCountWithCustomFieldsShowingVersion;
    public List<VersionUsageInCustomFields> customFieldUsage;

    public VersionIssueCounts self(URI self) {
        this.self = self.toString();
        return this;
    }

    public VersionIssueCounts self(String self) {
        this.self = self;
        return this;
    }

    public VersionIssueCounts issuesFixedCount(long issuesFixedCount) {
        this.issuesFixedCount = issuesFixedCount;
        return this;
    }

    public VersionIssueCounts issuesAffectedCount(long issuesAffectedCount) {
        this.issuesAffectedCount = issuesAffectedCount;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static class VersionUsageInCustomFields {

        public String fieldName;

        public long customFieldId;

        public long issueCountWithVersionInCustomField;

        public VersionUsageInCustomFields() {
        }

        public VersionUsageInCustomFields(long customFieldId, String fieldName, long issueCountWithVersionInCustomField) {
            this.fieldName = fieldName;
            this.customFieldId = customFieldId;
            this.issueCountWithVersionInCustomField = issueCountWithVersionInCustomField;
        }

        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public String toString() {
            return "VersionUsageInCustomFields{" +
                    "fieldName='" + fieldName + '\'' +
                    ", customFieldId=" + customFieldId +
                    ", issueCountWithVersionInCustomField=" + issueCountWithVersionInCustomField +
                    '}';
        }
    }
}
