/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * @since v5.1
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class ChangeLog
{
    public int startAt;
    public int maxResults;
    public int total;
    public List<History> histories;

    public List<HistoryItem> mergeHistoryItems()
    {
        List<HistoryItem> items = Lists.newArrayList();
        for (History history : histories)
        {
            if (history.items != null)
            {
                items.addAll(history.items);
            }
        }
        return items;
    }

    public int getStartAt()
    {
        return startAt;
    }

    public ChangeLog setStartAt(int startAt)
    {
        this.startAt = startAt;
        return this;
    }

    public int getMaxResults()
    {
        return maxResults;
    }

    public ChangeLog setMaxResults(int maxResults)
    {
        this.maxResults = maxResults;
        return this;
    }

    public int getTotal()
    {
        return total;
    }

    public ChangeLog setTotal(int total)
    {
        this.total = total;
        return this;
    }

    public List<History> getHistories()
    {
        return histories;
    }

    public ChangeLog setHistories(List<History> histories)
    {
        this.histories = histories;
        return this;
    }

    public History addHistory()
    {
        History history = new History();
        histories.add(history);
        return history;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @JsonIgnoreProperties (ignoreUnknown = true)
    public static class History
    {
        public long id;
        public String created;
        public Author author;
        public List<HistoryItem> items;

        @Override
        public String toString()
        {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }

        @Override
        public int hashCode()
        {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object obj)
        {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        public long getId()
        {
            return id;
        }

        public History setId(long id)
        {
            this.id = id;
            return this;
        }

        public List<HistoryItem> getItems()
        {
            return items;
        }

        public void setItems(List<HistoryItem> items)
        {
            this.items = items;
        }

        public History setAuthor(Author author) {
            this.author = author;
            return this;
        }

        public Author getAuthor() {
            return author;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public HistoryItem addHistory()
        {
            HistoryItem item = new HistoryItem();
            items.add(item);

            return item;
        }
    }

    @JsonIgnoreProperties (ignoreUnknown = true)
    public static class HistoryItem
    {
        public String field;
        public String fieldtype;
        public String from;
        public String fromString;
        public String to;
        public String toString;

        public String getField()
        {
            return field;
        }

        public HistoryItem setField(String field)
        {
            this.field = field;
            return this;
        }

        public String getFieldtype()
        {
            return fieldtype;
        }

        public HistoryItem setFieldtype(String fieldtype)
        {
            this.fieldtype = fieldtype;
            return this;
        }

        public String getFrom()
        {
            return from;
        }

        public HistoryItem setFrom(String from)
        {
            this.from = from;
            return this;
        }

        public String getFromString()
        {
            return fromString;
        }

        public HistoryItem setFromString(String fromString)
        {
            this.fromString = fromString;
            return this;
        }

        public String getTo()
        {
            return to;
        }

        public HistoryItem setTo(String to)
        {
            this.to = to;
            return this;
        }

        public String getToString()
        {
            return toString;
        }

        public HistoryItem setToString(String toString)
        {
            this.toString = toString;
            return this;
        }

        @Override
        public String toString()
        {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }

        @Override
        public int hashCode()
        {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object obj)
        {
            return EqualsBuilder.reflectionEquals(this, obj);
        }
    }

    @JsonIgnoreProperties (ignoreUnknown = true)
    public static class Author
    {
        public String key;
        public String name;
        public String emailAddress;

        @Override
        public String toString()
        {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }

        @Override
        public int hashCode()
        {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object obj)
        {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }
    }
}
