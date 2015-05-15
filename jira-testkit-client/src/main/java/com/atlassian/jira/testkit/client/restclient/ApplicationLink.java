package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.plugins.rest.common.Link;

import java.net.URI;
import java.util.List;

/**
 * TODO: Document this class / interface here
 *
 * @since v6.4
 */
public class ApplicationLink
{
    public String id;
    public String typeId;
    public String name;
    public URI displayUrl;
    public URI iconUrl;
    public Boolean isPrimary;
    public Boolean isSystem;
    public Boolean primary;
    public Boolean system;
    public List<Link> link;
}
