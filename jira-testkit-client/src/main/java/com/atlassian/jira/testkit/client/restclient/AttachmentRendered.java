package com.atlassian.jira.testkit.client.restclient;

import java.util.Map;

/**
 * @since v5.0
 */
public class AttachmentRendered
{
    public String id;
    public String self;
    public String filename;
    public User author;
    public String created;
    public String size;
    public String mimeType;
    public String content;
    public String thumbnail;
    public Map<String, Object> properties;
}