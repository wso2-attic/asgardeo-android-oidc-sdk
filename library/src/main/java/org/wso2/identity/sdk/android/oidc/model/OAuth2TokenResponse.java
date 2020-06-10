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

/**
 * This class contains the TokenResponse.
 */
public class OAuth2TokenResponse implements Serializable {

    private static final long serialVersionUID = 8967247348511678909L;

    private String mTokenType;
    private String mAccessToken;
    private Long mAccessTokenExpirationTime;
    private String mIdToken;
    private String mRefreshToken;

    /**
     * Set idToken.
     *
     * @param idToken idToken.
     */
    public void setIdToken(String idToken) {
        this.mIdToken = idToken;
    }

    /**
     * Set accessToken.
     *
     * @param accessToken accesstoken.
     */
    public void setAccessToken(String accessToken) {
        this.mAccessToken = accessToken;
    }

    /**
     * Set refresh token.
     *
     * @param refreshToken refreshToken.
     */
    public void setRefreshToken(String refreshToken) {
        this.mRefreshToken = refreshToken;
    }

    /**
     * Set accessToken expiration time.
     *
     * @param accessTokenExpirationTime accessToken expiration time.
     */
    public void setAccessTokenExpirationTime(Long accessTokenExpirationTime) {
        this.mAccessTokenExpirationTime = accessTokenExpirationTime;
    }

    /**
     * Set token type.
     *
     * @param tokenType tokenType.
     */
    public void setTokenType(String tokenType) {
        this.mTokenType = tokenType;
    }

    /**
     * Returns idToken.
     *
     * @return idToken.
     */
    public String getIdToken() {
        return mIdToken;
    }

    /**
     * Returns accessToken.
     *
     * @return accessToken.
     */
    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * Returns tokenType.
     *
     * @return tokenType.
     */
    public String getTokenType() {
        return mTokenType;
    }

    /**
     * Returns Access Token Expiration Time.
     *
     * @return AccessTokenExpirationTime.
     */
    public Long getAccessTokenExpirationTime() {
        return mAccessTokenExpirationTime;
    }

    /**
     * Returns refresh token.
     *
     * @return refreshToken.
     */
    public String getRefreshToken() {
        return mRefreshToken;
    }
}
