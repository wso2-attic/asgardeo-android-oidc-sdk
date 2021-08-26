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

package io.asgardeo.android.oidc.sdk.context;

import io.asgardeo.android.oidc.sdk.model.OAuth2TokenResponse;
import io.asgardeo.android.oidc.sdk.model.OIDCDiscoveryResponse;
import io.asgardeo.android.oidc.sdk.model.User;
import io.asgardeo.android.oidc.sdk.model.UserInfoResponse;

import java.io.Serializable;

/**
 * This class holds the all context related to authentication flow.
 */
public class AuthenticationContext implements Serializable {

    private static final long serialVersionUID = -6089927518022641315L;

    private OIDCDiscoveryResponse mDiscoveryResponse;
    private OAuth2TokenResponse mOAuth2TokenResponse;
    private UserInfoResponse mUserInfoResponse;
    private User mUser;

    /**
     * Set OIDCDiscoveryResponse.
     *
     * @param oidcDiscoveryResponse OIDCDiscoveryResponse.
     */
    public void setOIDCDiscoveryResponse(OIDCDiscoveryResponse oidcDiscoveryResponse) {
        this.mDiscoveryResponse = oidcDiscoveryResponse;
    }

    /**
     * Set OAuth2TokenResponse.
     *
     * @param oAuth2TokenResponse OAuth2TokenResponse.
     */
    public void setOAuth2TokenResponse(OAuth2TokenResponse oAuth2TokenResponse) {
        this.mOAuth2TokenResponse = oAuth2TokenResponse;
    }

    /**
     * Set UserInfoResponse.
     *
     * @param userInfoResponse UserInfoResponse.
     */
    public void setUserInfoResponse(UserInfoResponse userInfoResponse) {
        this.mUserInfoResponse = userInfoResponse;
    }

    /**
     * Set User
     *
     * @param user User;
     */
    public void setUser(User user) {
        this.mUser = user;
    }

    /**
     * Returns OAuth2TokenResponse.
     *
     * @return OAuth2TokenResponse.
     */
    public OAuth2TokenResponse getOAuth2TokenResponse() {
        return mOAuth2TokenResponse;
    }

    /**
     * Returns OIDCDiscoveryResponse.
     *
     * @return OIDCDiscoveryResponse.
     */
    public OIDCDiscoveryResponse getOIDCDiscoveryResponse() {
        return mDiscoveryResponse;
    }

    /**
     * Return UserInfoResponse.
     *
     * @return UserInfoResponse.
     */
    public UserInfoResponse getUserInfoResponse() {
        return mUserInfoResponse;
    }

    /**
     * Returns User;
     *
     * @return User;
     */
    public User getUser() {
        return mUser;
    }
}
