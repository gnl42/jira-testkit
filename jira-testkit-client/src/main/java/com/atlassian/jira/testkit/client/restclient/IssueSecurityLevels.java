package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.beans.IssueSecurityType;

import java.util.List;

public class IssueSecurityLevels {
    List<IssueSecurityType> levels;

    public List<IssueSecurityType> getLevels() {
        return levels;
    }

    public void setLevels(List<IssueSecurityType> levels) {
        this.levels = levels;
    }

    public IssueSecurityLevels levels(List<IssueSecurityType> levels)
    {
        this.setLevels(levels);
        return this;
    }
}
