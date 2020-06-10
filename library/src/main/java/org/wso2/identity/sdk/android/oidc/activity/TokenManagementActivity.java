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

package org.wso2.identity.sdk.android.oidc.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;
import net.openid.appauth.internal.Logger;
import org.wso2.identity.sdk.android.oidc.context.AuthenticationContext;
import org.wso2.identity.sdk.android.oidc.model.OAuth2TokenResponse;

public class TokenManagementActivity extends Activity {

    static final String KEY_COMPLETE_INTENT = "completeIntent";
    static final String KEY_CANCEL_INTENT = "cancelIntent";
    private static final String LOG_TAG = "TokenManagementActivity";
    private AuthorizationService mAuthorizationService;
    PendingIntent mCompleteIntent;
    PendingIntent mCancelIntent;
    private static OAuth2TokenResponse sResponse;
    static AuthenticationContext mAuthenticationContext;

    public static PendingIntent createStartIntent(Context context, PendingIntent completeIntent,
            PendingIntent cancelIntent, OAuth2TokenResponse response,
            AuthenticationContext authenticationContext) {

        Intent tokenExchangeIntent = new Intent(context, TokenManagementActivity.class);
        tokenExchangeIntent.putExtra(KEY_COMPLETE_INTENT, completeIntent);
        tokenExchangeIntent.putExtra(KEY_CANCEL_INTENT, cancelIntent);
        sResponse = response;
        mAuthenticationContext = authenticationContext;
        return PendingIntent
                .getActivity(context, 0, tokenExchangeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuthorizationService = new AuthorizationService(this);
        if (savedInstanceState == null) {
            extractState(getIntent().getExtras());
        } else {
            extractState(savedInstanceState);
        }

    }

    @Override
    protected void onStart() {

        super.onStart();
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());
        if (ex != null) {
            Log.w(LOG_TAG, "Authorization flow failed: " + ex);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sendPendingIntent(mCancelIntent);
                }
            });
        } else {
            AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
            if (response != null) {
                handleAuthorizationResponse(response);
            }
        }
    }

    /**
     * Call AuthorizationService to perform token request.
     *
     * @param response AuthorizationResponse.
     */
    private void handleAuthorizationResponse(AuthorizationResponse response) {

        mAuthorizationService.performTokenRequest(response.createTokenExchangeRequest(),
                this::handleTokenResponse);

    }

    /**
     * Handles the token response sent by AuthorizationService.
     *
     * @param tokenResponse TokenResponse.
     * @param exception     AuthorizationException.
     */
    private void handleTokenResponse(@Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException exception) {

        if (exception != null) {
            Log.e(LOG_TAG, "Token Exchange failed", exception);
        } else {
            if (tokenResponse != null) {
                Log.d(LOG_TAG, String.format("Token Response [ Access Token: %s, ID Token: %s ]",
                        tokenResponse.accessToken, tokenResponse.idToken));
                if (mCompleteIntent != null) {
                    Logger.debug("Authorization complete - invoking completion intent");
                    setOAuth2Response(tokenResponse);
                    mAuthorizationService.dispose();
                    sendSuccessIntent();
                }
            } else {
                sendPendingIntent(mCancelIntent);
            }
        }
        finish();
    }

    private void setOAuth2Response(TokenResponse tokenResponse) {

        sResponse.setAccessToken(tokenResponse.accessToken);
        sResponse.setIdToken(tokenResponse.idToken);
        sResponse.setAccessTokenExpirationTime(tokenResponse.accessTokenExpirationTime);
        sResponse.setRefreshToken(tokenResponse.refreshToken);
        sResponse.setTokenType(tokenResponse.tokenType);
        mAuthenticationContext.setOAuth2TokenResponse(sResponse);
    }

    private void sendSuccessIntent() {

        Intent intent = new Intent(this, mCompleteIntent.getIntentSender().getClass());
        intent.putExtra("context", mAuthenticationContext);
        try {
            mCompleteIntent.send(this, 0, intent);
        } catch (PendingIntent.CanceledException e) {
            Log.e(LOG_TAG, "Unable to send intent", e);
        }
        finish();
    }

    private void sendPendingIntent(PendingIntent pendingIntent) {

        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Log.e(LOG_TAG, "Unable to send intent", e);
        }
        finish();
    }

    private void extractState(Bundle state) {

        if (state == null) {
            Log.d(LOG_TAG, "Cannot handle response");
            finish();
            return;
        }
        mCompleteIntent = state.getParcelable(KEY_COMPLETE_INTENT);
        mCancelIntent = state.getParcelable(KEY_CANCEL_INTENT);
    }
}
