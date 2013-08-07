package com.atlassian.jira.testkit.plugin;

import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityLinkService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.testkit.beans.EntityList;
import com.atlassian.jira.testkit.beans.EntityRefBean;
import com.atlassian.jira.testkit.beans.EntityTypeBean;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


/**
 * Backdoor for accessing entity links associated with projects
 *
 * @since v6.1
 */
@Path ("applinks/entitylinks")
@AnonymousAllowed
@Produces (MediaType.APPLICATION_JSON)
public class EntityLinksBackdoor
{

    private final ProjectManager projectManager;

    public EntityLinksBackdoor(final ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    @GET
    public EntityList getEntityLink(@QueryParam("projectId") Long projectId)
    {
        final EntityLinkService entityLinkService = ComponentAccessor.getOSGiComponentInstanceOfType(EntityLinkService.class);
        ArrayList<EntityRefBean> entities = Lists.newArrayList(Iterables.transform(entityLinkService.getEntityLinks(projectManager.getProjectObj(projectId)), new Function<EntityLink, EntityRefBean>()
        {
            public EntityRefBean apply(@Nullable EntityLink from)
            {
                return new EntityRefBean(
                        from.getKey(),
                        new EntityTypeBean(from.getType().getApplicationType().getName(), from.getType().getI18nKey(), from.getType().getPluralizedI18nKey(), from.getType().getIconUrl().toString()),
                        from.getName()
                );
            }
        }));
        return new EntityList(entities);
    }

}
