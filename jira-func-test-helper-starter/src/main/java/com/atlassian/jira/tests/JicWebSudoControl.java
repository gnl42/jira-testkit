package com.atlassian.jira.tests;

import com.atlassian.jira.functest.framework.backdoor.Backdoor;
import net.sourceforge.jwebunit.WebTester;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 */
public class JicWebSudoControl {

	private static final String WEBSUDO_URL_TEMPLATE = "rest/func-test/1.0/websudo?enabled=%s";
	private static final String WEBSUDO_URL_DISABLE = format(WEBSUDO_URL_TEMPLATE, "false");

	private final WebTester webTester;
	private final Backdoor backdoor;

	public JicWebSudoControl(Backdoor backdoor, final WebTester driver) {
		this.backdoor = backdoor;
		this.webTester = checkNotNull(driver);
	}

	public void disable() {
		final Context cx = Context.enter();
		try {
			final Scriptable scope = cx.initStandardObjects();
			scope.put("backdoor", scope, backdoor);

			final String script = "backdoor.websudo && !backdoor.websudo().disable()";
			final Object obj = cx.evaluateString(scope, script, "TestScript", 1, null);
			if (obj instanceof Undefined || Boolean.FALSE.equals(obj)) {
				webTester.beginAt(WEBSUDO_URL_DISABLE);
			}
		} finally {
			Context.exit();
		}
	}

}
