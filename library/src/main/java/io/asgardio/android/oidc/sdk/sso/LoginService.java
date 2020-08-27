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

package io.asgardio.android.oidc.sdk.sso;

import android.app.PendingIntent;
import android.content.Context;
import io.asgardio.android.oidc.sdk.context.AuthenticationContext;
import io.asgardio.android.oidc.sdk.handler.UserInfoRequestHandler;

/**
 * Provides the interface for authentication and logout mechanism.
 */
public interface LoginService {

    /**
     * Handles authorization flow and if callUserInfo value is true, then userinfo request will
     * be made to the IdentityServer after successful token exchange. Else if callUserInfo value
     * is false, SDK will not make any request to UserInfo Endpoint after token flow. Application can call
     * userinfo endpoint explicitly by calling
     * {@link #getUserInfo(AuthenticationContext, UserInfoRequestHandler.UserInfoResponseCallback)}
     *
     * @param successIntent Success intent.
     * @param failureIntent Failure Intent.
     * @param callUserInfo  If it is true, Request to UserInfo endpoint will happen after token
     *                      exchange. Else no request to user info endpoint.
     */
    void authorize(PendingIntent successIntent, PendingIntent failureIntent, Boolean callUserInfo);

    /**
     * Handles the call to UserInfo endpoint.
     *
     * @param context  Authentication context.
     * @param callback Callback.
     */
    void getUserInfo(AuthenticationContext context,
            UserInfoRequestHandler.UserInfoResponseCallback callback);

    /**
     * Handles logout flow.
     *
     * @param context               Context
     * @param authenticationContext AuthenticationContext
     */
    void logout(Context context, AuthenticationContext authenticationContext);
}
