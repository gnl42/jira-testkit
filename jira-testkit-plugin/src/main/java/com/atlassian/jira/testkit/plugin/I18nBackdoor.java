/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.plugin;

import com.atlassian.jira.util.I18nHelper;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.Locale;

import static java.util.Objects.nonNull;

/**
 * Use this backdoor to gain access to the translation for an i18n key.
 * @since v5.0
 */
@Path("i18n")
public class I18nBackdoor
{
    private final I18nHelper.BeanFactory i18nHelper;

    public I18nBackdoor(final I18nHelper.BeanFactory i18nHelperFactory)
    {
        this.i18nHelper = i18nHelperFactory;
    }

    @GET
    @AnonymousAllowed
    public Response getText(@QueryParam("key") String key, @QueryParam("locale") String locale, @QueryParam("value1") final String value1)
    {
        final String language = locale.split("_")[0];
        final String country = locale.split("_")[1];
        if (nonNull(value1)) {
            return Response.ok(i18nHelper.getInstance(new Locale(language, country)).getText(key, value1)).build();
        } else {
            return Response.ok(i18nHelper.getInstance(new Locale(language, country)).getText(key)).build();
        }

    }
}
