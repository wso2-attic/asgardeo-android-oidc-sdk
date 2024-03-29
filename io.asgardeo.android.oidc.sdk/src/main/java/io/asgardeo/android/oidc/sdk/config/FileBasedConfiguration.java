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

package io.asgardeo.android.oidc.sdk.config;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;

import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;
import io.asgardeo.android.oidc.sdk.R;
import io.asgardeo.android.oidc.sdk.exception.ClientException;
import io.asgardeo.android.oidc.sdk.constant.Constants;

/**
 * Reads and validates the configuration from res/raw/oidc_config.json file.
 */
public class FileBasedConfiguration implements Configuration {

    private static WeakReference<FileBasedConfiguration> sInstance = new WeakReference<>(null);

    private final Resources mResources;

    private JSONObject mConfigJson;
    private String mClientId;
    private String mScope;
    private Uri mRedirectUri;
    private Uri mDiscoveryUri;

    private static final String LOG_TAG = "FileBasedConfiguration";

    private FileBasedConfiguration(Context context) throws ClientException {

        this.mResources = context.getResources();
        readConfiguration(R.raw.oidc_config);
    }

    /**
     * Returns an instance of the FileBasedConfiguration class.
     *
     * @param context Context object with information about the current state of the application.
     * @return FileBasedConfiguration instance.
     */
    public static FileBasedConfiguration getInstance(Context context) throws ClientException {

        FileBasedConfiguration config = sInstance.get();
        if (config == null) {
            config = new FileBasedConfiguration(context);
            sInstance = new WeakReference<>(config);
        }

        return config;
    }

    /**
     * Returns the client id specified in the res/raw/oidc_config.json file.
     *
     * @return Client ID.
     */
    @NonNull
    public String getClientId() {

        return mClientId;
    }

    /**
     * Returns the authorization scope specified in the res/raw/oidc_config.json file.
     *
     * @return Authorization Scope.
     */
    @NonNull
    public String getScope() {

        return mScope;
    }

    /**
     * Returns the redirect URI specified in the res/raw/oidc_config.json file.
     *
     * @return Redirect URI.
     */
    @NonNull
    public Uri getRedirectUri() {

        return mRedirectUri;
    }

    /**
     * Returns the discovery endpoint URI derived from issuer uri specified in the
     * res/raw/oidc_config
     * .json file.
     *
     * @return Token Endpoint URI.
     */
    @NonNull
    public Uri getDiscoveryUri() {

        return mDiscoveryUri;
    }

    /**
     * Reads the configuration values.
     */
    private void readConfiguration(int rawid) throws ClientException {

        BufferedSource configSource = Okio.buffer(Okio.source(mResources.openRawResource(rawid)));
        Buffer configData = new Buffer();

        try {
            configSource.readAll(configData);
            mConfigJson = new JSONObject(configData.readString(Charset.forName("UTF-8")));
        } catch (IOException ex) {
            throw new ClientException("Error while reading the config file");

        } catch (JSONException ex) {
            throw new ClientException("Error while parsing the config as json");

        }
        mClientId = getRequiredConfigString(Constants.CLIENT_ID);
        mScope = getRequiredConfigString(Constants.AUTHORIZATION_SCOPE);
        mRedirectUri = getRequiredUri(getRequiredConfigString(Constants.REDIRECT_URI));
        mDiscoveryUri = getRequiredUri(getRequiredConfigString(Constants.DISCOVERY_URI));
    }

    /**
     * Returns the Config String of the the particular property name.
     *
     * @param propName Property name.
     * @return Property value.
     */
    @NonNull
    private String getRequiredConfigString(String propName) {

        String value = mConfigJson.optString(propName);

        if (value != null) {
            value = value.trim();
            if (TextUtils.isEmpty(value)) {
                value = null;
            }
        }
        if (value == null) {
            Log.e(LOG_TAG, propName + " is required but not specified in the configuration");
        }
        return value;
    }

    /**
     * Returns Config URI.
     *
     * @param endpoint Endpoint
     * @return Uri
     */
    private Uri getRequiredUri(String endpoint) throws ClientException {

        Uri uri = Uri.parse(endpoint);

        if (!uri.isHierarchical() || !uri.isAbsolute()) {
            throw new ClientException(endpoint + " must be hierarchical and absolute");
        }

        if (!TextUtils.isEmpty(uri.getEncodedUserInfo())) {
            throw new ClientException(endpoint + " must not have user info");
        }

        if (!TextUtils.isEmpty(uri.getEncodedQuery())) {
            throw new ClientException(endpoint + " must not have query parameters");
        }

        if (!TextUtils.isEmpty(uri.getEncodedFragment())) {
            throw new ClientException(endpoint + " must not have a fragment");
        }
        return uri;
    }
}
