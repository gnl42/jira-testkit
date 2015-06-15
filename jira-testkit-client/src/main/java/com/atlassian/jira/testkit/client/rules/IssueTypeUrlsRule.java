package com.atlassian.jira.testkit.client.rules;

import com.atlassian.jira.testkit.client.Backdoor;
import com.atlassian.jira.testkit.client.IssueTypeControl;
import com.atlassian.jira.util.Supplier;
import org.junit.rules.ExternalResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IssueTypeUrlsRule extends ExternalResource
{
    private final Pattern pattern = Pattern.compile(".*?avatarId=(\\d+).*?");

    private final Supplier<Backdoor> backdoorSupplier;

    private Map<String, IssueTypeControl.IssueType> nameToIssueTypeMap;

    public IssueTypeUrlsRule(final Supplier<Backdoor> backdoorSupplier)
    {
        this.backdoorSupplier = backdoorSupplier;
    }

    /**
     * For use with older test cases that do not support rules, for example JUnit3.
     */
    @Deprecated
    public void init()
    {
        try
        {
            before();
        }
        catch (Throwable throwable)
        {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    protected void before() throws Throwable
    {
        final List<IssueTypeControl.IssueType> issueTypes = backdoorSupplier.get().issueType().getIssueTypes();
        nameToIssueTypeMap = new HashMap<String, IssueTypeControl.IssueType>();

        for (IssueTypeControl.IssueType issueType : issueTypes)
        {
            nameToIssueTypeMap.put(issueType.getName().toLowerCase(), issueType);
        }
    }

    public String getIssueTypeUrl(final String issueTypeName)
    {
        final IssueTypeControl.IssueType issueType = nameToIssueTypeMap.get(issueTypeName.toLowerCase());

        return issueType != null ? issueType.getIconUrl() : null;
    }

    public Long getAvatarId(final String issueTypeName)
    {
        final IssueTypeControl.IssueType issueType = nameToIssueTypeMap.get(issueTypeName.toLowerCase());
        final Matcher matcher = pattern.matcher(issueType.getIconUrl());

        if (matcher.find())
        {
            try
            {
                return Long.parseLong(matcher.group(1));
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }

        return null;
    }
}
