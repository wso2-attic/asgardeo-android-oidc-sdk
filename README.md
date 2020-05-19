# Start enable Authentication for Android App

## Register Application



| Field                 | Value         | 
| --------------------- | ------------- | 
| Service Provider Name | sample-app  |
| Description           | This is a mobile application  | 
| Call Back Url         | wso2sample://oauth  | 

Enable following properties:
- PKCE Mandatory
- Allow authentication without the client secret


## Configure the Android SDK in your project

### Initializing the  SDK

#### Add the dependency 

1. Clone this project: https://github.com/piraveena/android-sdk.git

2. Build the library in your local maven. Run the following commands. Now the library will be available in your local .m2 cache. 
    - `./gradlew clean assembleRelease`
    - `./gradlew publishToMavenLocal `

3. Add `WSO2-SDK` dependency in `build.gradle` file.

```gradle
dependencies {
     implementation 'org.wso2.carbon.identity.sso:wso2is-oidc-sdk:0.0.1'
}

```

#### Add a URI Scheme   

To redirect to the application from browser, it is necessary to add redirect scheme in the
 application. 


```gradle
android.defaultConfig.manifestPlaceholders = [
       'appAuthRedirectScheme': 'wso2sample'
]
```

Verify that this should be consistent with the redirectUri of the application that you configured in the developer-portal and in the oidc_config.json file.

For an example, if you have configured the callbackUrl as ‘wso2sample://oauth’, then the
 ‘appAuthRedirectScheme’ should be ‘wso2sample’


#### Configuration

Create a `oidc_config.json` file inside the `res/raw` folder. Add the following configs. 

- Add the client-id and redirect-uri of the application.

- Update the {HOST_NAME}:{PORT} with the IS server's hostname and port respectively in the discovery endpoint.

```json
{
 "client_id": {client-id},
 "redirect_uri": "{application-redirect-url},
 "authorization_scope": "openid",
 "discovery_uri": "https://{HOST_NAME}:{PORT}/oauth2/oidcdiscovery/.well-known/openid-configuration"
}
``````

Example:

```json
"client_id": "rs5ww91iychg9JN0DJGLMaxG2gha",
 "redirect_uri": "wso2sample://callback",
 "authorization_scope": "openid",
 "discovery_uri": "https://stgcloud.kubesb.com/t/example/oauth2/oidcdiscovery/.well-known/openid-configuration"
}
```

### Login

As the first step, you need to initialize SDK in the Activity#onCreate method of the Activity that you are using to
 log users into your app. 
In this example, we will call it LoginActivity:

```java
    mLoginService = LoginService.getInstance(this);
```


#### Authorization.

Have a login button inside LoginActivity. Call the `doAuthorization(Context context)` method 
 when the login button is clicked to call authorization flow.

```java
    findViewById(R.id.login).setOnClickListener(v ->
                   doAuthorization(this)
    );
```
   
```java
private void doAuthorization(Context context) {
   
      mLoginService = LoginService.getInstance(this);

      Intent completionIntent = new Intent(context, UserInfoActivity.class);
      Intent cancelIntent = new Intent(context, LoginActivity.class);
      cancelIntent.putExtra("failed", true);
      cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      PendingIntent pendingCompletionIntent = PendingIntent.getActivity(context, 0,
                     completionIntent, 0);
      PendingIntent pendingCancelIntent = PendingIntent.getActivity(context, 0, cancelIntent, 0);
    
      mLoginService.doAuthorization(pendingCompletionIntent, pendingCancelIntent);
   }
```
   


#### Get the accesstoken and idtoken.

- You can add this `handleAuthorizationResponse(Intent intent)` method inside a Activity when there is a successfull
 authentication response comes from the IDP. 
 
- In the authorization request, you need to create a Intent for successfull request and redirect to this activity.
```java
@Override
    protected void onStart() {  
        super.onStart();
        getConfigManager(this);
        handleAuthorizationResponse(getIntent());
    }
``` 
 
```java
private void handleAuthorizationResponse(Intent intent) {

      mLoginService.handleAuthorization(intent, new TokenRequest.TokenRespCallback() {
          @Override
          public void onTokenRequestCompleted(OAuth2TokenResponse oAuth2TokenResponse) {
              mOAuth2TokenResponse = oAuth2TokenResponse;
              getUserInfo();
          }
      });
  }
```
  
### Read UserInfo

```java
private void getUserInfo(){
    mLoginService.getUserInfo(new UserInfoRequest.UserInfoResponseCallback() {
        @Override
        public void onUserInfoRequestCompleted(UserInfoResponse userInfoResponse) {
            mSubject = userInfoResponse.getSubject();
            mEmail = userInfoResponse.getUserInfoProperty("email");
            JSONObject userInfoProperties = userInfoResponse.getUserInfoProperties();
            mIdToken = mOAuth2TokenResponse.idToken;
            mAccessToken = mOAuth2TokenResponse.accessToken;
        }
    });
    }
```

### Logout

- Call the logout method when logout button is clicked.

```java
findViewById(R.id.logout).setOnClickListener(v ->
                  singleLogout(this)
          ); 

```
- Call the logout method of LoginService instance.

```java
private void singleLogout(Context context) {

        mLoginService.logout(context);
        finish();
    }
```