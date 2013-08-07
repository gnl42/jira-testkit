package com.atlassian.jira.testkit.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB bean for AppLinks EntityType.
 *
 * @since v4.3
 */
@XmlRootElement
public class EntityTypeBean
{
    @XmlElement
    public String applicationTypeClassName;
    @XmlElement
    public String i18nKey;
    @XmlElement
    public String pluralizedI18nKey;
    @XmlElement
    public String iconUrl;

    public EntityTypeBean()
    {
    }

    public EntityTypeBean(String applicationTypeClassName, String i18nKey, String pluralizedI18nKey, String iconUrl)
    {
        this.applicationTypeClassName = applicationTypeClassName;
        this.i18nKey = i18nKey;
        this.pluralizedI18nKey = pluralizedI18nKey;
        this.iconUrl = iconUrl;
    }

    @Override
    public String toString()
    {
        return "EntityTypeBean{" +
                "applicationTypeClassName='" + applicationTypeClassName + '\'' +
                ", i18nKey='" + i18nKey + '\'' +
                ", pluralizedI18nKey='" + pluralizedI18nKey + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                '}';
    }
}
