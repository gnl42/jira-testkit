package com.atlassian.jira.testkit.client.restclient;

import com.sun.jersey.api.client.GenericType;

import java.util.List;

public class ScreenField
{

    public static final GenericType<List<ScreenField>> LIST = new GenericType<List<ScreenField>>()
    {
    };

    public String fieldId;
    public String id;
    public String name;

    public ScreenField()
    {
    }

    public ScreenField(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        ScreenField that = (ScreenField) o;

        if (name != null ? !name.equals(that.name) : that.name != null) { return false; }

        return true;
    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }
}
