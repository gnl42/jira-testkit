package com.atlassian.jira.testkit.beans;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class PermissionSchemeAttributeBean
{
    @JsonProperty
    private String key;
    @JsonProperty
    private String value;

    public PermissionSchemeAttributeBean() {
    }

    public PermissionSchemeAttributeBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public PermissionSchemeAttributeBean setKey(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public PermissionSchemeAttributeBean setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return "PermissionSchemeAttributeBean{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionSchemeAttributeBean that = (PermissionSchemeAttributeBean) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
