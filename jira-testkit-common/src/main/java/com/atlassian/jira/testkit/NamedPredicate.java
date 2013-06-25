package com.atlassian.jira.testkit;

import com.atlassian.jira.util.Named;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

/**
 *
 * @since v6.0.29
 */
public class NamedPredicate implements Predicate<Named>
{
    private final String name;

    public NamedPredicate(final String name)
    {
        this.name = name;
    }

    @Override
    public boolean apply(@Nullable final Named input)
    {
        return input != null && name.equals(input.getName());
    }
}
