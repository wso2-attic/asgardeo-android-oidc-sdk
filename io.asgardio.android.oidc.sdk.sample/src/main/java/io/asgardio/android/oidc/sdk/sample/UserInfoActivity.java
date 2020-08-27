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

package io.asgardio.android.oidc.sdk.sample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import io.asgardio.android.oidc.sdk.sample.R;

import io.asgardio.android.oidc.sdk.context.AuthenticationContext;
import io.asgardio.android.oidc.sdk.model.OAuth2TokenResponse;
import io.asgardio.android.oidc.sdk.model.UserInfoResponse;
import io.asgardio.android.oidc.sdk.sso.DefaultLoginService;
import io.asgardio.android.oidc.sdk.sso.LoginService;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    private LoginService mLoginService;
    private static final String LOG_TAG = "UserInfoActivity";
    private static boolean tokenShown = false;
    private static boolean userShown = false;
    private String mName;
    private String mAccessToken;
    private String mIdToken;
    private Long accessTokenExpTime;
    private String accessTokenExpDate;
    private String tokenType;
    private String refreshToken;
    private AuthenticationContext mAuthenticationContext;
    private static final String AUTHENTICATION_CONTEXT = "context";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mLoginService = new DefaultLoginService(this);
        mAuthenticationContext = (AuthenticationContext) getIntent()
                .getSerializableExtra(AUTHENTICATION_CONTEXT);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        getTokenResponse();
        getIDTokenClaims();
        getUserInfoClaims();
        getUser();
        getUIContent();
    }

    private void getUser(){

        mName = mAuthenticationContext.getUser().getUserName();
        Map<String, Object> attr= mAuthenticationContext.getUser().getAttributes();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.user_details);

        for (Map.Entry<String, Object> claimEntry : attr.entrySet()) {
            Log.i(LOG_TAG, claimEntry.getKey() + " : " + claimEntry.getValue());

            RelativeLayout.LayoutParams keyParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView claimKey = new TextView(this);
            claimKey.setText(claimEntry.getKey());
            claimKey.setTextSize(14);
            claimKey.setGravity(Gravity.LEFT);
            claimKey.setTypeface(null, Typeface.BOLD);
            claimKey.setLayoutParams(keyParams);
            linearLayout.addView(claimKey);

            RelativeLayout.LayoutParams valueParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            valueParams.setMargins(0, 0, 0, 20);
            TextView claimValue = new TextView(this);
            claimValue.setText(claimEntry.getValue().toString());
            claimValue.setTextSize(14);
            claimValue.setGravity(Gravity.LEFT);
            claimValue.setTypeface(null, Typeface.NORMAL);
            claimValue.setLayoutParams(valueParams);
            linearLayout.addView(claimValue);
        }

    }

    private void showUserText() {
        Button userButton = findViewById(R.id.show_user_details);
        LinearLayout userView = findViewById(R.id.user_details);

        if (userShown) {
            userShown = false;
            userView.setVisibility(View.GONE);
            userButton.setText(R.string.showuserbtn);
        } else {
            userShown = true;
            userView.setVisibility(View.VISIBLE);
            userButton.setText(R.string.hideuserbtn);
        }
    }

    /**
     * Method to get OAuth2TokenResponse object from AuthenticationContext.
     */
    private void getTokenResponse() {

        // Get OAuth2TokenResponse object from authentication context.
        OAuth2TokenResponse oAuth2TokenResponse = mAuthenticationContext.getOAuth2TokenResponse();
        if (oAuth2TokenResponse != null) {
            mIdToken = oAuth2TokenResponse.getIdToken();
            mAccessToken = oAuth2TokenResponse.getAccessToken();
            accessTokenExpTime = oAuth2TokenResponse.getAccessTokenExpirationTime();
            tokenType = oAuth2TokenResponse.getTokenType();
            refreshToken = oAuth2TokenResponse.getRefreshToken();

            Timestamp stamp = new Timestamp(accessTokenExpTime);
            Date date = new Date(stamp.getTime());
            accessTokenExpDate = date.toString();

            Log.d(LOG_TAG,
                    String.format("Token Response [ Access Token: %s, ID Token: %s ]", mIdToken,
                            mAccessToken));
        }
    }

    /**
     * Method to get claims from IDToken from AuthenticationContext.
     */
    private void getIDTokenClaims() {

        OAuth2TokenResponse.IDTokenResponse idTokenResponse = mAuthenticationContext
                .getOAuth2TokenResponse().getIdTokenResponse();
        if (idTokenResponse != null) {
            // Get issuer claim from IDToken.
            Map<String, Object> claims = idTokenResponse.getClaims();
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.id_token_details);

            for (Map.Entry<String, Object> claimEntry : claims.entrySet()) {

                if ("exp" == claimEntry.getKey()) {
                    TextView expireText = findViewById(R.id.exp_value);
                    expireText.setText(claimEntry.getValue().toString());
                } else {
                    RelativeLayout.LayoutParams keyParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    TextView claimKey = new TextView(this);
                    claimKey.setText(claimEntry.getKey());
                    claimKey.setTextSize(14);
                    claimKey.setGravity(Gravity.LEFT);
                    claimKey.setTypeface(null, Typeface.BOLD);
                    claimKey.setLayoutParams(keyParams);
                    linearLayout.addView(claimKey);

                    RelativeLayout.LayoutParams valueParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    valueParams.setMargins(0, 0, 0, 20);
                    TextView claimValue = new TextView(this);
                    claimValue.setText(claimEntry.getValue().toString());
                    claimValue.setTextSize(14);
                    claimValue.setGravity(Gravity.LEFT);
                    claimValue.setTypeface(null, Typeface.NORMAL);
                    claimValue.setLayoutParams(valueParams);
                    linearLayout.addView(claimValue);
                }

            }

        }
    }

    private void setClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData
                .newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Get Claims from UserInfoResponse object from AuthenticationContext.
     */
    private void getUserInfoClaims() {

        UserInfoResponse userInfoResponse = mAuthenticationContext.getUserInfoResponse();
        if (userInfoResponse != null) {
            String subject = userInfoResponse.getSubject();
            Iterator<String> keys = userInfoResponse.getUserInfoProperties().keys();
            try {
                while (keys.hasNext()) {
                    String claimName = keys.next();
                    String claimValue = (String) userInfoResponse.getUserInfoProperties()
                            .get(claimName);
                    Log.d(LOG_TAG, claimName + " : " + claimValue);
                }
            } catch (JSONException exception) {
                Log.e(LOG_TAG, "Error while getting user claims", exception);
            }

        }
    }

    /**
     * Add UI content.
     */
    private void getUIContent() {
        addUiElements();

        findViewById(R.id.show_id_token_details).setOnClickListener(v -> showIDTokenText());
        findViewById(R.id.show_access_token_details).setOnClickListener(v -> showAccessTokenText());
        findViewById(R.id.id_copy).setOnClickListener(v -> copyIdToken());
        findViewById((R.id.access_copy)).setOnClickListener(v -> copyAccessToken());
        findViewById(R.id.show_user_details).setOnClickListener(v -> showUserText());

    }

    private void copyIdToken() {
        TextView idToken = findViewById(R.id.idtoken);
        CharSequence text = "ID Token Copied to clipboard";
        setClipboard(this.getApplicationContext(), idToken.getText().toString());
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void copyAccessToken() {
        TextView accessToken = findViewById(R.id.accesstoken);
        CharSequence text = "Access Token Copied to clipboard";
        setClipboard(this.getApplicationContext(), accessToken.getText().toString());
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showIDTokenText() {
        Button testButton = findViewById(R.id.show_id_token_details);
        LinearLayout idTokenView = findViewById(R.id.id_token_details);

        if (tokenShown) {
            tokenShown = false;
            idTokenView.setVisibility(View.GONE);
            testButton.setText(R.string.showbtn);
        } else {
            tokenShown = true;
            idTokenView.setVisibility(View.VISIBLE);
            testButton.setText(R.string.hidebtn);
        }
    }

    private void showAccessTokenText() {
        Button testButton = findViewById(R.id.show_access_token_details);
        LinearLayout idTokenView = findViewById(R.id.access_token_details);

        if (tokenShown) {
            tokenShown = false;
            idTokenView.setVisibility(View.GONE);
            testButton.setText(R.string.showbtn);
        } else {
            tokenShown = true;
            idTokenView.setVisibility(View.VISIBLE);
            testButton.setText(R.string.hidebtn);
        }
    }

    /**
     * Handles logout for the application.
     */
    private void Logout() {

        mLoginService.logout(this, mAuthenticationContext);
        finish();
    }

    private void addUiElements() {

        TextView usernameView = findViewById(R.id.username);
        TextView name = findViewById(R.id.name);
        TextView idTokenView = findViewById(R.id.idtoken);

        TextView accessTokenView = findViewById(R.id.accesstoken);
        TextView accessTokenType = findViewById(R.id.acc_type_value);
        TextView accessTokenExp = findViewById(R.id.acc_exp_value);
        TextView refreshTokenView = findViewById(R.id.refreshToken_value);

        idTokenView.setText(mIdToken);
        name.setText(mName);
        if (mName != null) {
            usernameView.setText("Hey ".concat(
                    mName.substring(0, 1).toUpperCase() + mName.substring(1) + ","));
        }

        accessTokenView.setText(mAccessToken);
        accessTokenType.setText(tokenType);
        accessTokenExp.setText(accessTokenExpDate);
        refreshTokenView.setText(refreshToken);
    }
}

