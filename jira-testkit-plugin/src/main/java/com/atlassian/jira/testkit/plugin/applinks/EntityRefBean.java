package com.atlassian.jira.testkit.plugin.applinks;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB bean for AppLinks EntityReference.
 *
 * @since v4.3
 */
@XmlRootElement
class EntityRefBean
{
    public final String key;
    public final EntityTypeBean type;
    public final String name;

    EntityRefBean(String key, EntityTypeBean type, String name)
    {
        this.key = key;
        this.type = type;
        this.name = name;
    }
}
