package com.atlassian.jira.testkit.client;

import com.atlassian.testkit.tests.mock.MockEnvironmentData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link com.atlassian.jira.testkit.client.DataImportControl}'s functionality of selecting
 * a correct blank XML for given build number.
 *
 * @since 5.2-m26
 */
public class TestDataImportControlBlankRestore
{

    @Test
    public void shouldSelectXmlWithMatchingBuildNumber()
    {
        final DataImportControl dataImportControl = createControl();
        assertEquals("xml/testkit-blankprojects-10.xml", dataImportControl.findMatchingResource(10));
    }

    @Test
    public void shouldSelectXmlWithClosestLowerBuildNumberIfMatchingXmlDoesNotExist()
    {
        final DataImportControl dataImportControl = createControl();
        assertEquals("xml/testkit-blankprojects-20.xml", dataImportControl.findMatchingResource(25));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfCannotFindXmlWithLowerBuildNumberThanRequired()
    {
        createControl().findMatchingResource(5); // 10 is the first one supported
    }

    private DataImportControl createControl()
    {
        return new DataImportControl(new MockEnvironmentData());
    }



}
