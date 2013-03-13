/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin.workflows;

import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.testkit.beans.WorkflowSchemeData;
import com.atlassian.jira.workflow.WorkflowScheme;

/**
 * @since v5.2
 */
public interface WorkflowSchemeDataFactory
{
    WorkflowSchemeData toData(WorkflowScheme scheme);

    WorkflowSchemeData toData(Scheme scheme);
}
