# Asgardio Android OIDC SDK
[![Stackoverflow](https://img.shields.io/badge/Ask%20for%20help%20on-Stackoverflow-orange)](https://stackoverflow.com/questions/tagged/wso2is)
[![Join the chat at https://join.slack.com/t/wso2is/shared_invite/enQtNzk0MTI1OTg5NjM1LTllODZiMTYzMmY0YzljYjdhZGExZWVkZDUxOWVjZDJkZGIzNTE1NDllYWFhM2MyOGFjMDlkYzJjODJhOWQ4YjE](https://img.shields.io/badge/Join%20us%20on-Slack-%23e01563.svg)](https://join.slack.com/t/wso2is/shared_invite/enQtNzk0MTI1OTg5NjM1LTllODZiMTYzMmY0YzljYjdhZGExZWVkZDUxOWVjZDJkZGIzNTE1NDllYWFhM2MyOGFjMDlkYzJjODJhOWQ4YjE)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/wso2/product-is/blob/master/LICENSE)
[![Twitter](https://img.shields.io/twitter/follow/wso2.svg?style=social&label=Follow)](https://twitter.com/intent/follow?screen_name=wso2)
---
Asgardio Android OIDC SDK is a library that can be used to secure any Android application.
This android library currently supports:
- [OAuth 2.0 Authorization Code Flow](https://tools.ietf.org/html/rfc6749#section-4.1) using the [PKCE extension](https://tools.ietf.org/html/rfc7636)

## Table of Contents
- [Getting started](#getting-started)
- [Integrating OIDC SDK to your Android application](#integrating-oidc-sdk-to-your-android-application)
- [Authentication SPI](#authentication-spi)
- [Contributing](#contributing)
  * [Reporting issues](#reporting-issues)
- [License](#license)

## Getting started
You can experience the capabilities of Asgardio Android OIDC SDK by following this small guide which contains main
sections listed below.
+ [Configuring the Identity Server](#configuring-the-identity-server)
+ [Configuring the sample](#configuring-the-sample)
+ [Running the sample](#running-the-sample)
  - [Running in an Android Emulator](#running-in-an-android-emulator)
  - [Running in an Android Device](#running-in-an-android-device)

### Configuring the Identity Server
1. Start the WSO2 IS.
2. Access WSO2 IS management console from https://localhost:9443/carbon/ and create a service provider.
   ![Management Console](https://user-images.githubusercontent.com/15249242/91068131-6fc2d380-e651-11ea-9d0a-d58c825bbb68.png)
   i. Navigate to the `Service Providers` tab listed under the `Identity` section in the management console and click `Add`.<br/>
   ii. Provide a name for the Service Provider (ex:- sample-app) and click `Register`. Now you will be redirected to the
    `Edit Service Provider` page.<br/>
   iii. Expand the  `Inbound Authentication Configuration` section and click `Configure` under the `OAuth/OpenID Connect Configuration` section.<br/>
   iv. Provide the following values for the respective fields and click `Update` while keeping other default settings as it is.

       Callback Url - wso2sample://oauth2
       PKCE Mandatory - True
       Allow authentication without the client secret - True
   v. Click `Update` to save.

3. Once the service provider is saved, you will be redirected to the `Service Provider Details` page. Here, expand the
    `Inbound Authentication Configuration` section and click the `OAuth/OpenID Connect Configuration` section. Copy the
    value of  `OAuth Client Key` shown here.
    ![OAuth Client Credentials](https://user-images.githubusercontent.com/15249242/91567068-27155e00-e962-11ea-8eab-b3bdd790bfd4.png)

### Configuring the sample
1. Clone this project by running `git clone https://github.com/asgardio/asgardio-android-oidc-sdk.git`.

2. Open the cloned project directory via Android Studio.

3. Add the relevant configs in oidc_config.json file located in `res/raw` folder.

   - Replace the value of `client-id` with the value of `OAuth Client Key` property which you copied in the step 3 when
     [configuring the Identity Server](#configuring-the-identity-server).
   - Update the `{HOST_NAME}:{PORT`} with the IS server's `hostname` and `port` respectively

   ```json
   {
    "client_id": {client-id},
    "redirect_uri": "wso2sample://oauth2",
    "authorization_scope": "openid",
    "discovery_uri": "https://{HOST_NAME}:{PORT}/oauth2/oidcdiscovery/.well-known/openid-configuration"
   }
   ```

   Example:

   ```json
    "client_id": "rs5ww91iychg9JN0DJGLMaxG2gha",
    "redirect_uri": "wso2sample://oauth2",
    "authorization_scope": "openid",
    "discovery_uri": "https://localhost:9443/oauth2/oidcdiscovery/.well-known/openid-configuration"
   }
   ```

4. Add `mavenLocal()` under `repositories` and `allProjects` in  the `build.gradle` file (This `build.gradle` file is
   the top-level build file where you can add configuration options common to all sub-projects/modules).

    ```
    repositories {
        google()
        jcenter()
        mavenLocal()
     }

    allProjects {
        google()
        jcenter()
        mavenLocal()
     }
    ```

5. Run the following commands to build the project.
    - `./gradlew clean assembleRelease`
    - `./gradlew publishToMavenLocal `

### Running the sample
#### Running in an Android Emulator
1. Create a suitable Android Virtual Device in the Android Studio.

2. If the WSO2 IS is hosted in the local machine, change the domain of the endpoints in the `io.asgardio.android.oidc.sdk.sample/res/raw/oidc_config.json`
   file to “10.0.2.2”. Refer the documentation on [emulator-networking](https://developer.android.com/studio/run/emulator-networking)

3. By default IS uses a self-signed certificate. If you are using the default pack without
    changing to a CA signed certificate, follow this [guide](https://developer.android.com/training/articles/security-config) to get rid of SSL issues.

4. Change the hostname of IS as 10.0.2.2 in the <IS_HOME>/deployment.toml.<br/>
    i. Create a new keystore with CN as localhost and SAN as 10.0.2.2
    
         ```
         keytool -genkey -alias wso2carbon -keyalg RSA -keystore wso2carbon.jks -keysize 2048 -ext SAN=IP:10.0.2.2
         ```

    ii. Export the public certificate (name it as wso2carbon.pem)to add into the truststore.
    
         ```
         keytool -exportcert -alias wso2carbon -keystore wso2carbon.jks -rfc -file wso2carbon.pem
         ```
    iii. Import the certificate in the client-truststore.jks file located in `<IS_HOME>/repository/resources/security/`
    
         ```
         keytool -import -alias wso2is -file wso2carbon.pem -keystore client-truststore.jks -storepass wso2carbon
         ```
    iv. Now copy this public certificate (wso2carbon.pem) into the `io.asgardio.android.oidc.sdk.sample/res/raw` folder.

5. Select the Virtual Device to run the application.
6. Run the the module `io.asgardio.android.oidc.sdk.sample` on the selected Virtual Device.


#### Running in an Android Device
1. Enable USB Debugging in the Developer Options in the Android Device. Refer documentation on
   [Run your App](https://developer.android.com/training/basics/firstapp/running-app).

2. If the WSO2 IS is hosted in the local machine, change the domain of the endpoints in the `io.asgardio.android.oidc.sdk.sample/res/raw/oidc_config.json`  file and the hostnames specified under `hostname` config
   in the `<IS_HOME>/repository/conf/deployment.toml` file to the IP Address of local machine.
   Make sure that both the Android Device and the local machine is connected to the same WIFI network.

3. Connect the Android Device to the machine through a USB cable.

4. Select the Android Device as the Deployment Target.

5. Run the the module `io.asgardio.android.oidc.sdk.sample` on the selected Android Device.

## Integrating OIDC SDK to your Android application
This section will guide you on integrating OIDC into your Android application with the Asgardio Android OIDC SDK.
This allows an Android application (i.e. Service Provider) to connect with an IDP using OpenID protocol.
This guide consist with the following sections.
+ [Introduction](#introduction)
+ [Installing the SDK](#installing-the-sdk)
+ [Login](#login)
+ [Authentication context.](#authentication-context)
+ [Get user details.](#get-user-details)

### Introduction
A sample application is included in
https://github.com/asgardio/asgardio-android-oidc-sdk/tree/master/io.asgardio.android.oidc.sdk.sample
which we would use for the following section.
Here, we are using the sample as a reference only, we can follow the same approach to build our own app as well.
The structure of the sample would be as follows:

![Sample Structure](https://user-images.githubusercontent.com/15249242/91576045-377b0800-e965-11ea-83b9-83549e77e720.png)

Throughout this section we will refer to the Identity Server installation directory as IS_HOME.

### Installing the SDK
1. Add [latest released SDK](https://github.com/asgardio/asgardio-android-oidc-sdk) in the `build.gradle` file of the module `io.asgardio.android.oidc.sdk.sample`.

```gradle
dependencies {
   dependencies {
        implementation 'io.asgardio.android.oidc.sdk:io.asgardio.android.oidc.sdk:0.1.9'
   }
}
```

2. Add a redirect scheme in the saample application. You need to add the `appAuthRedirectScheme` in the
   `build.gradle` file of the module `io.asgardio.android.oidc.sdk.sample`.<br/>
    This should be consistent with the `CallBack Url` of the Service Provider that you configured in the
    WSO2 Identity Server and in the `oidc_config.json` file. Refer the [configuration section](#configuration) for further information.

    For example, if you have configured the callBackUrl as `wso2sample://oauth2`, then the `appAuthRedirectScheme` should
    be `wso2sample`

    ```gradle
    android.defaultConfig.manifestPlaceholders = [
           'appAuthRedirectScheme': 'wso2sample'
    ]
    ```

3. Create the `oidc_config.json` file with the following configuration inside the `res/raw` folder of the module
   `io.asgardio.android.oidc.sdk.sample`.

    ```json
    {
    "client_id": "rs5ww91iychg9JN0DJGLMaxG2gha",
     "redirect_uri": "wso2sample://oauth2",
     "authorization_scope": "openid",
     "discovery_uri": "https://localhost:9443/oauth2/oidcdiscovery/.well-known/openid-configuration"
    }
    ```

### Login
1. First, you need to initialize the SDK object in an `Activity` that you are using to log users into your app.
   For the purpose of this sample, we will call it `LoginActivity`.
2. We need to create another activity which after successful authentication, the user will be redirected to.
   Let's name it as `UserInfoActivity`.

```java
    LoginService mLoginService = new DefaultLoginService(this);
```


3. Add a `button` inside `LoginActivity`. Here the button id is referred as `login`.
4. We need to attach an event listener to `login button` to initiate the Authentication Request to WSO2 IS.
    ```java
        LoginService mLoginService = new DefaultLoginService(this);
        findViewById(R.id.login).setOnClickListener(v ->
                       doLogin()
        );
    ```

    ```java
    private void doLogin() {

          Intent completionIntent = new Intent(this, UserInfoActivity.class);
          Intent cancelIntent = new Intent(this, LoginActivity.class);
          cancelIntent.putExtra("failed", true);
          cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          PendingIntent successIntent = PendingIntent.getActivity(this, 0, completionIntent, 0);
          PendingIntent failureIntent = PendingIntent.getActivity(this, 0, cancelIntent, 0);

          mLoginService.authorize(successIntent, failureIntent, true);
       }
    ```
    - The`doLogin()` method will be called when the `login button` is clicked to initiate authentication with WSO2 Identity Server.

    - You need to create `completionIntent` and `cancelIntent` while calling the `authorize` method of `LoginService`.

    - You can pass either `true` or `false` for the `callUserInfo` parameter. If `callUserInfo` value is `true`,
      then `userinfo request` will be made to the IdentityServer after successful token exchange. Else, if `callUserInfo`
      value is `false`, SDK will not make any request to UserInfo Endpoint after token flow.

      Now you will be able to authenticate the user with Identity Server.

### Authentication context.

- After successful authentication, `AuthenticationContext` object will be returned in the Intent.
  This `AuthenticationContext` object is used to store all the context related to that authentication flow.

- From the `onCreate()` method of the `UserInfoActivity`, get the `AuthenticationContext` object.

- Authentication context object has `User`, `OidcDiscovery response`,   `TokenResponse`, and `UserInfoResponse`.

```java
@Override
    protected void create() {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mLoginService = new DefaultLoginService(this);
        mAuthenticationContext = (AuthenticationContext) getIntent().getSerializableExtra("context");
    }
```


### Get user details.
In order to get user-related information, we can use the following APIs.
```
String userName = mAuthenticationContext.getUser().getUserName();
Map<String, Object> userAttributes = mAuthenticationContext.getUser().getAttributes();
```

# Authentication SPI
* [Get information related to token response](#get-information-related-to-token-response)
* [Get claims from ID Token](#get-claims-from-id-token)
* [Get userinfo response](#get-userinfo-response)
   + [Get userinfo response from authentication context](#get-userinfo-response-from-authentication-context)
   + [Call UserInfo explicitly.](#call-userinfo-explicitly)
* [Logout](#logout)
## Get information related to token response

- To get information related to token response, first you need to get `OAuth2TokenResponse` from
   `AuthenticationContext`. You can use the following code blocks.

    ```OAuth2TokenResponse oAuth2TokenResponse = mAuthenticationContext.getOAuth2TokenResponse();```   
   
- To get AccessToken and IDToken from  OAuth2TokenResponse
      
    ```
    String idToken = oAuth2TokenResponse.getIdToken();
    String accessToken = oAuth2TokenResponse.getAccessToken();
    Long accessTokenExpTime = oAuth2TokenResponse.getAccessTokenExpirationTime();
    String tokenType = oAuth2TokenResponse.getTokenType();
    String refreshToken = oAuth2TokenResponse.getRefreshToken();
    ```

## Get claims from ID Token

- To get information from idToken , first you need to get `IDTokenResponse` from `OAuth2TokenResponse`. 
  You can use the following code blocks.

    ```
    OAuth2TokenResponse.IDTokenResponse idTokenResponse = mAuthenticationContext
                    .getOAuth2TokenResponse().getIdTokenResponse();
    ```

- To get server specific claims from idToken:

    ```
    String iss = idTokenResponse.getIssuer();
    String sub = idTokenResponse.getSubject();
    String iat = idTokenResponse.getIssueTime();
    String exp = idTokenResponse.getExpiryTime();
    List<String> audience = idTokenResponse.getAudience()
    
    ```

- To get the map of all claims

   `Map<String, Object> claims = idTokenResponse.getClaims();`

- To get a specific String claim

  `String claimValue = idTokenResponse.getClaim(claimName)`

## Get userinfo response
### Get userinfo response from authentication context

If you called `LoginService.authorize(PendingIntent successIntent, PendingIntent failureIntent
, Boolean callUserInfo)` with `callUserInfo` parameter as `true`, then `UserInfoResponse` will be
 stored in the `AuthenticationContext` object.
 
 - To get `UserInfoResponse` from `AuthenticationContext`,

    ```UserInfoResponse userInfoResponse = mAuthenticationContext.getUserInfoResponse();```
 
- To get the subject,

    `String subject = userInfoResponse.getSubject();`
 
- To get some specific claim,

    `String email = userInfoResponse.getUserInfoProperty("email");`
    
- To get all claims,

    `JSONObject userClaims = userInfoResponse.getUserInfoProperties();`

 

### Call UserInfo explicitly.
You can get userclaims by calling `getUserInfo(..)` method in the `LoginService`.

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
1. Create a button with id logout `LoginActivity`.
2. Call the `logout` method when logout button is clicked.

    ```java
    findViewById(R.id.logout).setOnClickListener(v -> logout());
    
    ```
    - Call the logout method of LoginService instance.
    
    ```java
    private void logout() {
            mLoginService.logout(this, mAuthenticationContext);
            finish();
        }
    ```

# Contributing
Please read [Contributing to the Code Base](http://wso2.github.io/) for details on our code of conduct, and the
 process for submitting pull requests to us.
 
## Reporting issues
We encourage you to report issues, improvements, and feature requests creating [git Issues](https://github.com/asgardio/asgardio-android-oidc-sdk/issues).

Important: And please be advised that security issues must be reported to security@wso2.com, not as GitHub issues, 
in order to reach the proper audience. We strongly advise following the WSO2 Security Vulnerability Reporting Guidelines
 when reporting the security issues.

# License
This project is licensed under the Apache License 2.0. See the [LICENSE
](LICENSE) file for details.

