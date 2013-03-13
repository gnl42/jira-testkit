/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import java.util.Map;

/**
 * Representation of an attachment in the JIRA REST API. Example JSON:
 * <pre>
 * {
 *   self: http://localhost:8090/jira/rest/api/2/attachment/10000
 *   filename: attachment.txt
 *   author: {
 *     self: http://localhost:8090/jira/rest/api/2/user?username=admin
 *     name: admin
 *     displayName: Administrator
 *   }
 *   created: 2010-06-09T15:59:34.602+1000
 *   size: 19
 *   mimeType: text/plain
 *   content: http://localhost:8090/jira/secure/attachment/10000/attachment.txt
 * }
 * </pre>
 *
 * @since v4.3
 */
public class Attachment
{
    public String id;
    public String self;
    public String filename;
    public User author;
    public String created;
    public long size;
    public String mimeType;
    public String content;
    public String thumbnail;
    public Map<String, Object> properties;
}
