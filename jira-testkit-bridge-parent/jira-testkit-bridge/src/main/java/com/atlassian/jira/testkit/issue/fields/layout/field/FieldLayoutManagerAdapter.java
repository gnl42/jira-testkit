package com.atlassian.jira.testkit.issue.fields.layout.field;

import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.String;

public interface FieldLayoutManagerAdapter
{
    /**
     * @since JIRA 6.2
     */
    FieldLayoutScheme createFieldLayoutScheme(@Nonnull String name, @Nullable String description);
}
