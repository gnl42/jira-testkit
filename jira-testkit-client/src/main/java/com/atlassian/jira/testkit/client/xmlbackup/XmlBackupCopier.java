package com.atlassian.jira.testkit.client.xmlbackup;

import com.atlassian.jira.testkit.client.RestoreDataResources;
import com.atlassian.jira.util.collect.MapBuilder;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.LITERAL;
import static java.util.regex.Pattern.compile;

/**
 * This class is used to copy JIRA XML backups to the JIRA import directory.
 *
 * @since v4.3
 */
public class XmlBackupCopier
{
    /**
     * Logger for XmlBackupCopier.
     */
    private static final Logger log = LoggerFactory.getLogger(XmlBackupCopier.class);

    /**
     * Common base URL found in XML backups.
     */
    private static final String LOCALHOST_8090 = "\"http://localhost:8080/jira\"";

    /**
     * Common base URL found in XML backups.
     */
    private static final String LOCALHOST_8080 = "\"http://localhost:8090/jira\"";

    /**
     * The base URL for the running JIRA instance.
     */
    private final URL baseUrl;

    /**
     * The substitutions for the base URL that's in the XML file.
     */
    private Map<Pattern, String> baseUrlSubstitutions;

    /**
     * Creates a new XmlBackupCopier for the given base URL.
     *
     * @param baseUrl a String containing the base URL of the running JIRA instance
     */
    public XmlBackupCopier(URL baseUrl)
    {
        this.baseUrl = checkNotNull(baseUrl);
        this.baseUrlSubstitutions = createBaseUrlSubstitution(baseUrl.toString());
    }

    /**
     * Copies the input file to the JIRA import directory, substituting string tokens. This method will also try to
     * replace any occurrence of {@value #LOCALHOST_8080} and {@value #LOCALHOST_8090} with the value of {@link
     * #baseUrl}.
     * <p/>
     * The string tokens that will be replaced are dictated by the contents of the substitutions parameter.
     *
     * @param sourcePath the absolute path of the source file
     * @param destinationPath the absolute path of the destination file (will be created if it doesn't exist)
     * @return true if at least one substitution was made
     */
    public boolean copyXmlBackupTo(String sourcePath, String destinationPath)
    {
        return copyXmlBackupTo(sourcePath, destinationPath, Collections.<Pattern, String>emptyMap());
    }

