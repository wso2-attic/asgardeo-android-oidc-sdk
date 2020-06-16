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
package org.wso2.identity.sdk.android.oidc.sso;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.browser.customtabs.CustomTabsIntent;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import org.wso2.identity.sdk.android.oidc.config.Configuration;
import org.wso2.identity.sdk.android.oidc.context.AuthenticationContext;
import org.wso2.identity.sdk.android.oidc.exception.ClientException;
import org.wso2.identity.sdk.android.oidc.config.FileBasedConfiguration;
import org.wso2.identity.sdk.android.oidc.handler.OIDCDiscoveryRequestHandler;
import org.wso2.identity.sdk.android.oidc.activity.TokenManagementActivity;
import org.wso2.identity.sdk.android.oidc.handler.UserInfoRequestHandler;
import org.wso2.identity.sdk.android.oidc.model.OAuth2TokenResponse;
import org.wso2.identity.sdk.android.oidc.model.OIDCDiscoveryResponse;
import org.wso2.identity.sdk.android.oidc.constant.Constants;
import org.wso2.identity.sdk.android.oidc.util.Util;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Provides default implementation for authentication and logout support using Identity server.
 */
public class DefaultLoginService implements LoginService {

    private final AtomicReference<CustomTabsIntent> customTabIntent = new AtomicReference<>();
    private Configuration mConfiguration;
    private WeakReference<Context> mContext;
    private OAuth2TokenResponse mOAuth2TokenResponse;
    private AuthorizationService mAuthorizationService;
    private static final String LOG_TAG = "LoginService";

    private DefaultLoginService(Context context) throws ClientException {

        mContext = new WeakReference<>(context);
        mConfiguration = FileBasedConfiguration.getInstance(context);
    }

    private DefaultLoginService(Context context, Configuration configuration)
            throws ClientException {

        mConfiguration = configuration;
        mContext = new WeakReference<>(context);
    }

    /**
     * Handles authorization flow by getting the endpoints from discovery service. If callUserInfo
     * value is true, then UserInfo request will happen. Else if callUserInfo value
     * is false, SDK will not make any request to UserInfo Endpoint after token flow. Application can call
     * userinfo endpoint explicitly by calling
     * {@link #getUserInfo(AuthenticationContext, UserInfoRequestHandler.UserInfoResponseCallback)}.
     * After successful authorization, AuthenticationContext object will be returned in the success
     * intent.
     *
     * @param successIntent Success intent.
     * @param failureIntent Failure Intent.
     * @param callUserInfo  If it is true, Request to UserInfo endpoint will happen after token
     *                      exchange. Else no request to user info endpoint.
     */
    public void authorize(PendingIntent successIntent, PendingIntent failureIntent,
            Boolean callUserInfo) {

        // Creating a authentication context object to store context.
        AuthenticationContext authenticationContext = new AuthenticationContext();
        mOAuth2TokenResponse = new OAuth2TokenResponse();
        new OIDCDiscoveryRequestHandler(mConfiguration.getDiscoveryUri().toString(),
                (exception, oidcDiscoveryResponse) -> {
                    if (exception != null) {
                        Log.e(LOG_TAG, "Error while calling discovery endpoint", exception);
                    } else {
                        Log.i(LOG_TAG, "CallUserInfo" + callUserInfo);
                        authenticationContext.setOIDCDiscoveryResponse(oidcDiscoveryResponse);
                        Log.i(LOG_TAG, oidcDiscoveryResponse.getAuthorizationEndpoint().toString());
                        authorizeRequest(TokenManagementActivity
                                        .createStartIntent(mContext.get(), successIntent, failureIntent,
                                                mOAuth2TokenResponse, authenticationContext, callUserInfo),
                                failureIntent, authenticationContext);
                    }

                }).execute();
    }

    /**
     * Call authorization endpoint and authorize the request.
     *
     * @param completionIntent CompletionIntent.
     * @param cancelIntent     CancelIntent.
     */
    private void authorizeRequest(PendingIntent completionIntent, PendingIntent cancelIntent,
            AuthenticationContext authenticationContext) {

        if (authenticationContext.getOIDCDiscoveryResponse() != null) {
            OIDCDiscoveryResponse oidcDiscoveryResponse = authenticationContext.getOIDCDiscoveryResponse();
            AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                    oidcDiscoveryResponse.getAuthorizationEndpoint(), oidcDiscoveryResponse.getTokenEndpoint());

            AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                    serviceConfiguration, mConfiguration.getClientId(), ResponseTypeValues.CODE,
                    mConfiguration.getRedirectUri());
            builder.setScopes(mConfiguration.getScope());
            AuthorizationRequest request = builder.build();
            mAuthorizationService = new AuthorizationService(mContext.get());
            CustomTabsIntent.Builder intentBuilder = mAuthorizationService
                    .createCustomTabsIntentBuilder(request.toUri());
            customTabIntent.set(intentBuilder.build());
            mAuthorizationService
                    .performAuthorizationRequest(request, completionIntent, cancelIntent,
                            customTabIntent.get());
            Log.d(LOG_TAG, "Handling authorization request for service provider :" + mConfiguration
                    .getClientId());

        } else {
            Log.d(LOG_TAG, "OIDC discovery response is null");
        }
    }

    /**
     * Handles logout request from the client application.
     */
    public void logout(Context context, AuthenticationContext authenticationContext) {

        OAuth2TokenResponse oAuth2TokenResponse = null;
        Map<String, String> paramMap = new HashMap<>();
        if (authenticationContext.getOAuth2TokenResponse() != null) {
            oAuth2TokenResponse = authenticationContext.getOAuth2TokenResponse();
            paramMap.put(Constants.ID_TOKEN_HINT, oAuth2TokenResponse.getIdToken());
        }

        paramMap.put(Constants.POST_LOGOUT_REDIRECT_URI,
                mConfiguration.getRedirectUri().toString());
        try {
            if (authenticationContext.getOIDCDiscoveryResponse() != null) {
                String url = Util.buildURLWithQueryParams(
                        authenticationContext.getOIDCDiscoveryResponse().getLogoutEndpoint()
                                .toString(), paramMap);
                Log.d(LOG_TAG, "Handling logout request for service provider :" + mConfiguration
                        .getClientId());
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setFlags(
                        Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                customTabsIntent.launchUrl(context.getApplicationContext(), Uri.parse(url));
                dispose(authenticationContext);
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "Error while creating logout request", e);
        }

    }

    /**
     * Returns userinfo response.
     *
     * @param callback UserInfoResponseCallback.
     */
    public void getUserInfo(AuthenticationContext context,
            UserInfoRequestHandler.UserInfoResponseCallback callback) {

        if (context.getOAuth2TokenResponse() != null) {
            new UserInfoRequestHandler(context, callback).execute();
        } else {
            Log.e(LOG_TAG, "User does not have a authenticated session");
        }
    }

    /**
     * Disposes the authorization service and authentication context.
     */
    private void dispose(AuthenticationContext authenticationContext) {

        if (mAuthorizationService != null) {
            mAuthorizationService.dispose();
        }
        authenticationContext.setOAuth2TokenResponse(null);
        authenticationContext.setOIDCDiscoveryResponse(null);
        authenticationContext.setUserInfoResponse(null);
    }
}
