package com.atlassian.jira.testkit.client.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.*;

public class TestKitLocalEnvironmentDataTest {

    @Rule
    public ExpectedException exception = none();

    @Test
    public void emptyPropertiesThrowsMalformedUrlException() throws MalformedURLException {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Malformed URL http://localhost:2990jira");

        new TestKitLocalEnvironmentData(new Properties(), null);
    }

    @Test
    public void fullPropertiesGiveSensibleResults() throws IOException {
        TestKitLocalEnvironmentData localEnvironmentData = new TestKitLocalEnvironmentData();

        assertThat(localEnvironmentData.getBaseUrl(), is(new URL("https://somehost.somewhere:85673/myJIRA")));
        assertThat(localEnvironmentData.getContext(), is("/myJIRA"));
        assertThat(localEnvironmentData.getTenant(), is("DrWho"));
        assertThat(localEnvironmentData.shouldCreateDummyTenant(), is(true));
        assertThat(localEnvironmentData.getXMLDataLocation(), is(new File("src/test/xml").getCanonicalFile()));
        assertThat(localEnvironmentData.getEdition(), is("10"));
    }
}
