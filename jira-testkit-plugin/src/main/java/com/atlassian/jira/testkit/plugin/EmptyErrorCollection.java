/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.util.ErrorCollection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Some services require {@link com.atlassian.jira.bc.ServiceResult}s to be passed to their methods, but when calling
 * services in the context of a func test we don't mind the test falling over if the service call is invalid and fails.
 *
 * This empty {@link ErrorCollection} implementation can be passed to any {@link com.atlassian.jira.bc.ServiceResultImpl}
 * subclass that expects one.
 *
 * @since v5.0
 */
class EmptyErrorCollection implements ErrorCollection
{
    @Override
    public void addError(String field, String message)
    {
    }

    @Override
    public void addError(String field, String message, Reason
    reason)
    {
    }

    @Override
    public void addErrorMessage(String message)
    {
    }

    @Override
    public void addErrorMessage(String message, Reason
    reason)
    {
    }

    @Override
    public Collection<String> getErrorMessages()
    {
        return null;
    }

    @Override
    public void setErrorMessages(Collection<String> errorMessages)
    {
    }

    @Override
    public Collection<String> getFlushedErrorMessages()
    {
        return null;
    }

    @Override
    public Map<String, String> getErrors()
    {
        return null;
    }

    @Override
    public void addErrorCollection(ErrorCollection errors)
    {
    }

    @Override
    public void addErrorMessages(Collection<String> errorMessages)
    {
    }

    @Override
    public void addErrors(Map<String, String> errors)
    {
    }

    @Override
    public boolean hasAnyErrors()
    {
        return false;
    }

    @Override
    public void addReasons(Set< Reason > reasons)
    {
    }

    @Override
    public void addReason(Reason
    reason)
    {
    }

    @Override
    public void setReasons(Set< Reason > reasons)
    {
    }

    @Override
    public Set<Reason> getReasons()
    {
        return null;
    }
}
