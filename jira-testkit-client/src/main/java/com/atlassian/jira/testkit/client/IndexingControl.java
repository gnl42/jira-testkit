/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import javax.annotation.Nonnull;

/**
 * Backdoor control for indexing.
 *
 * @since v5.2
 */
public class IndexingControl extends BackdoorControl<IndexingControl>
{
    private static final int POLL_INTERVAL = 10;

    public IndexingControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    @Nonnull
    public IndexingProgress startInBackground()
    {
        createResource().path("indexing").path("background").post();
        return new IndexingProgress();
    }

    boolean isIndexingInProgress()
    {
        // returns true iff indexing is running
        return createResource().path("indexing").get(Boolean.class);
    }

    public boolean isIndexConsistent()
    {
        return createResource().path("indexing").path("consistent").get(Boolean.class);
    }

    public void deleteIndex()
    {
        createResource().path("indexing").path("deleteIndex").post();
    }

    public void reindexAll()
    {
        createResource().path("indexing").path("reindexAll").post();
    }

    public class IndexingProgress
    {
        /**
         * Waits until the indexing is finished. This method works by polling the server every {@value
         * IndexingControl#POLL_INTERVAL} seconds.
         */
        public void waitForCompletion()
        {
            while (isIndexingInProgress())
            {
                try
                {
                    Thread.sleep(POLL_INTERVAL * 1000);
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
