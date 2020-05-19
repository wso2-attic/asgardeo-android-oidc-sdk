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
import okio.Okio;
import org.json.JSONObject;
import org.oidc.agent.util.Constants;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class UserInfoRequest extends AsyncTask<Void, Void, UserInfoResponse> {

    private OAuthDiscoveryResponse mDiscovery;
    private String accessToken;
    private UserInfoResponseCallback mCallback;
    private static final String LOG_TAG = "UserInfoRequest";


    UserInfoRequest(OAuthDiscoveryResponse discovery, String accessToken,
            UserInfoResponseCallback callback) {

        this.mDiscovery = discovery;
        this.mCallback = callback;
        this.accessToken = accessToken;
    }

    @Override
    protected UserInfoResponse doInBackground(Void... voids) {

        UserInfoResponse userInfoResponse = null;
        Log.d(LOG_TAG, "Call userinfo endpoint: " + mDiscovery.getUserInfoEndpoint().toString());
        try {
            URL userInfoEndpoint = new URL(mDiscovery.getUserInfoEndpoint().toString());
            HttpURLConnection conn = (HttpURLConnection) userInfoEndpoint.openConnection();
            conn.setRequestProperty(Constants.AUTHORIZATION, Constants.BEARER + accessToken);
            conn.setInstanceFollowRedirects(false);
            String response = Okio.buffer(Okio.source(conn.getInputStream()))
                    .readString(Charset.forName("UTF-8"));

            JSONObject json = new JSONObject(response);
            userInfoResponse = new UserInfoResponse(json);
            mCallback.onUserInfoRequestCompleted(userInfoResponse);

        } catch (Exception e) {
            //
        }
        return userInfoResponse;
    }

    /**
     * Handle the userinfo response callback.
     */
    public interface UserInfoResponseCallback {

        /**
         * Handle the flow after userinfo request is completed.
         * @param userInfoResponse UserInfoResponse
         */
        void onUserInfoRequestCompleted(UserInfoResponse userInfoResponse);
    }
}
