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
import java.net.URI;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * Client for the version resource.
 *
 * @since v4.3
 */
public class VersionClient extends RestApiClient<VersionClient>
{
    /**
     * Constructs a new VersionClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public VersionClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs the version having a given id.
     *
     * @param versionID a String containing a version id
     * @return a Version
     * @throws UniformInterfaceException if anything goes wrong
     */
    public Version get(String versionID) throws UniformInterfaceException
    {
        return versionWithID(versionID).get(Version.class);
    }

    /**
     * GETs the version having a given id, returning a Response object.
     *
     * @param versionID a String containing a version id
     * @return a Response
     */
    public Response getResponse(final String versionID)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return versionWithID(versionID).get(ClientResponse.class);
            }
        });
    }

    public Version create(Version version) throws UniformInterfaceException
    {
        return version().post(Version.class, version);
    }

    public Response createResponse(final Version version)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return version().post(ClientResponse.class, version);
            }
        });
    }

    public Response delete(final String versionId) throws UniformInterfaceException
    {
        return delete(versionId, null, null);
    }

    public Response delete(final String versionId, final URI swapFixVersion, final URI swapAffectedVersion) throws UniformInterfaceException
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                WebResource deleteResource = versionWithID(versionId);
                if (swapFixVersion != null)
                {
                    deleteResource = deleteResource.queryParam("moveFixIssuesTo", swapFixVersion.getPath());
                }
                if (swapAffectedVersion != null)
                {
                    deleteResource = deleteResource.queryParam("moveAffectedIssuesTo", swapAffectedVersion.getPath());
                }
                return deleteResource.delete(ClientResponse.class);
            }
        });
    }

    public Response deleteVersionAndSwap(final String versionId, DeleteVersionWithCustomFieldParameters parameters) throws UniformInterfaceException
    {
        return toResponse((Method) () -> {
            WebResource removeAndSwapResource = versionWithID(versionId).path("removeAndSwap");

            return removeAndSwapResource
                    .entity(parameters, MediaType.APPLICATION_JSON_TYPE)
                    .post(ClientResponse.class);
        });
    }

    public Response merge(final String versionId, final String mergeToVersionId) throws UniformInterfaceException
    {
        return toResponse((Method) () -> {
            WebResource mergeResource = versionWithID(versionId).path("mergeto").path(mergeToVersionId);

            return mergeResource.put(ClientResponse.class);
        });
    }

    public Version move(String versionId, VersionMove versionMove) throws UniformInterfaceException
    {
        return versionMove(versionId).post(Version.class, versionMove);
    }

    public Response moveResponse(final String versionId, final VersionMove versionMove)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return versionMove(versionId).post(ClientResponse.class, versionMove);
            }
        });
    }

    public Version archive(String versionID) {
        return versionWithID(versionID).type(APPLICATION_JSON_TYPE).put(Version.class, new Version().archived(true));
    }

    public Version unarchive(String versionID) {
        return versionWithID(versionID).type(APPLICATION_JSON_TYPE).put(Version.class, new Version().archived(false));
    }

    public VersionIssueCounts getVersionIssueCounts(String versionID) throws UniformInterfaceException
    {
        return versionWithID(versionID).path("relatedIssueCounts").get(VersionIssueCounts.class);
    }

    public Response getVersionIssueCountsResponse(final String versionId)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return versionWithID(versionId).path("relatedIssueCounts").get(ClientResponse.class);
            }
        });
    }

    public VersionUnresolvedIssueCount getVersionUnresolvedIssueCount(String versionID) throws UniformInterfaceException
    {
        return versionWithID(versionID).path("unresolvedIssueCount").get(VersionUnresolvedIssueCount.class);
    }

    public Response getVersionUnresolvedIssueCountResponse(final String versionId)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return versionWithID(versionId).path("unresolvedIssueCount").get(ClientResponse.class);
            }
        });
    }

    public Response putResponse(final String versionId, final Version version)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return versionWithID(versionId).type(APPLICATION_JSON_TYPE).put(ClientResponse.class, version);
            }
        });
    }

    public Response putResponse(final Version version)
    {
        final String[] selfParts = version.self.split("/");
        final String versionId = selfParts[selfParts.length - 1];
        return putResponse(versionId, version);
    }

    /**
     * Returns a WebResponse for the version with the given id.
     *
     * @param versionID a String containing a version id
     * @return a WebResource
     */
    private WebResource versionWithID(String versionID)
    {
        return createResource().path("version").path(versionID);
    }

    /**
     * Returns a WebResponse for the version resource
     *
     * @return a WebResource
     */
    private WebResource.Builder version()
    {
        return createResource().path("version").type(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Returns a WebResponse for the version resource
     *
     * @return a WebResource
     */
    private WebResource.Builder versionMove(String versionID)
    {
        return createResource().path("version").path(versionID).path("move").type(MediaType.APPLICATION_JSON_TYPE);
    }
}
