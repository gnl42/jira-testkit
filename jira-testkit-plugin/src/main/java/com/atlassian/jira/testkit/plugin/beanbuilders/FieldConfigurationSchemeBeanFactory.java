package com.atlassian.jira.testkit.plugin.beanbuilders;

import com.atlassian.jira.issue.fields.layout.field.FieldConfigurationScheme;
import com.atlassian.jira.testkit.beans.FieldConfigurationSchemeBean;

/**
 * Builds a {@link com.atlassian.jira.testkit.beans.FieldConfigurationSchemeBean}. Has some smarts around when built with a null scheme.
 *
 * @since 6.3
 */
public class FieldConfigurationSchemeBeanFactory
{
    private FieldConfigurationSchemeBeanFactory()
    {
    }

    public static FieldConfigurationSchemeBean toFieldConfigurationSchemeBean(FieldConfigurationScheme fieldConfigurationScheme)
    {
        Long id;
        String name;
        if (fieldConfigurationScheme == null)
        {
            id = null;
            name = "System Default Field Configuration";
        }
        else
        {
            id = fieldConfigurationScheme.getId();
            name = fieldConfigurationScheme.getName();
        }
        return new FieldConfigurationSchemeBean(id, name);
    }
}
