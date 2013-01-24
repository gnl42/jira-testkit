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