    /**
     * Copies the input file to the JIRA import directory, substituting string tokens. Apart from the given
     * substitutions, this method will also try to replace any occurrence of {@value #LOCALHOST_8080} and {@value
     * #LOCALHOST_8090} with the value of {@link #baseUrl}.
     * <p/>
     * The string tokens that will be replaced are dictated by the contents of the substitutions parameter.
     *
     * @param sourcePath the absolute path of the source file
     * @param destinationPath the absolute path of the destination file (will be created if it doesn't exist)
     * @param substitutions the substitutions that will be performed
     * @return true if at least one substitution was made
     */
    public boolean copyXmlBackupTo(String sourcePath, String destinationPath, Map<Pattern, String> substitutions)
    {
        final File destinationFile = new File(destinationPath);
        if (!destinationFile.getParentFile().exists() && !destinationFile.getParentFile().mkdirs())
        {
            throw new RuntimeException("Tried to create parent folders of " + destinationPath + " which did not exist, but failed");
        }

        if (sourcePath.endsWith(".zip"))
        {
            try
            {
                log.trace("File '{}' is a ZIP file, copying without performing substiturions", sourcePath);
                FileUtils.copyFile(new File(sourcePath), destinationFile);
                return false;
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

		try {
			return performSubstitutions(new FileReader(sourcePath), destinationPath, substitutions);
		} catch (FileNotFoundException e) {
			log.trace("Error reading from '{}'", sourcePath);
			throw new RuntimeException("Could not read resource " + sourcePath, e);
		}
	}

	/**
	 * Copies the input resource to the JIRA import directory, substituting string tokens. Apart from the given
	 * substitutions, this method will also try to replace any occurrence of {@value #LOCALHOST_8080} and {@value
	 * #LOCALHOST_8090} with the value of {@link #baseUrl}.
	 * <p/>
	 * The string tokens that will be replaced are dictated by the contents of the substitutions parameter.
	 *
	 * @param sourcePath the class path of the source file
	 * @param destinationPath the absolute path of the destination file (will be created if it doesn't exist)
	 * @param substitutions the substitutions that will be performed
	 * @return true if at least one substitution was made
	 */
	public boolean copyXmlBackupFromClassPathTo(String sourcePath, String destinationPath, Map<Pattern, String> substitutions)
	{
		final File destinationFile = new File(destinationPath);
		if (!destinationFile.getParentFile().exists() && !destinationFile.getParentFile().mkdirs())
		{
			throw new RuntimeException("Tried to create parent folders of " + destinationPath + " which did not exist, but failed");
		}

		// this is a compatibility hack that is necessary to avoid overhauling lots of code in JIRA. this class
		// essentially prepends "xml/" to the resource path if the resource if not found under the original path
		InputStream inputStream = RestoreDataResources.getResourceAsStream(sourcePath);

		if (sourcePath.endsWith(".zip"))
		{
			try
			{
				log.trace("File '{}' is a ZIP file, copying without performing substiturions", sourcePath);
				FileUtils.copyInputStreamToFile(inputStream, destinationFile);
				return false;
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}

		try
		{
			try
			{
				return performSubstitutions(new InputStreamReader(inputStream), destinationPath, substitutions);
			}
			catch (NullPointerException e) {
				log.trace("Error reading from '{}'", sourcePath);
				throw new RuntimeException("Could not read resource " + sourcePath, e);
			}
		}
		finally
		{
			IOUtils.closeQuietly(inputStream);
		}
	}


	/**
	 * Copies the input file to the JIRA import directory, substituting string tokens. Apart from the given
	 * substitutions, this method will also try to replace any occurrence of {@value #LOCALHOST_8080} and {@value
	 * #LOCALHOST_8090} with the value of {@link #baseUrl}.
	 * <p/>
	 * The string tokens that will be replaced are dictated by the contents of the substitutions parameter.
	 *
	 * @param sourceFile the absolute path of the source file
	 * @param destinationPath the absolute path of the destination file (will be created if it doesn't exist)
	 * @param substitutions the substitutions that will be performed
	 * @return true if at least one substitution was made
	 */
	public boolean performSubstitutions(Reader sourceFile, String destinationPath, Map<Pattern, String> substitutions) {
		final File destinationFile = new File(destinationPath);
		final String ls = System.getProperty("line.separator");

		Map<Pattern, String> tokensPlusBaseUrlSubs = Maps.newHashMap(substitutions);
		tokensPlusBaseUrlSubs.putAll(baseUrlSubstitutions);

		boolean wasReplaced = false;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try
		{
			reader = new BufferedReader(sourceFile);
			writer = new BufferedWriter(new FileWriter(destinationFile));
			log.trace("Copying resource to '{}'", destinationPath);

			// copy line by line, substituting strings
			int lineNumber = 1;
			for (String line = reader.readLine(); line != null; line = reader.readLine())
			{
				for (Map.Entry<Pattern, String> subst : tokensPlusBaseUrlSubs.entrySet())
				{
					Matcher m = subst.getKey().matcher(line);
					if (m.find())
					{
						wasReplaced = true;
						line = m.replaceAll(subst.getValue());
						log.trace("Line {} replaced '{}' with '{}'", new Object[] { lineNumber, subst.getKey(), subst.getValue() });
					}
				}

				writer.write(line);
				writer.write(ls);
				lineNumber++;
			}

			log.trace("Finished copying to '{}'", destinationPath);
			return wasReplaced;
		}
		catch (IOException e)
		{
			log.trace("Error copying to '{}'", destinationPath);
			throw new RuntimeException("Could not copy resource to the import directory in jira home " + destinationPath, e);
		}
		finally
		{
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(writer);
		}
	}

    /**
     * The substitutions to make in the file in order to set the base URL correctly.
     *
     * @param baseURL a String containing the base URL to set in the file
     * @return a Map of substitutions
     */
    protected Map<Pattern, String> createBaseUrlSubstitution(String baseURL)
    {
        return MapBuilder.<Pattern, String>newBuilder()
                .add(compile(String.format("value=%s", LOCALHOST_8080), LITERAL), quoteReplacement(format("value=\"%s\"", baseURL)))
                .add(compile(String.format("value=%s", LOCALHOST_8090), LITERAL), quoteReplacement(format("value=\"%s\"", baseURL)))
                .toMap();
    }
}
