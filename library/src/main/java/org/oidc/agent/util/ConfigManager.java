
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

import android.content.Context;
import android.content.SharedPreferences;
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
import org.oidc.agent.exception.ClientException;
import org.oidc.agent.library.R;

/**
 * Reads and validates the configuration from res/raw/oidc_config.json file.
 */
public class ConfigManager {

    private static WeakReference<ConfigManager> sInstance = new WeakReference<>(null);

    private final Context mContext;
    private final Resources mResources;

    private JSONObject mConfigJson;
    private String mClientId;
    private String mScope;
    private Uri mRedirectUri;
    private Uri mDicoveryUri;


    private static final String LOG_TAG = "ConfigManager";
    static final String KEY_LAST_HASH = "lastHash";


    private int mConfigHash;
    private String mConfigurationError;
    private final SharedPreferences prefs;


    private ConfigManager(Context context) throws ClientException {

        this.mContext = context;
        mResources = context.getResources();
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        readConfiguration(R.raw.oidc_config);
    }

    /**
     * Gives an instance of the ConfigManager class.
     *
     * @param context Context object with information about the current state of the application.
     * @return ConfigManager instance.
     */
    public static ConfigManager getInstance(Context context) throws ClientException {

        ConfigManager config = sInstance.get();
        if (config == null) {
            config = new ConfigManager(context);
            sInstance = new WeakReference<>(config);
        }

        return config;
    }

    /**
     * Returns the client id specified in the res/raw/config.json file.
     *
     * @return Client ID.
     */
    @NonNull
    public String getClientId() {

        return mClientId;
    }

    /**
     * Returns the authorization scope specified in the res/raw/config.json file.
     *
     * @return Authorization Scope.
     */
    @NonNull
    public String getScope() {

        return mScope;
    }

    /**
     * Returns the redirect URI specified in the res/raw/config.json file.
     *
     * @return Redirect URI.
     */
    @NonNull
    public Uri getRedirectUri() {

        return mRedirectUri;
    }

    /**
     * Returns the discovery endpoint URI derived from issuer uri specified in the res/raw/config
     * .json file.
     *
     * @return Token Endpoint URI.
     */
    @NonNull
    public Uri getDiscoveryUri() {

        return mDicoveryUri;
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
        mDicoveryUri = deriveDiscoveryUri(getRequiredConfigString(Constants.DISCOVERY_URI));
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
     * @param endpoint
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
            throw new ClientException(endpoint+ " must not have query parameters");
        }

        if (!TextUtils.isEmpty(uri.getEncodedFragment())) {
            throw new ClientException(endpoint+ " must not have a fragment");
        }
        return uri;
    }

    /**
     * Returns discovery URI.
     *
     * @param issuerUri Uri.
     * @return discovery URI.
     */
    private Uri deriveDiscoveryUri(String issuerUri) throws ClientException {

        if(issuerUri.contains(Constants.DISCOVERY_ENDPOINT)){
            Log.d(LOG_TAG, "Discovery endpoint is " + issuerUri);
            return getRequiredUri(issuerUri);
        }else{
            String derivedUri = issuerUri+ Constants.DISCOVERY_ENDPOINT;
            Log.d(LOG_TAG, "Discovery endpoint is " + derivedUri);
            return getRequiredUri(derivedUri);
        }
    }

    public boolean hasConfigurationChanged() {
        Integer lastKnownConfigHash = getLastKnownConfigHash();
        return lastKnownConfigHash == null || mConfigHash != lastKnownConfigHash;
    }

    private Integer getLastKnownConfigHash() {
        String hashString = prefs.getString(KEY_LAST_HASH, null);
        return hashString == null ? null : Integer.valueOf(hashString);
    }
}
