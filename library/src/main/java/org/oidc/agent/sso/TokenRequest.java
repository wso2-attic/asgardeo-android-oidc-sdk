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

package org.oidc.agent.sso;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

public class TokenRequest extends AsyncTask<Void, Void, OAuth2TokenResponse> {

    private AuthorizationService mAuthorizationService;
    private AuthorizationResponse mResponse;
    private TokenRespCallback mCallback;
    private static final String LOG_TAG = "TokenRequest";
    OAuth2TokenResponse oAuth2TokenResponse;

    TokenRequest(AuthorizationService mAuthorizationService,
            OAuth2TokenResponse mAuth2TokenResponse, AuthorizationResponse mResponse,
            TokenRespCallback mCallback) {
        this.mAuthorizationService = mAuthorizationService;
        oAuth2TokenResponse = mAuth2TokenResponse;
        this.mResponse = mResponse;
        this.mCallback = mCallback;
    }

    @Override
    protected OAuth2TokenResponse doInBackground(Void... voids) {

        mAuthorizationService.performTokenRequest(mResponse.createTokenExchangeRequest(),
                new AuthorizationService.TokenResponseCallback() {
                    @Override
                    public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse,
                            @Nullable AuthorizationException exception) {
                        if (exception != null) {
                            Log.e(LOG_TAG, "Token Exchange failed", exception);
                        } else {
                            if (tokenResponse != null) {
                                oAuth2TokenResponse.setAccessToken(tokenResponse.accessToken);
                                oAuth2TokenResponse.setIdToken(tokenResponse.idToken);
                                oAuth2TokenResponse.setAccessTokenExpirationTime(
                                        tokenResponse.accessTokenExpirationTime);
                                oAuth2TokenResponse.setRefreshToken(tokenResponse.refreshToken);
                                oAuth2TokenResponse.setTokenType(tokenResponse.tokenType);
                                mCallback.onTokenRequestCompleted(oAuth2TokenResponse);
                                mAuthorizationService.dispose();
                            }
                        }
                    }
                });
        return oAuth2TokenResponse;
    }

    /**
     * Interface to handle token response.
     */
    public interface TokenRespCallback {

        /**
         * Handle the flow after token request is completed.
         *
         * @param oAuth2TokenResponse OAuth2TokenResponse.
         */
        void onTokenRequestCompleted(OAuth2TokenResponse oAuth2TokenResponse);
    }
}
