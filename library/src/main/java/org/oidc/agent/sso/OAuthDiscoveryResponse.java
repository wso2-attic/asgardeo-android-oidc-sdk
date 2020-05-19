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

import android.net.Uri;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.oidc.agent.util.Constants;

/**
 * This class stores the discovery response.
 */
public class OAuthDiscoveryResponse {

    private JSONObject mDiscoveryResponse;
    static final String LOG_TAG = "OAuthDiscoveryResponse";

    public OAuthDiscoveryResponse(JSONObject discoveryResponse) {

        mDiscoveryResponse = discoveryResponse;
    }

    /**
     * Get token endpoint from discovery object.
     *
     * @return Token endpoint
     */
    public Uri getTokenEndpoint() {

        Log.i(LOG_TAG, "Get token endpoint");
        return getRequiredUri(Constants.TOKEN_ENDPOINT);
    }

    /**
     * Get Authorization Endpoint from discovery object.
     *
     * @return Token endpoint
     */
    public Uri getAuthorizationEndpoint() {

        Log.i(LOG_TAG, "Get authorization endpoint");
        return getRequiredUri(Constants.AUTHORIZATION_ENDPOINT);
    }

    /**
     * Get Logout Endpoint from discovery object.
     *
     * @return Token endpoint
     */
    public Uri getLogoutEndpoint() {

        Log.d(LOG_TAG, "Get Logout endpoint");
        return getRequiredUri(Constants.LOGOUT_ENDPOINT);
    }

    /**
     * Get Logout Endpoint from discovery object.
     *
     * @return Token endpoint
     */
    public Uri getUserInfoEndpoint() {

        Log.d(LOG_TAG, "Get UserInfo endpoint");
        return getRequiredUri(Constants.USERINFO_ENDPOINT);
    }

    /**
     * Get the required endpoint from discovery object.
     *
     * @param endpointName Returns the endpoint corresponding to the name.
     * @return Endpoint URI.
     */
    public Uri getRequiredUri(String endpointName) {

        Uri endpoint = null;
        try {
            endpoint = Uri.parse(getDiscoveryProperty(endpointName));
        } catch (Throwable ex) {
            Log.e(LOG_TAG, endpointName + "could not be parsed ");
        }
        return endpoint;
    }

    /**
     * Returns the property from the discovery object
     *
     * @param property Discovery property name
     * @return Supported Value for that property from discovery object
     */
    public String getDiscoveryProperty(String property) {

        String discoveryProperty = null;
        try {
            discoveryProperty = (String) mDiscoveryResponse.get(property);
        } catch (JSONException e) {

        }
        return discoveryProperty;
    }
}
