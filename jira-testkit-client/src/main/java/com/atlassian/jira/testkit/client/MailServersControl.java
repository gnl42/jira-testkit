/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Use this class from func/selenium/page-object tests that need to manipulate Mail Servers.
 *
 * See {@link com.atlassian.jira.testkit.plugin.MailServersBackdoor} in jira-testkit-plugin for backend.
 *
 * @since v5.0
 */
public class MailServersControl extends BackdoorControl<MailServersControl>
{
    public static final String DEFAULT_FROM_ADDRESS = "jiratest@atlassian.com";
    public static final String DEFAULT_SUBJECT_PREFIX = "[JIRATEST]";

    public MailServersControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * Adds an SMTP server with default parameters and a specified port.
     *
     * Deletes any existing SMTP servers first.
     *
     * @param port The SMTP port of your mail server.
     */
    public void addSmtpServer(int port)
    {
        addSmtpServer(DEFAULT_FROM_ADDRESS, DEFAULT_SUBJECT_PREFIX, port);
    }

    /**
     * Adds an SMTP server with the specified parameters.
     *
     * Deletes any existing SMTP servers first.
     *
     * @param from The default address this server will use to send emails from.
     * @param prefix The prefix for all outgoing email subjects.
     * @param port The SMTP port of your mail server.
     */
    public void addSmtpServer(String from, String prefix, int port)
    {
        addSmtpServer("Local Test Server", "", from, prefix, "localhost", port);
    }

    /**
     * Adds an SMTP server with the specified parameters.
     *
     * Deletes any existing SMTP servers first.
     *
     * @param name The name of this server within JIRA.
     * @param description The description for this server.
     * @param from The default address this server will use to send emails from.
     * @param prefix The prefix for all outgoing email subjects.
     * @param serverName The SMTP host name of your mail server.
     * @param port The SMTP port of your mail server.
     */
    public void addSmtpServer(String name, String description, String from, String prefix, String serverName,
            int port)
    {
        addSmtpServer(name, description, from, prefix, serverName, port, false);
    }

    /**
     * Adds an SMTP server with the specified parameters.
     *
     * Deletes any existing SMTP servers first.
     *
     * @param name The name of this server within JIRA.
     * @param description The description for this server.
     * @param from The default address this server will use to send emails from.
     * @param prefix The prefix for all outgoing email subjects.
     * @param serverName The SMTP host name of your mail server.
     * @param port The SMTP port of your mail server.
     * @param tls Should TLS be enabled?
     */
    public void addSmtpServer(String name, String description, String from, String prefix, String serverName,
                              int port, boolean tls)
    {
        addSmtpServer(name, description, from, prefix, serverName, port, null, null, tls);
    }

    /**
     * Adds an SMTP server with the specified parameters.
     *
     * Deletes any existing SMTP servers first.
     *
     * @param name The name of this server within JIRA.
     * @param description The description for this server.
     * @param from The default address this server will use to send emails from.
     * @param prefix The prefix for all outgoing email subjects.
     * @param serverName The SMTP host name of your mail server.
     * @param port The SMTP port of your mail server.
     * @param username The username for authentication.
     * @param password The password for authentication.
     * @param tls Should TLS be enabled?
     */
    public void addSmtpServer(String name, String description, String from, String prefix, String serverName,
                              int port, String username, String password, boolean tls)
    {
        MailServersBean bean = new MailServersBean();
        bean.name = name;
        bean.description = description;
        bean.from = from;
        bean.prefix = prefix;
        bean.serverName = serverName;
        bean.port = String.valueOf(port);
        bean.tls = tls;
        bean.username = username;
        bean.password = password;
        createResource().path("mailServers/smtp").post(String.class, bean);
    }


    /**
     * Adds a POP server with the specified parameters.
     *
     * @param name The name of this server within JIRA.
     * @param port The POP port of your mail server.
     */
    public void addPopServer(String name, int port)
    {
        addPopServer(name, "", "pop3", "localhost", port, "username", "password");
    }

    /**
     * Adds an IMAP server with the specified parameters.
     *
     * @param name The name of this server within JIRA.
     * @param port The IMAP port of your mail server.
     */
    public void addImapServer(String name, int port)
    {
        addPopServer(name, "", "imap", "localhost", port, "username", "password");
    }
    /**
    * Adds a POP/IMAP server with the specified parameters.
    *
    * @param name The name of this server within JIRA.
    * @param description The description for this server.
    * @param protocol The server protocol (one of pop3, pop3s, imap, imaps).
    * @param serverName The POP/IMAP host name of your mail server.
    * @param port The POP/IMAP port of your mail server.
     * @param username Username.
     * @param password Password.
     */
    public void addPopServer(String name, String description, String protocol, String serverName,
            int port, String username, String password)
    {
        MailServersBean bean = new MailServersBean();
        bean.name = name;
        bean.description = description;
        bean.protocol = protocol;
        bean.serverName = serverName;
        bean.port = String.valueOf(port);
        bean.username = username;
        bean.password = password;
        createResource().path("mailServers/pop").post(String.class, bean);
    }

    /**
     * Flushing mail queue.
     */
    public void flushMailQueue()
    {
        final WebResource webResourcePath = createResource().path("mailServers/flush");
        final ClientResponse clientResponse = webResourcePath.get(ClientResponse.class);
        assertThat("Clinet response status should be equal to \"OK\"", clientResponse.getStatus(), equalTo(Response.Status.OK.getStatusCode()));
    }

    static class MailServersBean
    {
        public String name;
        public String description;
        public String protocol;
        public String serverName;
        public String port;
        public String username;
        public String password;
        public String from;
        public String prefix;
        public boolean tls;
    }

}
