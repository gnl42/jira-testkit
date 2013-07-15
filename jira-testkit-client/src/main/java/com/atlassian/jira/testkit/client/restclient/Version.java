/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.sun.jersey.api.client.GenericType;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Representation of a version in the JIRA REST API.
 *
 * @since v4.3
 */
@JsonSerialize (include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties (ignoreUnknown = true)
public class Version
{
    public static final GenericType<List<Version>> VERSIONS_TYPE = new GenericType<List<Version>>(){};
    private static final String DATE_FORMAT = "dd/MMM/yy";

    public String self;
    public String expand;
    public String name;
    public String description;
    public Long id;
    public Boolean released;
    public Boolean archived;
    public Boolean overdue;
    public String userStartDate;
    public String userReleaseDate;
    public String project;
    public List<SimpleLink> operations;

    @XmlJavaTypeAdapter (LocalDateAdapter.class)
    public LocalDate startDate;
    @XmlJavaTypeAdapter (LocalDateAdapter.class)
    public LocalDate releaseDate;

    public Version self(URI self)
    {
        this.self = self.toString();
        return this;
    }

    public Version self(String self)
    {
        this.self = self;
        return this;
    }

    public Version name(String name)
    {
        this.name = name;
        return this;
    }

    public Version description(String description)
    {
        this.description = description;
        return this;
    }

    public Version released(Boolean released)
    {
        this.released = released;
        return this;
    }

    public Version archived(Boolean archived)
    {
        this.archived = archived;
        return this;
    }

    public Version overdue(Boolean overdue)
    {
        this.overdue = overdue;
        return this;
    }

    public Version id(Long id)
    {
        this.id = id;
        return this;
    }

    public Version startDate(LocalDate startDate)
    {
        this.startDate = startDate;
        return this;
    }

    public Version startDate(String startDate)
    {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try
        {
            return startDate(startDate, LocalDate.fromDateFields(format.parse(startDate)));
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Version startDate(String startDate, LocalDate startDateObject)
    {
        this.startDate = startDateObject;
        this.userStartDate = startDate;
        return this;
    }

    public Version releaseDate(LocalDate releaseDate)
    {
        this.releaseDate = releaseDate;
        return this;
    }

    public Version releaseDate(String releaseDate)
    {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try
        {
            return releaseDate(releaseDate, LocalDate.fromDateFields(format.parse(releaseDate)));
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Version releaseDate(String releaseDate, LocalDate releaseDateObject)
    {
        this.releaseDate = releaseDateObject;
        this.userReleaseDate = releaseDate;
        return this;
    }

    public Version project(String project)
    {
        this.project = project;
        return this;
    }

    public Version userStartDate(String userStartDate)
    {
        this.userStartDate = userStartDate;
        return this;
    }

    public Version userReleaseDate(String userReleaseDate)
    {
        this.userReleaseDate = userReleaseDate;
        return this;
    }

    public Version operations(List<SimpleLink> operations)
    {
        this.operations = operations;
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
