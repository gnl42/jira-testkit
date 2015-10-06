/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.favourites.FavouritesService;
import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.sharing.SharePermissionUtils;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.query.Query;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.atlassian.jira.testkit.plugin.util.CacheControl.never;

/**
 * Use this backdoor to manipulate SearchRequests as part of setup for tests.
 *
 * This class should only be called by the <code>com.atlassian.jira.testkit.client.SearchRequestControl</code>.
 *
 * @since v5.0
 */
@Path ("filter")
@AnonymousAllowed
@Consumes ( { MediaType.APPLICATION_JSON })
@Produces ( { MediaType.APPLICATION_JSON })
public class SearchRequestBackdoor
{
    private UserUtil userUtil;
    private SearchRequestService searchRequestService;
    private SearchService searchService;
    private final FavouritesService favouritesService;

    public SearchRequestBackdoor(SearchRequestService searchRequestService, UserUtil userUtil,
            SearchService searchService, FavouritesService favouritesService)
    {
        this.userUtil = userUtil;
        this.searchRequestService = searchRequestService;
        this.searchService = searchService;
        this.favouritesService = favouritesService;
    }

    @POST
    public Response createFilter(SearchRequestBean searchBean) throws SearchException
    {
        ErrorCollection errorCollection = new SimpleErrorCollection();
        ApplicationUser searcher = userUtil.getUserByName(searchBean.username);
        JiraServiceContext ctx = new JiraServiceContextImpl(searcher, errorCollection);

        SearchService.ParseResult parseResult = searchService.parseQuery(searcher, searchBean.searchJql);
        if (!parseResult.isValid())
        {
            throw new IllegalArgumentException("This JQL you have give me, it is not so good.");
        }

        Query newQuery = parseResult.getQuery();

        SearchRequest searchRequest = new SearchRequest(newQuery, searchBean.username, searchBean.searchName, searchBean.searchDescription);
        if (StringUtils.isNotBlank(searchBean.jsonShareString))
        {
            // Ideally we would pass in a permission map rather than a JSON string but SharePermissionUtils doesn't
            // allow that yet. Be the change!
            try
            {
                searchRequest.setPermissions(SharePermissionUtils.fromJsonArrayString(searchBean.jsonShareString));
            }
            catch (JSONException e)
            {
                throw new IllegalArgumentException("This JSON share permission string you have give me, it is not so good.");
            }
        }

        SearchRequest newFilter = searchRequestService.createFilter(ctx, searchRequest, false);

        return Response.ok(newFilter.getId()).build();
    }

    @GET
    @Path("my")
    public Response my(@QueryParam("username") String username)
    {
        username = StringUtils.trimToNull(username);
        if (username == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).cacheControl(never())
                    .entity("No user passed.").build();
        }

        ApplicationUser user = userUtil.getUserByName(username);
        if (user == null)
        {
            return Response.status(Response.Status.BAD_REQUEST).cacheControl(never())
                    .entity("User '" + username + "' does not exist.").build();
        }

        return Response.ok().cacheControl(never())
                .entity(asBeans(user, searchRequestService.getOwnedFilters(user))).build();
    }

    private Iterable<SearchRequestBean> asBeans(final ApplicationUser user, Iterable<? extends SearchRequest> requests)
    {
        return Iterables.transform(requests, input -> {
            final SearchRequestBean searchRequestBean = new SearchRequestBean();
            searchRequestBean.searchName = input.getName();
            searchRequestBean.searchJql = input.getQuery().getQueryString();
            searchRequestBean.username = input.getOwnerUserName();
            searchRequestBean.searchDescription = input.getDescription();
            searchRequestBean.favourite = favouritesService.isFavourite(user, input);
            searchRequestBean.favouriteCount = input.getFavouriteCount();
            try
            {
                searchRequestBean.jsonShareString
                        = SharePermissionUtils.toJsonArray(input.getPermissions().getPermissionSet()).toString();
            }
            catch (JSONException e)
            {
                throw new RuntimeException(e);
            }
            return searchRequestBean;
        });
    }
}
