/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.rules;

import org.junit.runner.Description;

import java.lang.annotation.Annotation;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wrapper around {@link org.junit.runner.Description} to resolve its annotations.
 *
 * @since v4.4
 */
public class AnnotatedDescription
{
    private final Description description;

    public AnnotatedDescription(Description description)
    {
        this.description = checkNotNull(description);
    }

    /**
     * Checks whether the underlying description has given <tt>annotation</tt> - itself or on its parent suite
     * (only if this description is a test).
     *
     * @param annotation the annotation to find
     * @param <A> the annotation type
     * @return <code>true</code> if this description is annotated with given annotation (test method or class)
     */
    public <A extends Annotation> boolean hasAnnotation(Class<A> annotation)
    {
        return getAnnotation(annotation) != null;
    }

    /**
     * Gets annotation of given type for this description, or <code>null</code> if this description is not annotated
     * with <tt>annotation</tt>. Looks up both test method and test class (if applicable).
     *
     * @param annotation the annotation to find
     * @param <A> the annotation type
     * @return annotation instance for this description, or <code>null</code> if not found
     */
    public <A extends Annotation> A getAnnotation(Class<A> annotation)
    {
        A fromTest = getAnnotationFromTestMethod(annotation);
        if (fromTest == null)
        {
            fromTest = getAnnotationFromTestClass(annotation);
        }
        return fromTest;
    }

    public <A extends Annotation> A getAnnotationFromTestMethod(Class<A> annotation)
    {
        if (description.isTest())
        {
            return description.getAnnotation(annotation);
        }
        else
        {
            return null;
        }
    }

    public <A extends Annotation> A getAnnotationFromTestClass(Class<A> annotation)
    {
        if (description.isTest())
        {
            return description.getTestClass().getAnnotation(annotation);
        }
        else
        {
            return description.getAnnotation(annotation);
        }
    }

    public boolean isAnnotatedWith(Class<? extends Annotation> annotation)
    {
        return getAnnotation(annotation) != null;
    }

    public boolean isMethodAnnotated(Class<? extends Annotation> annotation)
    {
        return getAnnotationFromTestMethod(annotation) != null;
    }

    public boolean isClassAnnotated(Class<? extends Annotation> annotation)
    {
        return getAnnotationFromTestClass(annotation) != null;
    }

    @Override
    public String toString()
    {
        return description.toString();
    }
}
