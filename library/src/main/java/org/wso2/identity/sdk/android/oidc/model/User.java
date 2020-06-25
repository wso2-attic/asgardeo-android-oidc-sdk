/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.identity.sdk.android.oidc.model;

import java.io.Serializable;
import java.util.Map;

/**
 * User is the class that represents the authenticated user.
 */
public class User implements Serializable {

    private static final long serialVersionUID = -2805372902879554156L;

    private String mUserName;
    private Map<String, Object> mAttributes;

    /**
     * Set username.
     *
     * @param userName UserName.
     */
    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    /**
     * Set attributes of user.
     *
     * @param attributes UserAttributes.
     */
    public void setAttributes(Map<String, Object> attributes) {
        this.mAttributes = attributes;
    }

    /**
     * Returns username of the user.
     *
     * @return UserName.
     */
    public String getUserName() {
        return mUserName;
    }

    /**
     * Returns attributes of the user.
     *
     * @return Map of attributes.
     */
    public Map<String, Object> getAttributes() {
        return mAttributes;
    }
}
