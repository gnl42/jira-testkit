/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.rest.api.customfield.CustomFieldDefinitionJsonBean;
import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Client for the field resource.
 *
 * @since v5.0
 */
public class FieldClient extends RestApiClient<FieldClient>
{
    /**
     * Constructs a new FieldClient for a JIRA instance.
     *
     * @param environmentData The JIRA environment data
     */
    public FieldClient(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    /**
     * GETs all fieldes
     *
     * @return a Field
     * @throws com.sun.jersey.api.client.UniformInterfaceException if there's a problem getting the field
     */
    public List<Field> get() throws UniformInterfaceException
    {
        return field().get(new GenericType<List<Field>>(){});
    }


    public Response createCustomFieldResponse(CustomFieldDefinitionJsonBean customFieldDefinitionJson) {
        return toResponse(new Method() {
            @Override
            public ClientResponse call() {
                return field().type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, customFieldDefinitionJson);
            }
        });
    }

    /**
     * GETs the field with the given id, returning a Response object.
     *
     * @param fieldID a String containing the field id
     * @return a Response
     */
    public Response getResponse(final String fieldID)
    {
        return toResponse(new Method()
        {
            @Override
            public ClientResponse call()
            {
                return fieldWithID(fieldID).get(ClientResponse.class);
            }
        });
    }

    /**
     * Returns a WebResource for the all field.
     *
     * @return a WebResource
     */
    protected WebResource field()
    {
        return createResource().path("field");
    }

    /**
     * Returns a WebResource for the field having the given id.
     *
     * @param fieldID a String containing the field id
     * @return a WebResource
     */
    protected WebResource fieldWithID(String fieldID)
    {
        return createResource().path("field").path(fieldID);
    }
}
