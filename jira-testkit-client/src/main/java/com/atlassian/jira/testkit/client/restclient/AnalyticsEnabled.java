/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import java.util.List;
import java.util.Map;

import com.atlassian.jira.util.json.JSONObject;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonRawValue;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Analytics configuration bean.
 */
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyticsEnabled
{
    public boolean analyticsEnabled;

    public AnalyticsEnabled()
    {
    }

    public AnalyticsEnabled(boolean analyticsEnabled)
    {
        this.analyticsEnabled = analyticsEnabled;
    }
}
