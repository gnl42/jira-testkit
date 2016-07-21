package com.atlassian.jira.testkit.client.rules;

import com.atlassian.jira.testkit.client.Backdoor;
import com.atlassian.jira.testkit.client.model.FeatureFlag;
import com.google.common.collect.ImmutableList;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

import static com.atlassian.jira.util.dbc.Assertions.notNull;

/**
 * @since v7.2.15
 */
@ParametersAreNonnullByDefault
public class FeatureFlagRule implements TestRule {
    private final Backdoor backdoor;
    private final Collection<FeatureFlag> featureFlagsToEnable;
    private final Collection<FeatureFlag> featureFlagsToDisable;

    public FeatureFlagRule(Backdoor backdoor, Collection<FeatureFlag> featureFlagsToEnable) {
        this(backdoor, featureFlagsToEnable, ImmutableList.of());
    }

    public FeatureFlagRule(Backdoor backdoor, Collection<FeatureFlag> featureFlagsToEnable, Collection<FeatureFlag> featureFlagsToDisable) {
        this.backdoor = notNull(backdoor);
        this.featureFlagsToEnable = ImmutableList.copyOf(featureFlagsToEnable);
        this.featureFlagsToDisable = ImmutableList.copyOf(featureFlagsToDisable);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                featureFlagsToEnable.forEach(backdoor.darkFeatures()::enableForSite);
                featureFlagsToDisable.forEach(backdoor.darkFeatures()::disableForSite);
                try {
                    base.evaluate();
                } finally {
                    featureFlagsToEnable.forEach(backdoor.darkFeatures()::resetForSite);
                    featureFlagsToDisable.forEach(backdoor.darkFeatures()::resetForSite);
                }
            }
        };
    }
}
