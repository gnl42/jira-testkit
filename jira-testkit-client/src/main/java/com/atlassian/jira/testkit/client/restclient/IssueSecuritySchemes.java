package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.beans.IssueSecuritySchemeBean;

import java.util.List;

public class IssueSecuritySchemes {
    private List<IssueSecuritySchemeBean> issueSecuritySchemes;

    public List<IssueSecuritySchemeBean> getIssueSecuritySchemes()
    {
        return issueSecuritySchemes;
    }
}
