/*
 * Copyright © 2012 - 2013 Atlassian Corporation Pty Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.atlassian.jira.testkit.client.restclient;

import com.sun.jersey.api.client.GenericType;

import java.util.List;
import java.util.Map;

/**
 * A represenation of an avatar bean in the REST API
 *
 * @since v5.0
 */
public class Avatar
{
    static GenericType<Map<String, List<Avatar>>> ALLAVATARS_TYPE = new GenericType<Map<String, List<Avatar>>>()
    {
    };
    static GenericType<Avatar> AVATAR_TYPE = new GenericType<Avatar>()
    {
    };

    private Long id;
    private String owner;
    private boolean isSelected;



    private boolean selected;
    private boolean isSystemAvatar;

    public boolean getIsSystemAvatar() {
        return isSystemAvatar;
    }

    public void setIsSystemAvatar(boolean systemAvatar) {
        isSystemAvatar = systemAvatar;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Long getId()
    {
        return id;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }


    public String getOwner()
    {
        return owner;
    }


}
