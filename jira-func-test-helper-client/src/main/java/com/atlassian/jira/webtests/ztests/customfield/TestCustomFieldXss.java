package com.atlassian.jira.webtests.ztests.customfield;

import com.atlassian.jira.functest.framework.FuncTestCase;
import com.atlassian.jira.functest.framework.locator.CssLocator;
import com.atlassian.jira.functest.framework.suite.WebTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.atlassian.jira.functest.framework.suite.Category.CUSTOM_FIELDS;
import static com.atlassian.jira.functest.framework.suite.Category.FIELDS;
import static com.atlassian.jira.functest.framework.suite.Category.FUNC_TEST;
import static com.atlassian.jira.functest.framework.suite.Category.SECURITY;


@WebTest ( { FUNC_TEST, CUSTOM_FIELDS, FIELDS, SECURITY })
public class TestCustomFieldXss extends FuncTestCase
{

    private static final String ON_DEMAND_FEATURE = "com.atlassian.jira.config.CoreFeatures.ON_DEMAND";

    private static final String RAW_DESC_TEMPLATE = "description *wiki* markup <div>%s</div>";
    private static final String HTML_DESC_TEMPLATE = "description *wiki* markup <div>%s</div>";
    private static final String WIKI_DESC_TEMPLATE = "<p>description <b>wiki</b> markup &lt;div&gt;%s&lt;/div&gt;</p>";

    private static final Iterable<String> CUSTOM_FIELD_TYPES = ImmutableList.of(
        builInCustomFieldKey(CUSTOM_FIELD_TYPE_SELECT),
        builInCustomFieldKey(CUSTOM_FIELD_TYPE_RADIO),
        builInCustomFieldKey(CUSTOM_FIELD_TYPE_CHECKBOX),
        builInCustomFieldKey(CUSTOM_FIELD_TYPE_TEXTFIELD),
        builInCustomFieldKey(CUSTOM_FIELD_TYPE_MULTISELECT),
        builInCustomFieldKey(CUSTOM_FIELD_TYPE_USERPICKER),
        builInCustomFieldKey(CUSTOM_FIELD_TYPE_MULTIUSERPICKER),
        builInCustomFieldKey(CUSTOM_FIELD_TYPE_DATEPICKER),
        builInCustomFieldKey(CUSTOM_FIELD_TYPE_DATETIME),
        builInCustomFieldKey(CUSTOM_FIELD_TYPE_GROUPPICKER),
        builInCustomFieldKey(CUSTOM_FIELD_TYPE_MULTIGROUPPICKER)
    );

    private static final Map<String, String> SEARCHERS = ImmutableMap.<String, String>builder()
        .put(builInCustomFieldKey(CUSTOM_FIELD_TEXT_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_TEXTFIELD))
        .put(builInCustomFieldKey(CUSTOM_FIELD_EXACT_TEXT_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_URL))
        .put(builInCustomFieldKey(CUSTOM_FIELD_DATE_RANGE), builInCustomFieldKey(CUSTOM_FIELD_TYPE_DATEPICKER))
        .put(builInCustomFieldKey(CUSTOM_FIELD_EXACT_NUMBER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_FLOAT))
        .put(builInCustomFieldKey(CUSTOM_FIELD_NUMBER_RANGE), builInCustomFieldKey(CUSTOM_FIELD_TYPE_FLOAT))
        .put(builInCustomFieldKey(CUSTOM_FIELD_PROJECT_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_PROJECT))
        .put(builInCustomFieldKey(CUSTOM_FIELD_USER_PICKER_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_USERPICKER))
        .put(builInCustomFieldKey(CUSTOM_FIELD_GROUP_PICKER_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_MULTIGROUPPICKER))
        .put(builInCustomFieldKey(CUSTOM_FIELD_SELECT_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_SELECT))
        .put(builInCustomFieldKey(CUSTOM_FIELD_RADIO_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_RADIO))
        .put(builInCustomFieldKey(CUSTOM_FIELD_CASCADING_SELECT_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_CASCADINGSELECT))
        .put(builInCustomFieldKey(CUSTOM_FIELD_MULTI_SELECT_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_MULTISELECT))
        .put(builInCustomFieldKey(CUSTOM_FIELD_CHECKBOX_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_MULTICHECKBOXES))
        .put(builInCustomFieldKey(CUSTOM_FIELD_LABEL_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_LABELS))
        .build();

    private static final ImmutableMap<String, String> SEARCHERS_NON_RENDERING = ImmutableMap.of(
            builInCustomFieldKey(CUSTOM_FIELD_VERSION_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_VERSION),
            builInCustomFieldKey(CUSTOM_FIELD_USER_PICKER_GROUP_SEARCHER), builInCustomFieldKey(CUSTOM_FIELD_TYPE_USERPICKER)
    );

    @Override
    protected void setUpTest()
    {
        backdoor.restoreBlankInstance();
    }

    @Override
    protected void tearDownTest()
    {
        backdoor.darkFeatures().disableForSite(ON_DEMAND_FEATURE);
    }

