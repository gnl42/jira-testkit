package com.atlassian.jira.testkit.client;

import com.atlassian.applinks.api.application.jira.JiraProjectEntityType;
import com.atlassian.jira.testkit.client.restclient.ApplinksPluginRestClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Client-side control for entity links.
 */
public class EntityLinkControl
{
    // Duplicate of com.atlassian.applinks.application.confluence.ConfluenceSpaceEntityTypeImpl#TYPE_ID
    private static final String CONFLUENCE_SPACE_TYPE_ID = "confluence.space";

    private static final String FALSE = Boolean.toString(false);

    private static final String JIRA_PROJECT_ENTITY_TYPE = JiraProjectEntityType.class.getName();

    /**
     * Retuns the JSON payload for creating an entity link to a Confluence space.
     *
     * @param confluenceSpaceKey the Confluence space key
     * @param confluenceSpaceName the Confluence space name
     * @param applicationId the applinks ID of the Confluence instance
     * @return valid JSON
     */
    private static String getEntityJsonForConfluenceSpace(
            final String confluenceSpaceKey, final String confluenceSpaceName, final UUID applicationId)
    {
        checkNotNull(applicationId);
        checkNotNull(confluenceSpaceKey);
        checkNotNull(confluenceSpaceName);
        try
        {
            final JSONObject entityLink = new JSONObject();
            entityLink.put("applicationId", applicationId);
            entityLink.put("typeId", CONFLUENCE_SPACE_TYPE_ID);
            entityLink.put("name", confluenceSpaceName);
            entityLink.put("key", confluenceSpaceKey);
            return entityLink.toString();
        }
        catch (final JSONException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private final ApplinksPluginRestClient applinksBackdoor;

    /**
     * Constructor.
     *
     * @param jiraEnvironment the environment of the JIRA hosting the applinks plugin
     */
    public EntityLinkControl(@Nonnull final JIRAEnvironmentData jiraEnvironment)
    {
        this.applinksBackdoor = new ApplinksPluginRestClient(jiraEnvironment);
    }

    /**
     * Creates a link from a JIRA Project to a Confluence space.
     *
     * @param jiraProjectKey the JIRA project key
     * @param applicationId the ID of the application link that points to the Confluence instance
     * @param confluenceSpaceKey the Confluence space key
     * @param confluenceSpaceName the Confluence space name
     */
    @SuppressWarnings("unused")
    public void linkProjectToConfluenceSpace(
            @Nonnull final String jiraProjectKey,
            @Nonnull final UUID applicationId,
            @Nonnull final String confluenceSpaceKey,
            @Nonnull final String confluenceSpaceName)
    {
        final String confluenceSpaceJson =
                getEntityJsonForConfluenceSpace(confluenceSpaceKey, confluenceSpaceName, applicationId);
        createEntityLink(JIRA_PROJECT_ENTITY_TYPE, jiraProjectKey, confluenceSpaceJson);
    }

    /**
     * Facade for the applinks plugin's "create entity link" REST resource. Invoking this resource directly
     * from the testkit client is simpler and faster than having a REST resource in the testkit plugin that
     * delegates to <code>MutatingEntityLinkService#addEntityLink</code>.
     *
     * This method is not coupled to either Confluence or JIRA.
     *
     * @param localEntityType the type of entity being linked from
     * @param localKey the key of the entity being linked from
     * @param remoteEntityJSON the entity being linked to
     */
    private void createEntityLink(
            @Nonnull final String localEntityType,
            @Nonnull final String localKey,
            @Nonnull final String remoteEntityJSON)
    {
        checkNotNull(localKey);
        // See com.atlassian.applinks.core.rest.EntityLinkResource#createEntityLink for the URL format.
        applinksBackdoor.rootResource()
                .path("entitylink").path(localEntityType).path(localKey)
                .queryParam("reciprocate", FALSE)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .put(remoteEntityJSON);
    }
}
