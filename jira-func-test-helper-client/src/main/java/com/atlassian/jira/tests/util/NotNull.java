package com.atlassian.jira.tests.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * The annotated field, method result, parameter or local variable must not be null.
 */
@java.lang.annotation.Documented
@java.lang.annotation.Retention(value = CLASS)
@java.lang.annotation.Target(value = { FIELD, METHOD, PARAMETER, LOCAL_VARIABLE })
public @interface NotNull
{}
