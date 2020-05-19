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

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.oidc.agent.util.Constants;

/**
 * This class contains userinfo response.
 */
public class UserInfoResponse {

    private JSONObject mUserInfoResponse;
    static final String LOG_TAG = "UserInfoResponse";

    public UserInfoResponse(JSONObject userInfoResponse) {

        mUserInfoResponse = userInfoResponse;
    }

    /**
     * Returns the subject value of the userinfo response.
     * @return subject.
     */
    public String getSubject(){

        Log.i(LOG_TAG, "Call subject in userinfo service");
        return getUserInfoProperty(Constants.SUBJECT);
    }

    /**
     * Returns the claim values of additional claims returned in the userinfo response.
     * @param property Additional claim.
     * @return The claim value returned in the userinfo response.
     */
    public String getUserInfoProperty(String property) {

        Log.i(LOG_TAG, "Call userinfo service to get property");
        String userInfoProperty = null;
        try {
            userInfoProperty = (String) mUserInfoResponse.get(property);
        } catch (JSONException e) {

        }
        return userInfoProperty;
    }

    public JSONObject getUserInfoProperties() {

        Log.i(LOG_TAG, "Call userinfo service to get all claims");
        return mUserInfoResponse;

    }
}
