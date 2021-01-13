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

package io.asgardeo.android.oidc.sdk.handler;

import android.os.AsyncTask;
import android.util.Log;

import okio.Okio;
import org.json.JSONException;
import org.json.JSONObject;
import io.asgardeo.android.oidc.sdk.exception.ClientException;
import io.asgardeo.android.oidc.sdk.exception.ServerException;
import io.asgardeo.android.oidc.sdk.model.OIDCDiscoveryResponse;
import io.asgardeo.android.oidc.sdk.constant.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Handles the OIDC Discovery request flow to Identity Server.
 */
public class OIDCDiscoveryRequestHandler extends AsyncTask<Void, Void, OIDCDiscoveryResponse> {

    private String mDiscoveryEndpoint;
    private OIDCDiscoveryRespCallback mCallback;
    private static final String LOG_TAG = "OIDCDiscoveryRequest";
    private Exception mException;

    public OIDCDiscoveryRequestHandler(String discoveryEndpoint, OIDCDiscoveryRespCallback callback) {

        this.mDiscoveryEndpoint = discoveryEndpoint;
        this.mCallback = callback;

    }

    @Override
    protected OIDCDiscoveryResponse doInBackground(Void... voids) {

        OIDCDiscoveryResponse response = null;
        try {
            response = callDiscoveryUri();
        } catch (ServerException e) {
            Log.e(LOG_TAG, "Error while calling OIDC discovery endpoint", e);
        } catch (ClientException e) {
            Log.e(LOG_TAG, "Error while calling OIDC discovery endpoint", e);
        }
        return response;
    }

    /**
     * Call discovery endpoint of Identity Server.
     *
     * @return OAuthDiscovery.
     * @throws ServerException
     * @throws ClientException
     */
    private OIDCDiscoveryResponse callDiscoveryUri() throws ServerException, ClientException {

        HttpURLConnection conn;
        URL discoveryEndpoint;

        try {
            Log.d(LOG_TAG, "Call discovery service of identity server via: " + mDiscoveryEndpoint);
            discoveryEndpoint = new URL(mDiscoveryEndpoint);
            conn = (HttpURLConnection) discoveryEndpoint.openConnection();
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
            JSONObject jsonResponse = new JSONObject(response);
            return new OIDCDiscoveryResponse(jsonResponse);

        } catch (MalformedURLException exception) {
            mException = exception;
            throw new ClientException("Discovery endpoint is malformed. ", exception);
        } catch (IOException exception) {
            mException = exception;
            throw new ServerException("Error while calling the discovery endpoint. ", exception);
        } catch (JSONException exception) {
            mException = exception;
            throw new ServerException("Error while parsing the discovery response as JSON. ", exception);
        }
    }

    protected void onPostExecute(OIDCDiscoveryResponse response) {

        if (mException != null) {
            mCallback.onDiscoveryRequestCompleted(mException, null);
        } else {
            mCallback.onDiscoveryRequestCompleted(null, response);
        }
    }

    /**
     * Interface to handle token response.
     */
    public interface OIDCDiscoveryRespCallback {

        /**
         * Handle the flow after token request is completed.
         */
        void onDiscoveryRequestCompleted(Exception e, OIDCDiscoveryResponse oidcDiscoveryResponse);
    }
}
