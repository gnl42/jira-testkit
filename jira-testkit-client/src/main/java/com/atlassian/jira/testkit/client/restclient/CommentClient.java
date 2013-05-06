/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;

/**
 * Client for the Comment resource.
 *
 * @since v4.3
 */
public class CommentClient extends RestApiClient<CommentClient>
{
    /**
     * Constructs a new CommentClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public CommentClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the comment with the given ID.
     *
     * @param issueKey the comment belongs to
     * @param commentID a String containing a comment id
     * @return a Comment
     * @throws UniformInterfaceException if there is a problem getting the comment
     */
    public Response<Comment> get(final String issueKey, final String commentID) throws UniformInterfaceException
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return commentWithID(issueKey, commentID).get(ClientResponse.class);
            }
        }, Comment.class);
    }

    public Response<Comment> get(final String issueKey, final String commentID, final String expand) throws UniformInterfaceException
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return commentWithID(issueKey, commentID).queryParam("expand", expand)
                                                         .get(ClientResponse.class);
            }
        }, Comment.class);
    }

    public Response<CommentsWithPaginationBean> getComments(final String issueKey)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issue").path(issueKey).path("comment").get(ClientResponse.class);
            }
        }, CommentsWithPaginationBean.class);
    }

    public Response<CommentsWithPaginationBean> getComments(final String issueKey, final String expand)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issue").path(issueKey).path("comment")
                                       .queryParam("expand", expand)
                                       .get(ClientResponse.class);
            }
        }, CommentsWithPaginationBean.class);
    }

    public Response<Comment> put(final String issueKey, final Comment comment)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issue").path(issueKey).path("comment").path(comment.id).type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, comment);
            }
        }, Comment.class);
    }

    public Response<Comment> put(final String issueKey, final Comment comment, final String expand)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issue").path(issueKey).path("comment").path(comment.id)
                                       .queryParam("expand", expand)
                                       .type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, comment);
            }
        }, Comment.class);
    }

    public Response<Comment> post(final String issueKey, final Comment comment)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issue").path(issueKey).path("comment").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, comment);
            }
        }, Comment.class);
    }

    public Response<Comment> post(final String issueKey, final Comment comment, final String expand)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issue").path(issueKey).path("comment")
                                       .queryParam("expand", expand)
                                       .type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, comment);
            }
        }, Comment.class);
    }

    public Response delete(final String issueKey, final Comment comment)
    {
        return delete(issueKey, comment.id);
    }

    public Response delete(final String issueKey, final String commentId)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return createResource().path("issue").path(issueKey).path("comment").path(commentId).type(MediaType.APPLICATION_JSON_TYPE).delete(ClientResponse.class);
            }
        });
    }

    /**
     * GETs the comment with the given ID, and returns a Response.
     *
     * @param issueKey the comment belongs to
     * @param commentID a String containing a comment ID
     * @return a Response
     */
    public Response getResponse(final String issueKey, final String commentID)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return commentWithID(issueKey, commentID).get(ClientResponse.class);
            }
        });
    }

    /**
     * Returns a WebResource for the comment with the given ID.
     *
     * @param issueKey the comment belongs to
     * @param commentID a String containing a comment ID
     * @return a WebResource
     */
    protected WebResource commentWithID(String issueKey, String commentID)
    {
        return createResource().path("issue").path(issueKey).path("comment").path(commentID);
    }
}
