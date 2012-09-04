package com.atlassian.jira.testkit.client.restclient;

import com.sun.jersey.api.client.GenericType;

import java.util.List;

public class ScreenTab
{
    public static final GenericType<List<ScreenTab>> LIST = new GenericType<List<ScreenTab>>()
    {
    };
    public Long id;
    public String name;

    public ScreenTab(){}

    public ScreenTab(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        ScreenTab screenTab = (ScreenTab) o;

        if (name != null ? !name.equals(screenTab.name) : screenTab.name != null) { return false; }

        return true;
    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }
}
