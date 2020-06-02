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

package org.oidc.agent.handler;

import android.os.AsyncTask;
import android.util.Log;
import okio.Okio;
import org.json.JSONException;
import org.json.JSONObject;
import org.oidc.agent.context.AuthenticationContext;
import org.oidc.agent.exception.ClientException;
import org.oidc.agent.exception.ServerException;
import org.oidc.agent.model.UserInfoResponse;
import org.oidc.agent.util.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class UserInfoRequestHandler extends AsyncTask<Void, Void, UserInfoResponse> {

    private UserInfoResponseCallback mCallback;
    private ServerException mServerException;
    private UserInfoResponse mUserInfoResponse;
    private AuthenticationContext mAuthenticationContext;

    private static final String LOG_TAG = "UserInfoRequest";

    public UserInfoRequestHandler(AuthenticationContext authenticationContext,
            UserInfoResponseCallback callback) {

        this.mAuthenticationContext = authenticationContext;
        this.mCallback = callback;

    }

    @Override
    protected UserInfoResponse doInBackground(Void... voids) {

        if (mAuthenticationContext.getOAuth2TokenResponse() != null
                && mAuthenticationContext.getUserInfoResponse() !=null ) {
            Log.d(LOG_TAG, "There is already a userinfo response is stored");
            mUserInfoResponse = mAuthenticationContext.getUserInfoResponse();

        } else {
            try {
                if (mAuthenticationContext.getOIDCDiscoveryResponse() == null) {
                    throw new ClientException(
                            "DiscoveryResponse is null. Reinitiate the " + "authentication");
                }
                String accessToken = mAuthenticationContext.getOAuth2TokenResponse().getAccessToken();
                URL userInfoEndpoint = new URL(mAuthenticationContext.getOIDCDiscoveryResponse()
                        .getUserInfoEndpoint().toString());

                HttpURLConnection conn = (HttpURLConnection) userInfoEndpoint.openConnection();
                conn.setRequestProperty(Constants.AUTHORIZATION, Constants.BEARER + accessToken);
                conn.setInstanceFollowRedirects(false);
                String response = Okio.buffer(Okio.source(conn.getInputStream()))
                        .readString(Charset.forName("UTF-8"));
                Log.d(LOG_TAG, "Call userinfo endpoint: " + userInfoEndpoint);

                JSONObject json = new JSONObject(response);
                mUserInfoResponse = new UserInfoResponse(json);
                mAuthenticationContext.setUserInfoResponse(mUserInfoResponse);


            } catch (MalformedURLException e) {
                String error = "Error while calling userinfo endpoint";
                Log.e(LOG_TAG, error);
                mServerException = new ServerException(error, e);
            } catch (IOException e) {
                String error = "Error while calling userinfo endpoint";
                Log.e(LOG_TAG, error);
                mServerException = new ServerException(error, e);
            } catch (JSONException e) {
                String error = "Error while getting response from userinfo endpoint";
                Log.e(LOG_TAG, error);
                mServerException = new ServerException(error, e);
            } catch (ClientException e) {
                String error = "Error while calling from userinfo endpoint";
                Log.e(LOG_TAG, error);
                mServerException = new ServerException(error);
            }
        }

        return mUserInfoResponse;
    }

    protected void onPostExecute(UserInfoResponse response) {

        if (mServerException != null) {
            mCallback.onUserInfoRequestCompleted(null, mServerException);
        } else {
            mCallback.onUserInfoRequestCompleted(mUserInfoResponse, null);
        }
    }

    /**
     * Handle the userinfo response callback.
     */
    public interface UserInfoResponseCallback {

        /**
         * Handle the flow after userinfo request is completed.
         *
         * @param userInfoResponse UserInfoResponse
         */
        public void onUserInfoRequestCompleted(UserInfoResponse userInfoResponse,
                ServerException ex);
    }
}
