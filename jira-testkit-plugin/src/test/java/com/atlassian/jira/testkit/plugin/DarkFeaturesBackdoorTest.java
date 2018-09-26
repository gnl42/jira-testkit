package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.config.FeatureFlag;
import com.atlassian.jira.config.FeatureManager;
import com.atlassian.jira.plugin.profile.DarkFeatures;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static io.atlassian.fugue.Option.none;
import static io.atlassian.fugue.Option.some;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class DarkFeaturesBackdoorTest {

    private static final String POSTFIX_ENABLED = ".enabled";
    private static final String POSTFIX_DISABLED = ".disabled";
    private static final String FEATURE = "my.feature";

    @Mock
    private FeatureManager featureManager;

    private DarkFeaturesBackdoor darkFeaturesBackdoor;

    @Before
    public void setup() {
        darkFeaturesBackdoor = new MockDarkFeaturesBackdoor(featureManager);
    }

    @Test
    public void testisGloballyEnabledFeatureFlagOnByDefaultAndNotDisabled() {
        final FeatureFlag featureFlag = FeatureFlag.featureFlag(FEATURE).defaultedTo(true);

        doReturn(some(featureFlag)).when(featureManager).getFeatureFlag(FEATURE);
        doReturn(true).when(featureManager).isEnabled(featureFlag);

        assertBooleanStringResponse(darkFeaturesBackdoor.isGloballyEnabled(FEATURE), true);
    }

    @Test
    public void testisGloballyEnabledFeatureFlagOnByDefaultButBeenDisabledGlobally() {
        final FeatureFlag featureFlag = FeatureFlag.featureFlag(FEATURE).defaultedTo(true);

        doReturn(some(featureFlag)).when(featureManager).getFeatureFlag(FEATURE);
        doReturn(false).when(featureManager).isEnabled(featureFlag);

        final DarkFeatures darkFeatures = new DarkFeatures(emptySet(), singleton(FEATURE + POSTFIX_DISABLED), emptySet());
        doReturn(darkFeatures).when(featureManager).getDarkFeatures();

        assertBooleanStringResponse(darkFeaturesBackdoor.isGloballyEnabled(FEATURE), false);
    }

    @Test
    public void testisGloballyEnabledFeatureFlagOnByDefaultButBeenDisabledForUser() {
        final FeatureFlag featureFlag = FeatureFlag.featureFlag(FEATURE).defaultedTo(true);

        doReturn(some(featureFlag)).when(featureManager).getFeatureFlag(FEATURE);
        doReturn(false).when(featureManager).isEnabled(featureFlag);

        final DarkFeatures darkFeatures = new DarkFeatures(emptySet(), emptySet(), singleton(FEATURE + POSTFIX_DISABLED));
        doReturn(darkFeatures).when(featureManager).getDarkFeatures();

        assertBooleanStringResponse(darkFeaturesBackdoor.isGloballyEnabled(FEATURE), true);
    }

    @Test
    public void testisGloballyEnabledFeatureFlagOffByDefaultAndNotEnabled() {
        final FeatureFlag featureFlag = FeatureFlag.featureFlag(FEATURE).defaultedTo(false);

        doReturn(some(featureFlag)).when(featureManager).getFeatureFlag(FEATURE);
        doReturn(false).when(featureManager).isEnabled(featureFlag);

        assertBooleanStringResponse(darkFeaturesBackdoor.isGloballyEnabled(FEATURE), false);
    }

    @Test
    public void testisGloballyEnabledFeatureFlagOffByDefaultButBeenEnabledGlobally() {
        final FeatureFlag featureFlag = FeatureFlag.featureFlag(FEATURE).defaultedTo(false);

        doReturn(some(featureFlag)).when(featureManager).getFeatureFlag(FEATURE);
        doReturn(true).when(featureManager).isEnabled(featureFlag);

        final DarkFeatures darkFeatures = new DarkFeatures(emptySet(), singleton(FEATURE + POSTFIX_ENABLED), emptySet());
        doReturn(darkFeatures).when(featureManager).getDarkFeatures();

        assertBooleanStringResponse(darkFeaturesBackdoor.isGloballyEnabled(FEATURE), true);
    }

    @Test
    public void testisGloballyEnabledFeatureFlagOffByDefaultButBeenEnabledForUser() {
        final FeatureFlag featureFlag = FeatureFlag.featureFlag(FEATURE).defaultedTo(false);

        doReturn(some(featureFlag)).when(featureManager).getFeatureFlag(FEATURE);
        doReturn(true).when(featureManager).isEnabled(featureFlag);

        final DarkFeatures darkFeatures = new DarkFeatures(emptySet(), emptySet(), singleton(FEATURE + POSTFIX_ENABLED));
        doReturn(darkFeatures).when(featureManager).getDarkFeatures();

        assertBooleanStringResponse(darkFeaturesBackdoor.isGloballyEnabled(FEATURE), false);
    }

    @Test
    public void testisGloballyEnabledStringNotInDarkFeatures() {
        doReturn(none()).when(featureManager).getFeatureFlag(FEATURE);
        doReturn(true).when(featureManager).isEnabled(FEATURE);

        final DarkFeatures darkFeatures = new DarkFeatures(emptySet(), emptySet(), emptySet());
        doReturn(darkFeatures).when(featureManager).getDarkFeatures();

        assertBooleanStringResponse(darkFeaturesBackdoor.isGloballyEnabled(FEATURE), false);
    }

    @Test
    public void testisGloballyEnabledStringInGlobalDarkFeatures() {
        doReturn(none()).when(featureManager).getFeatureFlag(FEATURE);
        doReturn(true).when(featureManager).isEnabled(FEATURE);

        final DarkFeatures darkFeatures = new DarkFeatures(emptySet(), singleton(FEATURE), emptySet());
        doReturn(darkFeatures).when(featureManager).getDarkFeatures();

        assertBooleanStringResponse(darkFeaturesBackdoor.isGloballyEnabled(FEATURE), true);
    }

    @Test
    public void testisGloballyEnabledStringInUserDarkFeatures() {
        doReturn(none()).when(featureManager).getFeatureFlag(FEATURE);
        doReturn(true).when(featureManager).isEnabled(FEATURE);

        final DarkFeatures darkFeatures = new DarkFeatures(emptySet(), emptySet(), singleton(FEATURE));
        doReturn(darkFeatures).when(featureManager).getDarkFeatures();

        assertBooleanStringResponse(darkFeaturesBackdoor.isGloballyEnabled(FEATURE), false);
    }

    @Test
    public void testisGloballyEnabledStringIsNotEnabled() {
        doReturn(none()).when(featureManager).getFeatureFlag(FEATURE);
        doReturn(false).when(featureManager).isEnabled(FEATURE);

        assertBooleanStringResponse(darkFeaturesBackdoor.isGloballyEnabled(FEATURE), false);
    }

    private void assertBooleanStringResponse(Response response, boolean expected) {
        assertNotNull(response);

        final Object entity = response.getEntity();
        assertNotNull(entity);
        assertThat(entity, IsInstanceOf.instanceOf(String.class));

        final Boolean aBoolean = Boolean.valueOf(String.valueOf(entity));
        assertNotNull(aBoolean);
        assertThat(aBoolean, is(expected));
    }

    private static class MockDarkFeaturesBackdoor extends DarkFeaturesBackdoor {

        private final FeatureManager featureManager;

        MockDarkFeaturesBackdoor(FeatureManager featureManager) {
            this.featureManager = featureManager;
        }

        @Override
        protected FeatureManager getFeatureManager() {
            return featureManager;
        }

    }

}
