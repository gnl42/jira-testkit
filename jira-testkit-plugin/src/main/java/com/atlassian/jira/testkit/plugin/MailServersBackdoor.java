/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.mail.MailException;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.MailProtocol;
import com.atlassian.mail.queue.MailQueue;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.mail.server.impl.PopMailServerImpl;
import com.atlassian.mail.server.impl.SMTPMailServerImpl;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Use this backdoor to manipulate Mail Servers as part of setup for tests.
 *
 * This class should only be called by the <code>com.atlassian.jira.testkit.client.MailServersControl</code>.
 *
 * @since v5.0
 */
@Path ("mailServers")
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
public class MailServersBackdoor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MailServersBackdoor.class);
    private final MailQueue mailQueue;


    public MailServersBackdoor(final MailQueue mailQueue)
    {
        this.mailQueue = mailQueue;
    }

    @POST
    @Path("smtp")
    public Response addSmtpServer(MailServersBean mailServerBean) throws MailException
    {
        // 1. Premptive strike against failing tests - fall over if the SMTP server won't be able to send anything.
        if (MailFactory.isSendingDisabled())
        {
            throw new IllegalStateException("Mail sending is disabled. Please restart your server without"
                    + " -Datlassian.mail.senddisabled=true.");
        }

        // 2. Delete any existing SMTP servers
        MailServerManager mailServerManager = MailFactory.getServerManager();
        List<SMTPMailServer> smtpMailServers = mailServerManager.getSmtpMailServers();
        for (SMTPMailServer smtpMailServer : smtpMailServers)
        {
            mailServerManager.delete(smtpMailServer.getId());
        }

        // 3. Add the new server
        MailServer mailServer = new SMTPMailServerImpl(null, mailServerBean.name, mailServerBean.description,
                mailServerBean.from, mailServerBean.prefix, false, MailProtocol.SMTP, mailServerBean.serverName,
                mailServerBean.port, mailServerBean.tls == null ? false : mailServerBean.tls,
                mailServerBean.username, mailServerBean.password, 10000L);
        mailServerManager.create(mailServer);

        return Response.ok(null).build();
    }

    @POST
    @Path("pop")
    public Response addPopServer(MailServersBean mailServerBean) throws MailException
    {
        PopMailServer mailServer = new PopMailServerImpl(null, mailServerBean.name, mailServerBean.description,
                mailServerBean.serverName, mailServerBean.username, mailServerBean.password);
        if (StringUtils.isNotBlank(mailServerBean.port))
        {
            mailServer.setPort(mailServerBean.port);
        }
        if (StringUtils.isNotBlank(mailServerBean.protocol))
        {
            mailServer.setMailProtocol(MailProtocol.getMailProtocol(mailServerBean.protocol));
        }

        MailServerManager mailServerManager = MailFactory.getServerManager();
        mailServerManager.create(mailServer);

        return Response.ok(null).build();
    }

    @GET
    @Path("flush")
    public Response flushMailQueue()
    {
        LOGGER.info("Flushing mail Queue - currentQueueSize = {}", mailQueue.size());
        LOGGER.debug("Mail queue of type: {}, from classloader: {}, ",
                mailQueue.toString(),
                mailQueue.getClass().getClassLoader().toString());

        mailQueue.sendBuffer();
        return Response.ok().build();
    }

    @GET
    @Path ("smtpConfigured")
    public Response smtpMailConfigured()
    {
        boolean configured = true;
        if (MailFactory.isSendingDisabled())
        {
            configured = false;
        }
        MailServerManager mailServerManager = MailFactory.getServerManager();
        if (!mailServerManager.isDefaultSMTPMailServerDefined())
        {
            configured = false;
        }

        return Response.ok(configured).build();
    }
}