    public void testCustomFieldDescriptionsCanBeRenderedAsRawHtmlOrWikiMarkup() throws Exception
    {
        for (String type : CUSTOM_FIELD_TYPES)
        {
            testSingleCustomFieldDescriptionOnCustomFieldsScreen(type);
        }
    }

    public void testCustomFieldDescriptionsCanBeRenderedAsRawHtmlOrWikiMarkUpInIssueNavigator() throws Exception
    {
        for(Map.Entry<String, String> entry : SEARCHERS.entrySet())
        {
            testSingleCustomFieldDescriptionOnIssueNavigatorScreen(entry.getValue(), entry.getKey());
        }
    }

    public void testCustomFieldDescriptionsInIssueNavigatorNoXss() throws Exception
    {
        for(Map.Entry<String, String> entry : SEARCHERS_NON_RENDERING.entrySet())
        {
            testSingleCustomFieldOnIssueNavigatorScreen(entry.getValue(), entry.getKey());
        }
    }

    private void testSingleCustomFieldOnIssueNavigatorScreen(String customFieldType, String customFieldSearcher)
    {
        final String fieldId = backdoor.customFields().createCustomField(customFieldType + "-name", fieldDescription(customFieldType), customFieldType, customFieldSearcher);

        // now test that the description is does not contain raw HTML
        backdoor.darkFeatures().enableForSite(ON_DEMAND_FEATURE);
        navigation.issueNavigator().displayAllIssues();
        assertFalse("ON_DEMAND is enabled so the description should be rendered as wiki markup for " + customFieldSearcher, getPageSource().contains("<div>" + customFieldType + "</div>"));

        backdoor.customFields().deleteCustomField(fieldId);
    }

    private void testSingleCustomFieldDescriptionOnIssueNavigatorScreen(String customFieldType, String customFieldSearcher)
    {
        final String fieldId = backdoor.customFields().createCustomField(customFieldType + "-name", fieldDescription(customFieldType), customFieldType, customFieldSearcher);

        // test that the description is rendered as raw HTML
        backdoor.darkFeatures().disableForSite(ON_DEMAND_FEATURE);
        navigation.issueNavigator().displayAllIssues();
        assertTrue("ON_DEMAND is disabled so the description should be rendered as raw HTML for " + customFieldSearcher, getPageSource().contains(fieldDescriptionHtml(customFieldType)));

        // now test that the description is rendered as Wiki markup
        backdoor.darkFeatures().enableForSite(ON_DEMAND_FEATURE);
        navigation.issueNavigator().displayAllIssues();
        assertTrue("ON_DEMAND is enabled so the description should be rendered as wiki markup for " + customFieldSearcher, getPageSource().contains(fieldDescriptionWikiFormat(customFieldType)));
        assertFalse("ON_DEMAND is enabled so the description should be rendered as wiki markup for " + customFieldSearcher, getPageSource().contains("<div>" + customFieldType + "</div>"));

        backdoor.customFields().deleteCustomField(fieldId);
    }

    private void testSingleCustomFieldDescriptionOnCustomFieldsScreen(String customFieldType)
    {
        final String fieldId = backdoor.customFields().createCustomField(customFieldType + "-name", fieldDescription(customFieldType), customFieldType, null);

        // test that the description is rendered as raw HTML
        backdoor.darkFeatures().disableForSite(ON_DEMAND_FEATURE);
        goToCustomFields();
        assertTrue("ON_DEMAND is disabled so the description should be rendered as raw HTML for " + customFieldType, getPageSource().contains(fieldDescriptionHtml(customFieldType)));

        // now test that the description is rendered as Wiki markup
        backdoor.darkFeatures().enableForSite(ON_DEMAND_FEATURE);
        goToCustomFields();
        assertTrue("ON_DEMAND is enabled so the description should be rendered as wiki markup for " + customFieldType, getPageSource().contains(fieldDescriptionWikiFormat(customFieldType)));
        assertFalse("ON_DEMAND is enabled so the description should be rendered as wiki markup for " + customFieldType, getPageSource().contains("<div>" + customFieldType + "</div>"));

        backdoor.customFields().deleteCustomField(fieldId);
    }

    private static String fieldDescription(String fieldId)
    {
        return String.format(RAW_DESC_TEMPLATE, fieldId);
    }

    private static String fieldDescriptionHtml(String fieldId)
    {
        return String.format(HTML_DESC_TEMPLATE, fieldId);
    }

    private static String fieldDescriptionWikiFormat(String fieldId)
    {
        return String.format(WIKI_DESC_TEMPLATE, fieldId);
    }

    private void goToCustomFields()
    {
        navigation.gotoAdminSection("view_custom_fields");
    }

    private String getPageSource()
    {
        return tester.getDialog().getResponseText();
    }

    private CssLocator locatorForDescription(String fieldId)
    {
        return locator.css(String.format("#custom-fields-%s-name div.description", fieldId));
    }
}
