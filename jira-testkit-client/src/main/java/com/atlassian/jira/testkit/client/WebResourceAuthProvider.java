package com.atlassian.jira.testkit.client;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Provides authentication capability to WebResource
 *
 * @since v8.0
 */
final class WebResourceAuthProvider {

    private static final Logger LOG = LoggerFactory.getLogger(WebResourceAuthProvider.class);

    /**
     * Authentication mechanisms supported by RestApiClient
     */
    enum AuthMethod {
        NONE(false),
        BASIC(true),
        @Deprecated
        QUERY_STRING(true);

        /**
         * Denotes whether or not this method could be configured by
         * TESTKIT_AUTH_METHOD_PROP_KEY property from JIRAEnvironmentData
         */
        private boolean configurableByProperty;

        AuthMethod(boolean configurableByProperty) {
            this.configurableByProperty = configurableByProperty;
        }

        public boolean isConfigurableByProperty() {
            return configurableByProperty;
        }

        public static AuthMethod nullableValueOf(final String value) {
            if (value == null) {
                return null;
            }
            try {
                return AuthMethod.valueOf(value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    /**
     * Supported client credentials
     */
    enum ClientCredentials {
        LOGIN,
        PASSWORD
    }

    static final AuthMethod DEFAULT_AUTH_METHOD = AuthMethod.QUERY_STRING;

    static final String TESTKIT_AUTH_METHOD_PROP_KEY = "jira.testkit.client.auth.method";

    /** Holds auth method configured with JIRAEnvironmentData or default one (when not present in environment) */
    private final AuthMethod environmentAuthMethod;

    WebResourceAuthProvider(final JIRAEnvironmentData environment) {
        environmentAuthMethod = getConfiguredAuthMethod(environment);
    }

    WebResource withAuthentication(final WebResource instance, final Map<ClientCredentials,String> credentials) {
        Objects.requireNonNull(instance, "required not null WebResource instance");
        Objects.requireNonNull(credentials, "required not null credentials map");
        switch (resolveAuthMethod(credentials)) {
            case QUERY_STRING: {
                return instance.queryParam("os_authType", "basic")
                        .queryParam("os_username", encodeURL(credentials.get(ClientCredentials.LOGIN)))
                        .queryParam("os_password", encodeURL(credentials.get(ClientCredentials.PASSWORD)));
            }
            case BASIC: {
                instance.addFilter(new HTTPBasicAuthFilter(credentials.get(ClientCredentials.LOGIN),
                        credentials.get(ClientCredentials.PASSWORD)));
                return instance;
            }
            default: {
                return instance;
            }
        }
    }

    AuthMethod resolveAuthMethod(final Map<ClientCredentials,String> credentials) {
        return  (credentials.get(ClientCredentials.LOGIN) == null)
                ? AuthMethod.NONE
                :environmentAuthMethod;
    }

    public AuthMethod getEnvironmentAuthMethod() {
        return environmentAuthMethod;
    }

    static String encodeURL(final String queryParam) {
        try {
            return queryParam == null ? null : URLEncoder.encode(queryParam, StandardCharsets.UTF_8.toString());
        } catch(UnsupportedEncodingException e) {
            LOG.error("Query string parameter encoding failed", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads authentication method from environment property, adu returns it when valid.
     *
     * @param environment Jira environment
     * @return auth method configured with environment property or default one
     */
    private static AuthMethod getConfiguredAuthMethod(final JIRAEnvironmentData environment) {
        final Optional<String> propertyValueOpt = Optional.ofNullable(environment)
                .map(e -> e.getProperty(TESTKIT_AUTH_METHOD_PROP_KEY))
                .map(String::toUpperCase).filter(s -> !s.isEmpty());

        final AuthMethod configuredMethod;
        if (propertyValueOpt.isPresent()) {
            final Optional<AuthMethod> authMethodOpt = propertyValueOpt.map(AuthMethod::nullableValueOf)
                                                            .filter(AuthMethod::isConfigurableByProperty);
            if (!authMethodOpt.isPresent()) {
                LOG.debug("WebResource auth method configuration is not valid so running with defaults. Use one of {}.",
                        Arrays.stream(AuthMethod.values()).filter(AuthMethod::isConfigurableByProperty)
                                .map(AuthMethod::name).collect(Collectors.joining(","))
                );
                configuredMethod = DEFAULT_AUTH_METHOD;
            } else {
                configuredMethod = authMethodOpt.get();
            }

        } else {
            LOG.debug("WebResource auth method configuration is not present or is empty so running with defaults.");
            configuredMethod = DEFAULT_AUTH_METHOD;
        }

        LOG.debug("WebResource auth method set to {}", configuredMethod);
        return configuredMethod;
    }
}
