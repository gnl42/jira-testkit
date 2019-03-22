package com.atlassian.jira.testkit.client.restclient;

import com.atlassian.jira.testkit.client.JIRAEnvironmentData;
import com.atlassian.jira.testkit.client.RestApiClient;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Client for the ICalendarResource provided by jira-ical-feed bundled plugin.
 * Most of the methods return Response with null body so can be used for link tests (to check the response status code).
 *
 * @since v8.1.4
 */
public class ICalendarResourceClient extends RestApiClient<ICalendarResourceClient> {
    public ICalendarResourceClient(JIRAEnvironmentData environmentData) {
        super(environmentData);
    }

    /**
     * @return Response with QueryOptions as a body
     */
    public Response<QueryOptions> getQueryOptions() {
        return toResponse(() -> createResource().path("config").path("query").path("options").get(ClientResponse.class), QueryOptions.class);
    }

    /**
     * @return Response with null body
     */
    public Response validateJql(String jql) {
        MultivaluedMap<String, String> body = new MultivaluedMapImpl();
        body.put("jql", Collections.singletonList(jql));

        return toResponse(() -> createResource().path("util").path("jql").path("validate").type("application/x-www-form-urlencoded").post(ClientResponse.class, body));
    }

    /**
     * @return Response with Set of available dates fields as a body
     */
    public Response<? extends Set> getConfigFields(String jql) {
        return toResponse(() -> createResource().path("config").path("fields").queryParam("jql", jql).get(ClientResponse.class), HashSet.class);
    }

    /**
     * @return Response with null body
     */
    public Response searchWithJql(String jql, String... dateFieldNames) {
        WebResource part = createResource().path("search").path("jql").path("events.ics").queryParam("jql", jql);
        final WebResource resource = applyDateFieldNameQueryParams(part, dateFieldNames);
        return toResponse(() -> resource.get(ClientResponse.class));
    }

    /**
     * @return Response with null body
     */
    public Response searchWithFilter(String filterId, String... dateFieldNames) {
        WebResource part = createResource().path("search").path("filter").path("events.ics").queryParam("searchFilterId", filterId);
        final WebResource resource = applyDateFieldNameQueryParams(part, dateFieldNames);
        return toResponse(() -> resource.get(ClientResponse.class));
    }

    @Override
    protected WebResource createResource() {
        return resourceRoot(getEnvironmentData().getBaseUrl().toExternalForm()).path("rest").path("ical").path("1.0").path("ical");
    }

    private WebResource applyDateFieldNameQueryParams(WebResource part, String[] dateFieldNames) {
        if (dateFieldNames != null) {
            for (String name : dateFieldNames) {
                part = part.queryParam("dateFieldName", name);
            }
        }
        return part;
    }


    /**
     * Mapping classes copied from jira-ical-feed plugin
     */
    @XmlRootElement
    public static class QueryOptions
    {
        @XmlElement
        public List<SimpleProject> projects;

        @XmlElement
        public List<SearchFilter> searchFilters;

        @XmlElement
        public String visibleFieldNames;

        @XmlElement
        public String visibleFunctionNamesJson;

        @XmlElement
        public String jqlReservedWordsJson;

        @XmlElement
        public boolean dateRangeSupported = true;
    }

    @XmlRootElement
    public static class SimpleProject implements Comparable<SimpleProject>
    {
        @XmlElement
        public String key;

        @XmlElement
        public String name;

        @Override
        public int compareTo(SimpleProject simpleProject)
        {
            int result = StringUtils.defaultString(name).compareTo(StringUtils.defaultString(simpleProject.name));
            return 0 == result
                    ? key.compareTo(simpleProject.key)
                    : result;
        }
    }

    @XmlRootElement
    public static class SearchFilter implements Comparable<SearchFilter>
    {
        @XmlElement
        public long id;

        @XmlElement
        public String name;

        @XmlElement
        public String description;

        @Override
        public int compareTo(SearchFilter searchFilter)
        {
            int result = StringUtils.defaultString(name).compareTo(StringUtils.defaultString(searchFilter.name));
            return 0 == result
                    ? (int) (id - searchFilter.id)
                    : result;
        }
    }

}
