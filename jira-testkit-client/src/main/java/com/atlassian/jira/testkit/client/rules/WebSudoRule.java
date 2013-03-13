/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

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
