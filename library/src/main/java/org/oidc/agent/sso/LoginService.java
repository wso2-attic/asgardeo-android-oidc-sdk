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
import org.oidc.agent.context.AuthenticationContext;
import org.oidc.agent.handler.UserInfoRequestHandler;
import org.oidc.agent.model.OAuth2TokenResponse;

public interface LoginService {

    /**
     * Handles authorization flow.
     *
     * @param successIntent Success intent.
     * @param failureIntent Failure Intent.
     */
    void authorize(PendingIntent successIntent, PendingIntent failureIntent);

    /**
     * Get call userinfo endpoint.
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

