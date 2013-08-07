package com.atlassian.jira.testkit.beans;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB-enabled entity list.
 *
 * @since v4.3
 */
@XmlRootElement
public class EntityList
{
    @XmlElement
    public ArrayList<EntityRefBean> entities;

    public EntityList()
    {
    }

    public EntityList(ArrayList<EntityRefBean> entities)
    {
        this.entities = entities;
    }
}
