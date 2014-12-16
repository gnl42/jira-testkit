package com.atlassian.jira.testkit.client.restclient.matcher;

import com.atlassian.jira.testkit.client.Backdoor;
import com.atlassian.jira.testkit.client.restclient.Project;
import com.atlassian.jira.testkit.client.restclient.ProjectClient;
import com.atlassian.jira.testkit.client.restclient.ProjectUpdateField;
import com.google.common.collect.Maps;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Map;

public class CorrectlyUpdatedProjectMatcher extends TypeSafeMatcher<Project>
{

    private String description = "";
    private final Map<ProjectUpdateField, String> expectedFields;
    private final Backdoor backdoor;

    public static Matcher<Project> create(ProjectClient.UpdateBean updateBean, Project originalProject, final Backdoor backdoor)
    {
        final Map<ProjectUpdateField, String> expectedFields = Maps.newHashMap();
        for (ProjectUpdateField field : ProjectUpdateField.values())
        {
            expectedFields.put(field, updateBean.getJson().get(field.jsonFieldName()) != null ? updateBean.getJson().get(field.jsonFieldName()) : field.getFrom(originalProject, backdoor));
        }

        return new CorrectlyUpdatedProjectMatcher(expectedFields, backdoor);
    }

    private CorrectlyUpdatedProjectMatcher(Map<ProjectUpdateField, String> expectedFields, Backdoor backdoor)
    {
        this.expectedFields = expectedFields;
        this.backdoor = backdoor;
    }

    @Override
    protected boolean matchesSafely(Project project)
    {
        for (Map.Entry<ProjectUpdateField, String> entry : expectedFields.entrySet())
        {
            String projectValue = entry.getKey().getFrom(project, backdoor);
            if (!nullSafeEquals(projectValue, entry.getValue()))
            {
                description = "Field '" + entry.getKey().jsonFieldName() + "' of project should be '" + entry.getValue() + "' but was '" + projectValue + "'";
                return false;
            }
        }
        return true;
    }

    /** Added to make this artifact java6-compatible. */
    private static final boolean nullSafeEquals(Object a, Object b)
    {
        return (a == b) || (a != null && a.equals(b));
    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText(this.description);
    }
}
