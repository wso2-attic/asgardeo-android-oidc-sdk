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

package io.asgardio.android.oidc.sdk.model;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import io.asgardio.android.oidc.sdk.constant.Constants;

import java.io.Serializable;

/**
 * This class contains userinfo response.
 */
public class UserInfoResponse implements Serializable {

    private static final long serialVersionUID = -6173570286358038816L;
    private static final String LOG_TAG = "UserInfoResponse";

    private String mUserInfoResponse;

    public UserInfoResponse(JSONObject userInfoResponse) {

        mUserInfoResponse = userInfoResponse.toString();
    }

    /**
     * Returns the subject value of the userinfo response.
     *
     * @return subject.
     */
    public String getSubject() {

        return getUserInfoProperty(Constants.SUBJECT);
    }

    /**
     * Returns the claim values of additional claims returned in the userinfo response.
     *
     * @param property Additional claim.
     * @return The claim value returned in the userinfo response.
     */
    public String getUserInfoProperty(String property) {

        String userInfoProperty = null;
        try {
            JSONObject obj = new JSONObject(mUserInfoResponse);
            userInfoProperty = (String) obj.get(property);
            Log.d(LOG_TAG, "Get the value for the claim: " + property + " from userinfo response");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error while getting getting claims from userinfo response", e);
        }
        return userInfoProperty;
    }

    /**
     * Returns all claims from userinfo response.
     *
     * @return all claims.
     * @throws JSONException JSONException.
     */
    public JSONObject getUserInfoProperties() throws JSONException {

        Log.d(LOG_TAG, "Get all claim information from userinfo response");
        return new JSONObject(mUserInfoResponse);
    }
}
