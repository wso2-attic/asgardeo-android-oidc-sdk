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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import okio.Okio;
import org.json.JSONException;
import org.json.JSONObject;
import org.oidc.agent.exception.ClientException;
import org.oidc.agent.exception.ServerException;
import org.oidc.agent.util.ConfigManager;
import org.oidc.agent.util.Constants;
import org.oidc.agent.util.Util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Handles the login process by making use of AppAuth library.
 */
public class LoginService {

    private final AtomicReference<CustomTabsIntent> customTabIntent = new AtomicReference<>();
    private ConfigManager mConfigManager;
    private Context mContext;
    private OAuth2TokenResponse mOAuth2TokenResponse;
    private AuthorizationService mAuthorizationService;
    private static final String LOG_TAG = "LoginService";
    private AuthState mAuthState;
    private static LoginService mLoginService;
    private OAuthDiscoveryResponse mDiscovery;

    private LoginService(Context context) throws ClientException {

        mContext = context;
        if (mConfigManager == null) {
            mConfigManager = ConfigManager.getInstance(context);
        }
    }

    /**
     * Returns the login service instance.
     *
     * @param context Context
     * @return LoginService
     */
    public static LoginService getInstance(@NonNull Context context) throws ClientException {

        if (mLoginService == null) {
            mLoginService = new LoginService(context);
        }
        return mLoginService;
    }

    /**
     * Handles the authorization flow by getting the endpoints from discovery service.
     *
     * @param completionIntent
     * @param cancelIntent
     */
    public void doAuthorization(PendingIntent completionIntent, PendingIntent cancelIntent) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                mDiscovery = callDiscoveryUri();
            } catch (ServerException e) {
                Log.e(LOG_TAG, e.getMessage());
            } catch (ClientException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            authorizeRequest(completionIntent, cancelIntent);
        });
    }

    /**
     * Call discovery endpoint of Identity Server.
     *
     * @return OAuthDiscovery.
     * @throws ServerException
     * @throws ClientException
     */
    private OAuthDiscoveryResponse callDiscoveryUri() throws ServerException, ClientException {

        HttpURLConnection conn;
        URL userInfoEndpoint;

        try {
            Log.d(LOG_TAG, "Call discovery service of identity server via: " + mConfigManager
                    .getDiscoveryUri().toString());
            userInfoEndpoint = new URL(mConfigManager.getDiscoveryUri().toString());
            conn = (HttpURLConnection) userInfoEndpoint.openConnection();
            conn.setRequestMethod(Constants.HTTP_GET);
            conn.setDoInput(true);
            String response = Okio.buffer(Okio.source(conn.getInputStream()))
                    .readString(Charset.forName("UTF-8"));
            conn.disconnect();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(LOG_TAG, "Server returns" + conn.getResponseCode() + "when "
                        + "calling discovery endpoint");
                throw new ServerException("Server returns" + conn.getResponseCode() + "when "
                        + "calling discovery endpoint");
            }
            JSONObject discoveryResponse = new JSONObject(response);
            return new OAuthDiscoveryResponse(discoveryResponse);

        } catch (MalformedURLException e) {
            throw new ClientException("Discovery endpoint is malformed. ", e);
        } catch (IOException e) {
            throw new ServerException("Error while calling the discovery endpoint. ", e);
        } catch (JSONException e) {
            throw new ServerException("Error while parsing the discovery response as JSON. ", e);
        }
    }

    /**
     * Call authorization endpoint and authorize the request.
     *
     * @param completionIntent CompletionIntent.
     * @param cancelIntent     CancelIntent.
     */
    private void authorizeRequest(PendingIntent completionIntent, PendingIntent cancelIntent) {

        if (mDiscovery != null) {
            AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                    mDiscovery.getAuthorizationEndpoint(), mDiscovery.getTokenEndpoint());

            mAuthState = new AuthState(serviceConfiguration);
            AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                    serviceConfiguration, mConfigManager.getClientId(), ResponseTypeValues.CODE,
                    mConfigManager.getRedirectUri());
            builder.setScopes(mConfigManager.getScope());
            AuthorizationRequest request = builder.build();
            mAuthorizationService = new AuthorizationService(mContext);
            CustomTabsIntent.Builder intentBuilder = mAuthorizationService
                    .createCustomTabsIntentBuilder(request.toUri());
            customTabIntent.set(intentBuilder.build());
            mAuthorizationService
                    .performAuthorizationRequest(request, completionIntent, cancelIntent,
                            customTabIntent.get());
            Log.d(LOG_TAG, "Handling authorization request for service provider :" + mConfigManager
                    .getClientId());
        }
    }

    /**
     * Handle the token request.
     *
     * @param intent   intent.
     * @param callback callback.
     */
    public void handleAuthorization(Intent intent, TokenRequest.TokenRespCallback callback) {

        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        mOAuth2TokenResponse = new OAuth2TokenResponse();
        new TokenRequest(mAuthorizationService, mOAuth2TokenResponse, response, callback).execute();
        Log.d(LOG_TAG,
                "Handling token request for service provider :" + mConfigManager.getClientId());

    }

    /**
     * Handles logout request from the client application.
     *
     * @param context context.
     */
    public void logout(Context context) {

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(Constants.ID_TOKEN_HINT, mOAuth2TokenResponse.getIdToken());
        paramMap.put(Constants.POST_LOGOUT_REDIRECT_URI,
                mConfigManager.getRedirectUri().toString());
        try {
            String url = Util
                    .buildURLWithQueryParams(mDiscovery.getLogoutEndpoint().toString(), paramMap);
            Log.d(LOG_TAG, "Handling logout request for service provider :" + mConfigManager
                    .getClientId());
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "Error while creating logout request", e);
        }
    }

    public OAuth2TokenResponse getTokenResponse() {

        return mOAuth2TokenResponse;
    }

    public void getUserInfo(UserInfoRequest.UserInfoResponseCallback callback) {

        Log.i(LOG_TAG, "Call userinfo");
        new UserInfoRequest(mDiscovery, mOAuth2TokenResponse.getAccessToken(), callback).execute();
    }

    /**
     * Dispose the authorization service.
     */
    public void dispose() {

        if (mAuthorizationService != null) {
            mAuthorizationService.dispose();
        }
    }

    /**
     * Returns whether the user is logged in or not.
     *
     * @return true if the user is logged in, else returns false.
     */
    public boolean isUserLoggedIn() {

        return mAuthState.isAuthorized() && !mConfigManager.hasConfigurationChanged()
                && mAuthState.getAuthorizationServiceConfiguration() != null;
    }
}
