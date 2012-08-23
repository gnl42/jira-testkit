package com.atlassian.jira.tests.util.collect;

import static com.atlassian.jira.tests.util.dbc.Assertions.notNull;

import com.atlassian.jira.tests.util.dbc.Assertions;
import com.atlassian.jira.tests.util.Function;
import com.atlassian.jira.tests.util.NotNull;

import java.util.Iterator;

/**
 * {@link Iterator} implementation that decorates another {@link Iterator} who
 * contains values of type I and uses a {@link com.atlassian.jira.tests.util.Function} that converts that I
 * into a V.
 * <p>
 * This implementation is unmodifiable.
 *
 * @param <I> the value in the underlying iterator
 * @param <E> the value it is converted to
 */
class TransformingIterator<I, E> implements Iterator<E>
{
    private final Iterator<? extends I> iterator;
    private final com.atlassian.jira.tests.util.Function<I, E> decorator;

    TransformingIterator(@com.atlassian.jira.tests.util.NotNull final Iterator<? extends I> iterator, @com.atlassian.jira.tests.util.NotNull final com.atlassian.jira.tests.util.Function<I, E> decorator)
    {
        this.iterator = Assertions.notNull("iterator", iterator);
        this.decorator = Assertions.notNull("decorator", decorator);
    }

    public boolean hasNext()
    {
        return iterator.hasNext();
    }

    public E next()
    {
        return decorator.get(iterator.next());
    }

    public void remove()
    {
        iterator.remove();
    }
}
