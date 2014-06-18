package com.atlassian.jira.testkit.issue.fields.layout.field;

import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Jira62FieldLayoutManagerAdapterImpl implements FieldLayoutManagerAdapter
{
    private final FieldLayoutManager delegate;

    public Jira62FieldLayoutManagerAdapterImpl(FieldLayoutManager delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public FieldLayoutScheme createFieldLayoutScheme(@Nonnull String name, @Nullable String description)
    {
        return delegate.createFieldLayoutScheme(name, description);
    }
}
