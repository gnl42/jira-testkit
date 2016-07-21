package com.atlassian.jira.testkit.client.rules;

import com.atlassian.jira.testkit.client.Backdoor;
import com.atlassian.jira.testkit.client.model.FeatureFlag;
import com.google.common.collect.ImmutableList;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

import static com.atlassian.jira.util.dbc.Assertions.notBlank;
import static com.atlassian.jira.util.dbc.Assertions.notNull;

/**
 * @since v7.2.15
 */
@ParametersAreNonnullByDefault
public class FeatureFlagUserRule implements TestRule {
    private final Backdoor backdoor;
    private final String username;
    private final Collection<FeatureFlag> featureFlagsToEnable;
    private final Collection<FeatureFlag> featureFlagsToDisable;

    public FeatureFlagUserRule(Backdoor backdoor, String username, Collection<FeatureFlag> featureFlagsToEnable) {
        this(backdoor, username, featureFlagsToEnable, ImmutableList.of());
    }

    public FeatureFlagUserRule(Backdoor backdoor, String username, Collection<FeatureFlag> featureFlagsToEnable, Collection<FeatureFlag> featureFlagsToDisable) {
        this.backdoor = notNull(backdoor);
        this.username = notBlank("username", username);
        this.featureFlagsToEnable = ImmutableList.copyOf(featureFlagsToEnable);
        this.featureFlagsToDisable = ImmutableList.copyOf(featureFlagsToDisable);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                featureFlagsToEnable.forEach(flag -> backdoor.darkFeatures().enableForUser(username, flag));
                featureFlagsToEnable.forEach(flag -> backdoor.darkFeatures().disableForUser(username, flag));
                try {
                    base.evaluate();
                } finally {
                    featureFlagsToEnable.forEach(flag -> backdoor.darkFeatures().resetForUser(username, flag));
                    featureFlagsToDisable.forEach(flag -> backdoor.darkFeatures().resetForUser(username, flag));
                }
            }
        };
    }
}
