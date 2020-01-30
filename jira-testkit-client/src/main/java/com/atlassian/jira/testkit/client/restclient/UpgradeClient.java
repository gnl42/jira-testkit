/*
 * Copyright © 2012 - 2020 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.UniformInterfaceException;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;

/**
 * Client for the upgrade resource.
 *
 * @since v8.1.15
 */
public class UpgradeClient extends RestApiClient<UpgradeClient> {
    public UpgradeClient(JIRAEnvironmentData environmentData) {
        super(environmentData);
    }

    public void runUpgrades(final int seconds) {
        createResource().path("upgrade").post();
        await().atMost(seconds, TimeUnit.SECONDS).until(this::areUpgradesFinished);
    }

    private boolean areUpgradesFinished() {
        final Map<String, Object> result;
        try {
            result = createResource().path("upgrade").get(Map.class);
        } catch (UniformInterfaceException e) {
            e.printStackTrace();
            return false;
        }

        return "SUCCESS".equals(result.get("outcome"));
    }
}
