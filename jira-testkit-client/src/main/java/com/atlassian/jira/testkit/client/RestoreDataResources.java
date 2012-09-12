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
