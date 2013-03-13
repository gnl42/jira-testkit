/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import java.util.List;

public class LinkGroup
{
    private String id;
    private SimpleLink header;
    private List<SimpleLink> links;
    private List<LinkGroup> groups;
    private Integer weight;

    public LinkGroup()
    {

    }

    public String getId()
    {
        return id;
    }

    public SimpleLink getHeader()
    {
        return header;
    }

    public List<SimpleLink> getLinks()
    {
        return links;
    }

    public List<LinkGroup> getGroups()
    {
        return groups;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setHeader(SimpleLink header)
    {
        this.header = header;
    }

    public void setLinks(List<SimpleLink> links)
    {
        this.links = links;
    }

    public void setGroups(List<LinkGroup> groups)
    {
        this.groups = groups;
    }

    public Integer getWeight()
    {
        return weight;
    }

    public void setWeight(Integer weight)
    {
        this.weight = weight;
    }
}
