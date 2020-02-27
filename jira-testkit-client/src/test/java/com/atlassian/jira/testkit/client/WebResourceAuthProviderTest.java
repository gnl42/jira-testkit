package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.atlassian.jira.testkit.client.WebResourceAuthProvider.DEFAULT_AUTH_METHOD;
import static com.atlassian.jira.testkit.client.WebResourceAuthProvider.TESTKIT_AUTH_METHOD_PROP_KEY;
import static com.atlassian.jira.testkit.client.WebResourceAuthProvider.AuthMethod;
import static com.atlassian.jira.testkit.client.WebResourceAuthProvider.ClientCredentials;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test cases of WebResourceAuthProvider
 *
 * @since v8.0
 */
public class WebResourceAuthProviderTest {

    @Test
    public void defaultBehaviorIsPreservedWithNullJiraEnv() {
        final WebResourceAuthProvider provider = new WebResourceAuthProvider(null);

        final WebResourceAuthProvider.AuthMethod configuredAuth = provider.getEnvironmentAuthMethod();

        assertThat(configuredAuth, equalTo(DEFAULT_AUTH_METHOD));
    }

    @Test
    public void defaultBehaviorIsPreservedWithoutConfigurationProperty() {
        final WebResourceAuthProvider provider = new WebResourceAuthProvider(Mockito.mock(JIRAEnvironmentData.class));

        final WebResourceAuthProvider.AuthMethod configuredAuth = provider.getEnvironmentAuthMethod();

        assertThat(configuredAuth, equalTo(DEFAULT_AUTH_METHOD));
    }

    @Test
    public void defaultBehaviorIsPreservedWithInvalidConfiguration() {
        final JIRAEnvironmentData mockedEnv = Mockito.mock(JIRAEnvironmentData.class);
        Mockito.when(mockedEnv.getProperty(Mockito.eq(TESTKIT_AUTH_METHOD_PROP_KEY)))
                .thenReturn("a kuku");
        final WebResourceAuthProvider provider = new WebResourceAuthProvider(mockedEnv);

        final WebResourceAuthProvider.AuthMethod configuredAuth = provider.getEnvironmentAuthMethod();

        assertThat(configuredAuth, equalTo(DEFAULT_AUTH_METHOD));
    }

    @Test
    public void defaultBehaviorIsPreservedWhenNONEMethodIsConfigured() {
        final JIRAEnvironmentData mockedEnv = Mockito.mock(JIRAEnvironmentData.class);
        Mockito.when(mockedEnv.getProperty(Mockito.eq(TESTKIT_AUTH_METHOD_PROP_KEY)))
                .thenReturn("none");
        final WebResourceAuthProvider provider = new WebResourceAuthProvider(mockedEnv);

        final WebResourceAuthProvider.AuthMethod configuredAuth = provider.getEnvironmentAuthMethod();

        assertThat(configuredAuth, equalTo(DEFAULT_AUTH_METHOD));
    }

    @Test
    public void authTypeConfigurationValueIsCaseInsensitive() {
        final JIRAEnvironmentData mockedEnv = Mockito.mock(JIRAEnvironmentData.class);
        Mockito.when(mockedEnv.getProperty(Mockito.eq(WebResourceAuthProvider.TESTKIT_AUTH_METHOD_PROP_KEY)))
                .thenReturn("bAsiC");
        final WebResourceAuthProvider provider = new WebResourceAuthProvider(mockedEnv);

        final WebResourceAuthProvider.AuthMethod configuredAuth = provider.getEnvironmentAuthMethod();

        assertThat(configuredAuth, equalTo(AuthMethod.BASIC));
    }

    @Test(expected = NullPointerException.class)
    public void providingAuthenticationForNullResourceCausesNPE() {
        final JIRAEnvironmentData mockedEnv = Mockito.mock(JIRAEnvironmentData.class);
        Mockito.when(mockedEnv.getProperty(Mockito.eq(WebResourceAuthProvider.TESTKIT_AUTH_METHOD_PROP_KEY)))
                .thenReturn("bAsiC");
        final WebResourceAuthProvider provider = new WebResourceAuthProvider(mockedEnv);

        provider.withAuthentication(null, Collections.EMPTY_MAP);
    }

    @Test(expected = NullPointerException.class)
    public void providingAuthenticationForNullCredentialsCausesNPE() {
        final JIRAEnvironmentData mockedEnv = Mockito.mock(JIRAEnvironmentData.class);
        Mockito.when(mockedEnv.getProperty(Mockito.eq(WebResourceAuthProvider.TESTKIT_AUTH_METHOD_PROP_KEY)))
                .thenReturn("bAsiC");
        final WebResourceAuthProvider provider = new WebResourceAuthProvider(mockedEnv);

        provider.withAuthentication(Mockito.mock(WebResource.class), null);
    }

    @Test
    public void effectiveAuthMethodResolvedToNONEWithoutLoginCredential() {
        final JIRAEnvironmentData mockedEnv = Mockito.mock(JIRAEnvironmentData.class);
        Mockito.when(mockedEnv.getProperty(Mockito.eq(WebResourceAuthProvider.TESTKIT_AUTH_METHOD_PROP_KEY)))
                .thenReturn("bAsiC");
        final WebResourceAuthProvider provider = new WebResourceAuthProvider(mockedEnv);

        final Map<WebResourceAuthProvider.ClientCredentials, String> credentials = new HashMap<>();

        assertThat(provider.resolveAuthMethod(credentials), equalTo(AuthMethod.NONE));

        credentials.put(ClientCredentials.LOGIN, null);

        assertThat(provider.resolveAuthMethod(credentials), equalTo(AuthMethod.NONE));
    }

    @Test
    public void effectiveAuthMethodIsEnvironmentOriginatedWithLoginCredential() {
        final JIRAEnvironmentData mockedEnv = Mockito.mock(JIRAEnvironmentData.class);
        Mockito.when(mockedEnv.getProperty(Mockito.eq(WebResourceAuthProvider.TESTKIT_AUTH_METHOD_PROP_KEY)))
                .thenReturn("bAsiC");
        final WebResourceAuthProvider provider = new WebResourceAuthProvider(mockedEnv);

        final Map<WebResourceAuthProvider.ClientCredentials, String> credentials = new HashMap<>();
        credentials.put(ClientCredentials.LOGIN, "koza");

        assertThat(provider.resolveAuthMethod(credentials), equalTo(provider.getEnvironmentAuthMethod()));
    }
}
