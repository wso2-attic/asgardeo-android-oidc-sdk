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

package org.oidc.agent.model;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * This class contains the TokenResponse.
 */
public class OAuth2TokenResponse implements Serializable {

    private String mTokenType;
    private String mAccessToken;
    private Long mAccessTokenExpirationTime;
    private String mIdToken;
    private String mRefreshToken;

    public void setIdToken(String idToken) {
        this.mIdToken = idToken;
    }

    public void setAccessToken(String accessToken) {
        this.mAccessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.mRefreshToken = refreshToken;
    }

    public void setAccessTokenExpirationTime(Long accessTokenExpirationTime) {
        this.mAccessTokenExpirationTime = accessTokenExpirationTime;
    }

    public void setTokenType(String tokenType) {
        this.mTokenType = tokenType;
    }

    public String getIdToken() {
        return mIdToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getTokenType() {
        return mTokenType;
    }

    public Long getAccessTokenExpirationTime() {
        return mAccessTokenExpirationTime;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

}
