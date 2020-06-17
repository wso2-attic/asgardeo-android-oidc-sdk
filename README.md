# Start enable Authentication for Android App

## Register Application



| Field                 | Value         | 
| --------------------- | ------------- | 
| Service Provider Name | sample-app  |
| Description           | This is a mobile application  | 
| Call Back Url         | wso2sample://oauth2  | 

Enable following properties:
- PKCE Mandatory
- Allow authentication without the client secret


## Configure the Android SDK in your project

### Initializing the  SDK

#### Add the dependency 

1. Clone this project: https://github.com/wso2-extensions/identity-sdks-android.git.

2. Build the library in your local maven. Run the following commands. Now the library will be available in your local .m2 cache. 
    - `./gradlew clean assembleRelease`
    - `./gradlew publishToMavenLocal `

3. Add `WSO2-SDK` dependency in `build.gradle` file.

```gradle
dependencies {
     implementation 'org.wso2.identity.sdk.android.oidc:wso2is-oidc-sdk:0.0.1'
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
    LoginService mLoginService = new DefaultLoginService(this);
```


#### Authorization.

Have a login button inside LoginActivity. Call the `doAuthorization()` method 
 when the login button is clicked to call authorization flow.
 
 `authorize(PendingIntent successIntent, PendingIntent failureIntent,
             Boolean callUserInfo)`
 
When calling authorize method of LoginService, you have to create completionIntent, and
  cancelIntent.
  

You can pass either true or false for callUserInfo parameter. If callUserInfo value is true, then userinfo request will be made to the IdentityServer after successful token exchange. Else if callUserInfo value is false, SDK will not make any request to UserInfo Endpoint after
   token flow.

```java
    findViewById(R.id.login).setOnClickListener(v ->
                   doAuthorization()
    );
```
   
```java
private void doAuthorization() {
   
      Intent completionIntent = new Intent(this, UserInfoActivity.class);
      Intent cancelIntent = new Intent(this, LoginActivity.class);
      cancelIntent.putExtra("failed", true);
      cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      PendingIntent successIntent = PendingIntent.getActivity(this, 0, completionIntent, 0);
      PendingIntent failureIntent = PendingIntent.getActivity(this, 0, cancelIntent, 0);

      mLoginService.authorize(successIntent, failureIntent, true);
   }
```
   


### Get The token response.

- After successful authorization, AuthenticationContext object will be returned in the Intent
. From the `oncreate()` method of the UserInfo.Activity, get the authenticationcontext object. 

- Authentication context object has oidcdiscovery response, tokenresponse, ans userinfo responses.

- In all flows such as userinfo and logout request, you need to pass this context object.
 
- In the authorization request, you need to create a Intent for successfull request and redirect to this activity.
```java
@Override
    protected void create() {  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mLoginService = new DefaultLoginService(this);
        mAuthenticationContext = (AuthenticationContext) getIntent().getSerializableExtra("context");
    }
``` 

 
### Get information related to token response: 

To get information related to token response, first you need to get TokenResponse from
 AuthenticationContext. You can use the following code blocks.

```OAuth2TokenResponse oAuth2TokenResponse = mAuthenticationContext.getOAuth2TokenResponse();```   
   
To get AccessToken and IDToken from  OAuth2TokenResponse
      
```
String idToken = oAuth2TokenResponse.getIdToken();
String accessToken = oAuth2TokenResponse.getAccessToken();
Long accessTokenExpTime = oAuth2TokenResponse.getAccessTokenExpirationTime();
```

### Get claims from IDToken

To get information from idToken , first you need to get IDTokenResponse from TokenResponse. 
You can use the following code blocks.

```
OAuth2TokenResponse.IDTokenResponse idTokenResponse = mAuthenticationContext
                .getOAuth2TokenResponse().getIdTokenResponse();
```

To get server specific claims from idToken:

```
String iss = idTokenResponse.getIssuer();
String sub = idTokenResponse.getSubject();
String iat = idTokenResponse.getIssueTime();
String exp = idTokenResponse.getExpiryTime();
List<String> audience = idTokenResponse.getAudience()

```

To get the map of all claims

`Map<String, Object> claims = idTokenResponse.getClaims();`

To get a specific String claim

`String claimValue = idTokenResponse.getClaim(claimName)`


### Get user details

### Get userinfo response from authentication context

If you called `LoginService.authorize(PendingIntent successIntent, PendingIntent failureIntent
, Boolean callUserInfo)` with callUserInfo parameter as true, then userinfo response will be
 stored in the AuthenticationContext object.
 
 To get UserInfoResponse from AuthenticationContext,

```UserInfoResponse userInfoResponse = mAuthenticationContext.getUserInfoResponse();```
 
- To get the subject,

    `String subject = userInfoResponse.getSubject();`
 
- To get some specific claim,

    `String email = userInfoResponse.getUserInfoProperty("email");`
    
- To get all claims,

    `JSONObject userClaims = userInfoResponse.getUserInfoProperties();`

 

### Call UserInfo explicitly.

You can get userclaims by calling User
```java
private void getUserInfo(){
   mLoginService.getUserInfo(mAuthenticationContext,
                  new UserInfoRequestHandler.UserInfoResponseCallback() {
               @Override
               public void onUserInfoRequestCompleted(UserInfoResponse userInfoResponse,
                       ServerException e) {
                   if (userInfoResponse != null) {
                       mSubject = userInfoResponse.getSubject();
                       mEmail = userInfoResponse.getUserInfoProperty("email");
                       JSONObject userInfoProperties = userInfoResponse.getUserInfoProperties();
                   }
    }
```

### Logout

- Call the logout method when logout button is clicked.

```java
findViewById(R.id.logout).setOnClickListener(v -> Logout());

```
- Call the logout method of LoginService instance.

```java
private void Logout() {

        mLoginService.logout(this, mAuthenticationContext);
        finish();
    }
```