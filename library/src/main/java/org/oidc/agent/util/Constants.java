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

package org.oidc.agent.util;

public class Constants {

    // Constants related to HTTP connection
    public static final String HTTP_GET = "GET";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    // Constants related to Configuration.
    public static final String PREFS_NAME = "config";
    public static final String KEY_LAST_HASH = "lastHash";
    public static final String DISCOVERY_ENDPOINT =
            "/oauth2/oidcdiscovery/.well-known/openid-configuration";
    public static final String DISCOVERY_URI = "discovery_uri";
    public static final String CLIENT_ID = "client_id";
    public static final String AUTHORIZATION_SCOPE = "authorization_scope";
    public static final String REDIRECT_URI = "redirect_uri";

    // Constants related to OIDC discovery response.
    public static final String AUTHORIZATION_ENDPOINT = "authorization_endpoint";
    public static final String TOKEN_ENDPOINT = "token_endpoint";
    public static final String LOGOUT_ENDPOINT = "end_session_endpoint";
    public static final String USERINFO_ENDPOINT = "userinfo_endpoint";

    // Constants related to OIDC userinfo response.
    public static final String SUBJECT = "sub";

    // Constants related to OIDC logout request.
    public static final String ID_TOKEN_HINT = "id_token_hint";
    public static final String POST_LOGOUT_REDIRECT_URI = "post_logout_redirect_uri";
}
