/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Map;

/**
 * A bean used when updating the role actors through the ProjectRoleResource as we may
 * not have enough information to fully populate a ProjectRoleBean when doing an update,
 * hence only a reduced set of data consisting of a map of actor type to actor parameter
 * is required for this bean.
 *
 * @since v4.4
 */
@JsonSerialize (include = JsonSerialize.Inclusion.NON_NULL)
public class ProjectRoleActorsUpdate
{
    private Long id;
    private Map<String, String[]> categorisedActors;

    public ProjectRoleActorsUpdate(final Long id, final Map<String, String[]> categorisedActors) {
        this.id = id;
        this.categorisedActors = categorisedActors;
    }

    public Long getId()
    {
        return id;
    }

    public Map<String, String[]> getCategorisedActors()
    {
        return categorisedActors;
    }
}
