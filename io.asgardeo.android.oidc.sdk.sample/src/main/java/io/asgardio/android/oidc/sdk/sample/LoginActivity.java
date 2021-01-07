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

package io.asgardeo.android.oidc.sdk.sample;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.asgardeo.android.oidc.sdk.sso.DefaultLoginService;
import io.asgardeo.android.oidc.sdk.sso.LoginService;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.login).setOnClickListener(v -> doAuthorization());
    }

    /**
     * Handles the authorization code flow. Build the authorization request with the given
     * parameters and sent it to the IDP. If the authorization request is successful,
     * UserInfoActivity will handle it.
     */
    private void doAuthorization() {

        LoginService mLoginService = new DefaultLoginService(this);
        Intent completionIntent = new Intent(this, UserInfoActivity.class);
        Intent cancelIntent = new Intent(this, LoginActivity.class);
        cancelIntent.putExtra("failed", true);
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent successIntent = PendingIntent.getActivity(this, 0, completionIntent, 0);
        PendingIntent failureIntent = PendingIntent.getActivity(this, 0, cancelIntent, 0);

        mLoginService.authorize(successIntent, failureIntent, true);
    }
}
