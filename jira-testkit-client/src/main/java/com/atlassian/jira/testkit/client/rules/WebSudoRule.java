package com.atlassian.jira.testkit.client.rules;

import com.atlassian.jira.testkit.client.Backdoor;
import com.google.common.collect.ImmutableList;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class WebSudoRule implements TestRule
{
	private final ImmutableList<Backdoor> jiras;

    public <T extends Backdoor> WebSudoRule(T... jiras)
    {
		this.jiras = ImmutableList.<Backdoor>copyOf(jiras);
    }


    @Override
    public Statement apply(final Statement base, final Description description)
    {
        return new Statement()
        {
            @Override
            public void evaluate() throws Throwable
            {
                if (shouldEnable(description))
                {
					for (Backdoor jira : jiras) {
                    	jira.websudo().enable();
					}
                }
                else
                {
					for (Backdoor jira : jiras) {
                    	jira.websudo().disable();
					}
                }
                base.evaluate();
            }

        };
    }

    private boolean shouldEnable(Description description)
    {
        return new AnnotatedDescription(description).hasAnnotation(EnableWebSudo.class);
    }
}
