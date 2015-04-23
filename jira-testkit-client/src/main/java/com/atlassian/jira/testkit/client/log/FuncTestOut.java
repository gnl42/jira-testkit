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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * We need to write output to a PrintStream that we know goes to file.  Maven 1, in its infinite wisdom, holds test
 * output including System.out calls in memory.  If all hell breaks loose, the extra generated output (such as exceptions
 * and so on) will be kept in memory and LOST if the process runs out of memory.
 * We need to put it in a file in this case as we go, so it's available for debugging purposes.
 * So instead of using <code>System.out.println();</code>, use <code>FuncTestOut.out.println();</code>.
 *
 * @since v4.0
 */
public class FuncTestOut
{
    /**
     * This {@link java.io.PrintStream} is tee'ed to write to {@link System#out} and to a Maven aware output filec
     * called JiraFuncTests.out.log
     */
    public final static PrintStream out = new PrintStream(new EnvironmentDetectingPrintStream("JiraFuncTests.out.log", System.out));

    /**
     * This {@link java.io.PrintStream} is tee'ed to write to {@link System#err} and to a Maven aware output filec
     * called JiraFuncTests.out.log
     */
    public final static PrintStream err = new PrintStream(new EnvironmentDetectingPrintStream("JiraFuncTests.err.log", System.err));

    /**
     * This will log to a StringBuffer first if need be and replace the new lines with "indent + newline"
     *
     * @param logData the data to log
     */
    public static void log(Object logData)
    {
        FuncTestLoggerImpl.logIndented(logData);
    }

    /**
     * This will log to a StringBuffer first if need be and replace the new lines with "indent + newline"
     *
     * @param indentLevel the indented level required
     * @param logData the data to log
     */
    public static void log(int indentLevel, Object logData)
    {
        FuncTestLoggerImpl.logIndented(indentLevel, logData);
    }

    private static class EnvironmentDetectingPrintStream extends OutputStream
    {
        private final OutputStream fileOutputStream;
        private final PrintStream systemPrintStream;

        private EnvironmentDetectingPrintStream(String fileName, PrintStream systemPrintStream)
        {
            FileOutputStream fileOut;
            String targetFileName = MavenEnvironment.getMavenAwareOutputDir() + File.separator + fileName;
            try
            {
                fileOut = new FileOutputStream(targetFileName, false);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace(System.err);
                fileOut = null;
            }
            this.fileOutputStream = fileOut;
            this.systemPrintStream = systemPrintStream;
        }

        /*
        * SOMETHING TO THINK ABOUT
         *
         * If Maven continues its mmemory hungry System.out memory caching then maybe
         * we will want to stop writing to it, just to teach it a lesson. But we want
         * to while we're in IDEA.
         */

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException
        {
            if (fileOutputStream != null)
            {
                fileOutputStream.write(b, off, len);
            }
            systemPrintStream.write(b, off, len);
        }

        @Override
        public void write(final byte[] b) throws IOException
        {
            if (fileOutputStream != null)
            {
                fileOutputStream.write(b);
            }
            systemPrintStream.write(b);
        }

        @Override
        public void write(final int b) throws IOException
        {
            if (fileOutputStream != null)
            {
                fileOutputStream.write(b);
            }
            systemPrintStream.write(b);
        }

        @Override
        public void flush() throws IOException
        {
            if (fileOutputStream != null)
            {
                fileOutputStream.flush();
            }
            systemPrintStream.flush();
        }

        /**
         * At least have a go at trying to close the file.
         *
         * @throws Throwable according to method signature
         */
        @Override
        protected void finalize() throws Throwable
        {
            super.finalize();
            if (fileOutputStream != null)
            {
                fileOutputStream.close();
            }
        }
    }
}
