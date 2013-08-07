package com.atlassian.jira.testkit.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB bean for AppLinks EntityReference.
 *
 * @since v4.3
 */
@XmlRootElement
public class EntityRefBean
{
    @XmlElement
    public String key;
    @XmlElement
    public EntityTypeBean type;
    @XmlElement
    public String name;

    public EntityRefBean()
    {
    }

    public EntityRefBean(String key, EntityTypeBean type, String name)
    {
        this.key = key;
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "EntityRefBean{" +
                "key='" + key + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
