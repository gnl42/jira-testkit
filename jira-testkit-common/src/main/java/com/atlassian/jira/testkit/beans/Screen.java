package com.atlassian.jira.testkit.beans;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;

public class Screen implements Iterable<Screen.Tab>
{
    @JsonProperty
    private long id;

    @JsonProperty
    private String name;

    @JsonProperty
    private List<Tab> tabs;

    public Screen()
    {
    }

    public Screen(final long id, final String name, final Iterable<? extends Tab> tabs)
    {
        this.id = id;
        this.name = name;
        this.tabs = copyOf(tabs);
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public List<Tab> getTabs()
    {
        return tabs;
    }

    @Override
    public Iterator<Tab> iterator()
    {
        return getTabs().iterator();
    }

    public List<String> getFields()
    {
        final List<String> fields = Lists.newArrayList();
        for (Tab tab : this)
        {
            Iterables.addAll(fields, Iterables.transform(tab, Field.GET_NAME));
        }
        return fields;
    }

    public static class Tab implements Iterable<Field>
    {
        @JsonProperty
        private String name;

        @JsonProperty
        private Long id;

        @JsonProperty
        private List<Field> fields;

        public Tab()
        {
        }

        public Tab(final String name, final Iterable<? extends Field> fields)
        {
            this(null, name, fields);
        }

        public Tab(final Long id, final String name, final Iterable<? extends Field> fields)
        {
            this.fields = copyOf(fields);
            this.name = name;
            this.id = id;
        }


        public List<Field> getFields()
        {
            return fields;
        }

        public String getName()
        {
            return name;
        }

        public Long getId() {
            return id;
        }

        @Override
        public Iterator<Field> iterator()
        {
            return fields.iterator();
        }
    }
}
