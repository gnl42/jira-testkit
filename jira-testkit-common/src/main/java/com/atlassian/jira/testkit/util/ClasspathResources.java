package com.atlassian.jira.testkit.util;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.google.common.collect.Iterators.forEnumeration;

/**
 * List resources available from the classpath.
 *
 * Very loosely based on the code from Stack Overflow
 * http://stackoverflow.com/questions/3923129/get-a-list-of-resources-from-classpath-directory
 *
 * @since 5.2-m26
 */
public class ClasspathResources
{

    /**
     * <p/>
     * For given classpath location ('dir'), list all resources whose name matches given <tt>pattern</tt>.
     *
     * <p/>
     * NOTE: Location is only used here to filter out a narrow set of locations to look in, the <tt>pattern</tt>
     * still has to match the entire resource name to be included in the result.
     *
     * <p/>
     * NOTE 2: This will only work with classloaders based on file systems: class directory or JAR. More sophisticated
     * resource locations are not supported.
     *
     *
     * @param location location on the classpath to search in
     * @param pattern the pattern to match
     * @return the resources in the order they are found
     */
    public static Iterable<String> getResources(final String location, Pattern pattern)
    {

        ImmutableList.Builder<String> allResources = ImmutableList.builder();
        try
        {
            for (URL singleLocation : iterate(location))
            {
                if (isFile(singleLocation))
                {
                    File dir = new File(singleLocation.getFile());
                    if (dir.isDirectory())
                    {
                        allResources.addAll(getResourcesFromDirectory(dir, pattern));
                    }
                }
                else if (isJar(singleLocation))
                {
                    allResources.addAll(getResourcesFromJarFileInLocation(getJarFile(singleLocation), location, pattern));
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return allResources.build();
    }

    private static ImmutableList<URL> iterate(final String location) throws IOException
    {
        return ImmutableList.copyOf(forEnumeration(ClasspathResources.class.getClassLoader().getResources(location)));
    }

    private static boolean isFile(final URL singleLocation)
    {
        return "file".equals(singleLocation.getProtocol());
    }

    private static boolean isJar(final URL singleLocation)
    {
        return "jar".equals(singleLocation.getProtocol());
    }

    private static File getJarFile(final URL singleLocation)
    {
        final String jarPath = singleLocation.getFile().split("\\!")[0];
        if (jarPath.startsWith("file:"))
        {
            return new File(jarPath.substring("file:".length()));
        }
        else
        {
            return new File(jarPath);
        }
    }

    private static Collection<String> getResourcesFromJarFileInLocation(File file, String location, Pattern pattern)
    {
        final ArrayList<String> retval = new ArrayList<String>();
        ZipFile zf = null;
        try
        {
            zf = new ZipFile(file);
            final Enumeration e = zf.entries();
            while (e.hasMoreElements())
            {
                final ZipEntry ze = (ZipEntry) e.nextElement();
                if (matchesLocation(ze, location))
                {
                    final String fileName = getFileName(ze);
                    if (pattern.matcher(fileName).matches())
                    {
                        retval.add(fileName);
                    }
                }
            }
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            closeQuietly(zf);
        }
        return retval;
    }

    private static void closeQuietly(final ZipFile zf)
    {
        // ZipFile, y u no Closeable!!! (in JDK6, anyway)
        if (zf != null)
        {
            try
            {
                zf.close();
            }
            catch (Exception ignored) {}
        }
    }

    private static boolean matchesLocation(final ZipEntry ze, final String location)
    {
        return ze.getName().startsWith(location);
    }

    private static String getFileName(final ZipEntry ze)
    {
        return ze.getName();
    }

    private static Collection<String> getResourcesFromDirectory(final File directory, final Pattern pattern)
    {
        final ArrayList<String> retval = new ArrayList<String>();
        final File[] fileList = directory.listFiles();
        for (final File file : fileList)
        {
            if (file.isDirectory())
            {
                retval.addAll(getResourcesFromDirectory(file, pattern));
            }
            else
            {
                try
                {
                    final String fileName = file.getCanonicalPath();
                    final boolean accept = pattern.matcher(fileName).matches();
                    if (accept)
                    {
                        retval.add(fileName);
                    }
                }
                catch (final IOException e)
                {
                    throw new Error(e);
                }
            }
        }
        return retval;
    }
}