/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO: Document this class / interface here
 *
 * @since v5.2
 */
public class MavenEnvironment
{
    /**
     * This will return a directory that can be used to place file output from func tests.  It tries to detect if Maven
     * is being used and hence output to be placed in the target/test-reports directory otherwise it uses a temp
     * directory. The directory is guaranteed to exist after this method returns.
     *
     * @return a directory that can be used for func test output
     */
    public static String getMavenAwareOutputDir()
    {
        File outputDir;

        // check the to see if the maven directory is there
        File targetDir = new File("target");
        if (targetDir.exists() && targetDir.isDirectory() && targetDir.canWrite())
        {
            // ok we are willing to create the test-reports directory if its not present but not the target directory
            outputDir = new File(targetDir, "test-reports");
        }
        else
        {
            // fall back from the desired directory
            try
            {
                File tmpFile = File.createTempFile("jirafunctests_", "dir");
                if (!tmpFile.delete())
                {
                    throw new RuntimeException(String.format("Could not delete temp file '%s'", tmpFile.getAbsolutePath()));
                }

                outputDir = tmpFile;
            }
            catch (IOException e)
            {
                // man can you believe our luck!  Right worst case is this
                String tmpDirName = System.getProperty("java.io.tmpdir");
                String yymmdd = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                outputDir = new File(tmpDirName + File.separator + "jirafunctests_" + yymmdd);
            }
        }

        // create the output dir if necessary
        if (!(outputDir.exists() || outputDir.mkdir()))
        {
            throw new RuntimeException(String.format("Could not create output directory '%s'", outputDir.getAbsolutePath()));
        }

        return outputDir.getAbsolutePath();
    }
}
