package com.atlassian.jira.testkit.plugin;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginState;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PluginsBackdoorTest {

    private static final String PLUGIN_KEY = "thePluginKey";

    @Mock
    private Plugin plugin;

    @Mock
    private PluginAccessor pluginAccessor;

    @Mock
    private PluginController pluginController;

    @Mock
    private PluginSettingsFactory pluginSettingsFactory;

    @InjectMocks
    private PluginsBackdoor pluginsBackdoor;

    @Test
    public void getStatePlugin_shouldReturnOkResponseWhenPluginFound() {
        // Set up
        when(pluginAccessor.getPlugin(PLUGIN_KEY)).thenReturn(plugin);
        final PluginState pluginState = PluginState.values()[0];
        when(plugin.getPluginState()).thenReturn(pluginState);

        // Invoke
        final Response response = pluginsBackdoor.getStatePlugin(PLUGIN_KEY);

        // Check
        assertResponse(response, pluginState.name(), OK, TEXT_PLAIN_TYPE);
    }

    @Test
    public void getStatePlugin_shouldReturnNotFoundResponseWhenPluginNotFound() {
        // Set up
        when(pluginAccessor.getPlugin(PLUGIN_KEY)).thenReturn(null);

        // Invoke
        final Response response = pluginsBackdoor.getStatePlugin(PLUGIN_KEY);

        // Check
        assertResponse(response, "Unknown plugin key 'thePluginKey'", NOT_FOUND, TEXT_PLAIN_TYPE);
    }

    @SuppressWarnings("SameParameterValue")
    private static void assertResponse(final Response actualResponse, final String expectedText,
                                       final Status expectedStatus, final MediaType expectedMediaType) {
        assertThat(actualResponse.getEntity(), is(expectedText));
        assertThat(actualResponse.getStatus(), is(expectedStatus.getStatusCode()));
        assertThat(actualResponse.getMetadata().getFirst("Content-Type"), is(expectedMediaType));
    }
}