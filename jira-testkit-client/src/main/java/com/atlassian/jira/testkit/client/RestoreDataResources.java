/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client;

import java.io.InputStream;
import java.net.URL;

/**
 * Gets classpath resources for restoring data.
 *
 * @since 5.0
 */
public final class RestoreDataResources
{

    public static URL getResourceUrl(String resourcePath)
    {
        final URL original = getCl().getResource(resourcePath);
        if (original != null)
        {
            return original;
        }
        else
        {
            // compatibility with legacy approach - check for resources under 'xml/'
            final URL withXmlPrefix = getCl().getResource("xml/" + resourcePath);
            if (withXmlPrefix != null)
            {
                return withXmlPrefix;
            }
            else
            {
                throw new IllegalArgumentException("Import resource with path \"" + resourcePath + "\" not found");
            }
        }
    }

    public static InputStream getResourceAsStream(String resourcePath)
    {
        final InputStream original = getCl().getResourceAsStream(resourcePath);
        if (original != null)
        {
            return original;
        }
        else
        {
            // compatibility with legacy approach - check for resources under 'xml/'
            final InputStream withXmlPrefix = getCl().getResourceAsStream("xml/" + resourcePath);
            if (withXmlPrefix != null)
            {
                return withXmlPrefix;
            }
            else
            {
                throw new IllegalArgumentException("Import resource with path \"" + resourcePath + "\" not found");
            }
        }
    }

    private static ClassLoader getCl()
    {
        return RestoreDataResources.class.getClassLoader();
    }
}
