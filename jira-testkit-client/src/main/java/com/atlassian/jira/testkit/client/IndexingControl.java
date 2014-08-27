package com.atlassian.jira.testkit.client;

import com.atlassian.jira.util.Supplier;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.Nonnull;

/**
 * Backdoor control for indexing.
 *
 * @since v5.2
 */
public class IndexingControl extends BackdoorControl<IndexingControl>
{
    public IndexingControl(JIRAEnvironmentData environmentData)
    {
        super(environmentData);
    }

    @Nonnull
    public IndexingProgress startInBackground()
    {
        createResource().path("indexing").path("background").post();
        return new IndexingProgress() {

            @Override
            boolean isIndexing()
            {
                return isIndexingInProgress();
            }

            @Override
            boolean isIndexingStarted()
            {
                return checkIsIndexingStarted();
            }
        };
    }

    boolean isIndexingInProgress()
    {
        // returns true iff indexing is running
        return createResource().path("indexing").get(Boolean.class);
    }

    boolean checkIsIndexingStarted()
    {
        // returns true if indexing has already started
        return createResource().path("indexing").path("started").get(Boolean.class);
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

    @Nonnull
    public IndexingProgress getInBackgroundProgress()
    {
        return new IndexingProgress() {

            @Override
            boolean isIndexing()
            {
                return isIndexingInProgress();
            }

            @Override
            boolean isIndexingStarted()
            {
                return checkIsIndexingStarted();
            }
        };
    }

    public abstract class IndexingProgress
    {
		/** Timeout definition, in minutes. */
		private final long TIMEOUT_MINUTES = 5;
		/** Chunk of incremental updates. */
		private final long MILLIS_PER_CHUNK = 200;
		/** Timeout definition, in chunks. */
		private final long MAX_TIMEOUT_IN_CHUNKS = (TIMEOUT_MINUTES * DateUtils.MILLIS_PER_MINUTE) / MILLIS_PER_CHUNK;

		/** Current interval */
		private long interval = 0;
		/** Overall number of intervals waited. */
		private long overallChunks = 0;

        /**
         * Waits until the indexing is finished. This method works by polling the server in increasing intervals
		 * starting from MILLIS_PER_CHUNK.
         */
        public void waitForCompletion()
        {
            poolUntilTrue(new Supplier<Boolean>()
            {

                @Override
                @Nonnull
                public Boolean get()
                {
                    return !isIndexing();
                }
            });
        }

        public void waitForIndexingStarted()
        {
            poolUntilTrue(new Supplier<Boolean>()
            {
                @Override
                public Boolean get()
                {
                    return isIndexingStarted();
                }
            });
        }

        private void poolUntilTrue(final Supplier<Boolean> condition)
        {
            while (!condition.get())
            {
                try
                {
                    Thread.sleep( (++interval) * MILLIS_PER_CHUNK);
					overallChunks += interval;
					if(overallChunks > MAX_TIMEOUT_IN_CHUNKS)
					{
						throw new RuntimeException("Indexing timed out.");
					}
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        abstract boolean isIndexing();
        abstract boolean isIndexingStarted();
    }
}
