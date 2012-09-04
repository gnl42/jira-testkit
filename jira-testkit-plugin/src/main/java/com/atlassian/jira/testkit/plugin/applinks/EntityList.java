package com.atlassian.jira.testkit.plugin.applinks;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * JAXB-enabled entity list.
 *
 * @since v4.3
 */
@XmlRootElement
public class EntityList
{
    public final ArrayList<EntityRefBean> entities;

    public EntityList(ArrayList<EntityRefBean> entities)
    {
        this.entities = entities;
    }
}
