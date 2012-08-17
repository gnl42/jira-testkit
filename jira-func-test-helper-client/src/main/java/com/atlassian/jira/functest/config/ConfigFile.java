package com.atlassian.jira.functest.config;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Represents a JIRA configuration file.
 */
public abstract class ConfigFile
{
    private final File file;

    protected ConfigFile(File file)
    {
        this.file = file;
    }

    public static ConfigFile create(File file) throws ConfigFileException
    {
        if (!file.exists())
        {
            throw new ConfigFileException(String.format("File '%s' does not exist.", file.getPath()));
        }
        if (file.isDirectory())
        {
            throw new ConfigFileException(String.format("File '%s' is a directory.", file.getPath()));
        }
        if (!file.canRead())
        {
            throw new ConfigFileException(String.format("Cannot read from '%s'.", file.getPath()));
        }
        final String extension = FilenameUtils.getExtension(file.getName());

        if ("xml".equalsIgnoreCase(extension))
        {
            return new XmlConfigFile(file);
        }
        else if ("zip".equalsIgnoreCase(extension))
        {
            return new ZipConfigFile(file);
        }
        else
        {
            throw new ConfigFileException(String.format("File '%s' does not appear to be a JIRA config file.", file.getPath()));
        }
    }

    public Document readConfig() throws ConfigFileException
    {
        try
        {
            Reader reader = new BufferedReader(new InputStreamReader(getInputStream(getFile()), "UTF-8"));
            try
            {
                SAXReader saxReader = new SAXReader();
                saxReader.setMergeAdjacentText(true);
                return saxReader.read(reader);
            }
            finally
            {
                IOUtils.closeQuietly(reader);
            }
        }
        catch (IOException e)
        {
            throw new ConfigFileException("Unable to read configuration for '" + getFile().getPath() + "': " + e.getMessage(), e);
        }
        catch (DocumentException e)
        {
            throw new ConfigFileException("Unable to read configuration for '" + getFile().getPath() + "': " + e.getMessage(), e);
        }
    }

    public void writeFile(Document document) throws ConfigFileException
    {
        try
        {
            final Writer writer = new BufferedWriter(new OutputStreamWriter(getOutputStream(getFile()), "UTF-8"));
            try
            {
                formatForWrite(document).write(writer);
            }
            finally
            {
                IOUtils.closeQuietly(writer);
            }
        }
        catch (IOException e)
        {
            throw new ConfigFileException("Unable to write configuration to '" + getFile().getPath() + "': " + e.getMessage(), e);
        }
    }

    private static Document formatForWrite(Document document)
    {
        @SuppressWarnings ({ "unchecked" }) final List<Node> list = document.content();
        //The XML space around the comments is removed by the XML parser. Lets add it back if it exists.
        if (list.size() > 1)
        {
            final Node node = list.get(0);
            if (node instanceof Comment)
            {
                final Node elementNode = list.get(1);
                if (elementNode instanceof Element)
                {
                    list.add(1, DocumentFactory.getInstance().createText("\n"));
                }
            }
        }
        return document;
    }

    public File getFile()
    {
        return file;
    }

    protected abstract InputStream getInputStream(File file) throws IOException;

    protected abstract OutputStream getOutputStream(File file) throws IOException;


    private static class XmlConfigFile extends ConfigFile
    {
        private XmlConfigFile(File file)
        {
            super(file);
        }

        protected InputStream getInputStream(File file) throws IOException
        {
            return new FileInputStream(file);
        }

        protected OutputStream getOutputStream(File file) throws IOException
        {
            return new FileOutputStream(file);
        }
    }

    private static class ZipConfigFile extends ConfigFile
    {
        private ZipConfigFile(File file)
        {
            super(file);
        }

        protected InputStream getInputStream(File file) throws IOException
        {
            final ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
            try
            {
                checkAndPosition(file, zis);
                return zis;
            }
            catch (IOException e)
            {
                try
                {
                    zis.close();
                }
                catch (IOException ex)
                {
                    //ignored.
                }
                throw e;
            }
        }

        private void checkAndPosition(File file, ZipInputStream zis)
                throws IOException
        {
            final ZipEntry zipEntry = zis.getNextEntry();
            if (zipEntry == null)
            {
                throw new IOException("The zip file is empty.");
            }
            if (zipEntry.isDirectory())
            {
                throw new IOException(String.format("The zip file contains a directory '%s'.", zipEntry.getName()));
            }
            final String expectedName = getEntryName(file);
            if (!zipEntry.getName().equalsIgnoreCase(expectedName) && !zipEntry.getName().equals("entities.xml"))
            {
                throw new IOException(String.format("The zip file contains a file '%s' but we expected '%s'.", zipEntry.getName(), expectedName));
            }
        }

        protected OutputStream getOutputStream(File file) throws IOException
        {
            final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
            zos.setLevel(Deflater.BEST_COMPRESSION);
            try
            {
                zos.putNextEntry(new ZipEntry(getEntryName(file)));
                return zos;
            }
            catch (IOException e)
            {
                try
                {
                    zos.close();
                }
                catch (IOException ex)
                {
                    //ignored.
                }
                throw e;
            }
        }

        private static String getEntryName(File file)
        {
            return FilenameUtils.getBaseName(file.getName()) + ".xml";
        }
    }

    public static class ConfigFileException extends ConfigException
    {
        public ConfigFileException()
        {
            super();
        }

        public ConfigFileException(String message)
        {
            super(message);
        }

        public ConfigFileException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public ConfigFileException(Throwable cause)
        {
            super(cause);
        }
    }
}
