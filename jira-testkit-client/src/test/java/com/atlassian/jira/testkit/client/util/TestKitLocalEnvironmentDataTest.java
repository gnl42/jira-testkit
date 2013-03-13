/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class TestKitLocalEnvironmentDataTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void emptyPropertiesThrowsMalformedUrlException() throws IOException {
        TestKitLocalEnvironmentData localEnvironmentData = new TestKitLocalEnvironmentData(new Properties(), null);

        assertThat(localEnvironmentData.getBaseUrl(), is(new URL("http://localhost:2990/jira")));
        assertThat(localEnvironmentData.getContext(), is("/jira"));
        assertThat(localEnvironmentData.getTenant(), is(nullValue()));
        assertThat(localEnvironmentData.shouldCreateDummyTenant(), is(false));
        assertThat(localEnvironmentData.getXMLDataLocation(), is(new File("src/test/xml").getCanonicalFile()));
        assertThat(localEnvironmentData.getEdition(), is("standard"));
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

    @Test
    public void xmlDataLocationOverrideIsAccepted() throws IOException {
        TestKitLocalEnvironmentData localEnvironmentData = new TestKitLocalEnvironmentData("src/test/resources");
        assertThat(localEnvironmentData.getXMLDataLocation(), is(new File("src/test/resources").getCanonicalFile()));
    }

    @Test
    public void invalidXmlDataLocationThrowsException() throws IOException {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Cannot find xml data location: 'bad'");
        new TestKitLocalEnvironmentData("bad");
    }
}
