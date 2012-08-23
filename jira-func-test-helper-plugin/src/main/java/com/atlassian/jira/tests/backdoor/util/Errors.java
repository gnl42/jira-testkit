package com.atlassian.jira.tests.backdoor.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A JAXB representation of an {@link com.atlassian.jira.util.ErrorCollection} useful for returning via JSON or XML.
 *
 * @since v4.2
 */
@XmlRootElement
public class Errors
{
    /**
     * Returns a new ErrorCollection containing a list of error messages.
     *
     * @param messages an array of Strings containing error messages
     * @return a new ErrorCollection
     */
    public static Errors of(String... messages)
    {
        return of(Arrays.asList(messages));
    }

    /**
     * Returns a new ErrorCollection containing a list of error messages.
     *
     * @param messages an Iterable of Strings containing error messages
     * @return a new ErrorCollection
     */
    public static Errors of(Collection<String> messages)
    {
        return new Errors().addErrorMessages(messages);
    }

    /**
     * Returns a new ErrorCollection containing all the errors contained in the input error collection.
     *
     * @param errorCollection a com.atlassian.jira.util.ErrorCollection
     * @return a new ErrorCollection
     */
    public static Errors of(com.atlassian.jira.util.ErrorCollection errorCollection)
    {
        return new Errors().addErrorCollection(errorCollection);
    }

    /**
     * Generic error messages
     */
    @XmlElement
    private Collection<String> errorMessages = Lists.newArrayList();

    @XmlElement
    private Map<String, String> errors = Maps.newHashMap();

    /**
     * Http return code, or null.
     */
    private Integer status = null;

    public Errors()
    {}

    /**
     * Adds all the errors and error messages that are in the given error collection to this error collection.
     *
     * @param errorCollection an ErrorCollection
     * @return this
     */
    public Errors addErrorCollection(com.atlassian.jira.util.ErrorCollection errorCollection)
    {
        errorMessages.addAll(checkNotNull(errorCollection).getErrorMessages());
        errors.putAll(errorCollection.getErrors());
        reason(com.atlassian.jira.util.ErrorCollection.Reason.getWorstReason(errorCollection.getReasons()));

        return this;
    }

    /**
     * Adds the given error message to this error collection.
     *
     * @param errorMessage a String containing an error message
     * @return this
     */
    public Errors addErrorMessage(String errorMessage)
    {
        errorMessages.add(checkNotNull(errorMessage));
        return this;
    }

    /**
     * Adds the given error messages to this error collection.
     *
     * @param messages a collection of Strings containing error messages
     * @return this
     */
    public Errors addErrorMessages(Collection<String> messages)
    {
        errorMessages.addAll(checkNotNull(messages));
        return this;
    }

    /**
     * Returns true if this error collection contains errors or error messages.
     *
     * @return true if this error collection contains errors or error messages.
     */
    public boolean hasAnyErrors()
    {
        return !errorMessages.isEmpty() || !errors.isEmpty();
    }

    public Collection<String> getErrorMessages()
    {
        return errorMessages;
    }

    public Map<String, String> getErrors()
    {
        return errors;
    }

    public Errors reason(final com.atlassian.jira.util.ErrorCollection.Reason reason)
    {
        status = reason == null ? Status.BAD_REQUEST.getStatusCode() : reason.getHttpStatusCode();
        return this;
    }

    public Integer getStatus()
    {
        return status;
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object o)
    {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}