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

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class contains the TokenResponse.
 */
public class OAuth2TokenResponse implements Serializable {

    private static final long serialVersionUID = 8967247348511678909L;

    private String mTokenType;
    private String mAccessToken;
    private Long mAccessTokenExpirationTime;
    private String mIdToken;
    private String mRefreshToken;
    private IDTokenResponse mIdTokenResponse;

    /**
     * Set idToken.
     *
     * @param idToken idToken.
     */
    public void setIdToken(String idToken) {

        this.mIdToken = idToken;
        this.mIdTokenResponse = new IDTokenResponse();
    }

    /**
     * Set accessToken.
     *
     * @param accessToken accessToken.
     */
    public void setAccessToken(String accessToken) {
        this.mAccessToken = accessToken;
    }

    /**
     * Set refresh token.
     *
     * @param refreshToken refreshToken.
     */
    public void setRefreshToken(String refreshToken) {
        this.mRefreshToken = refreshToken;
    }

    /**
     * Set accessToken expiration time.
     *
     * @param accessTokenExpirationTime accessToken expiration time.
     */
    public void setAccessTokenExpirationTime(Long accessTokenExpirationTime) {
        this.mAccessTokenExpirationTime = accessTokenExpirationTime;
    }

    /**
     * Set token type.
     *
     * @param tokenType tokenType.
     */
    public void setTokenType(String tokenType) {
        this.mTokenType = tokenType;
    }

    /**
     * Returns idToken.
     *
     * @return idToken.
     */
    public String getIdToken() {
        return mIdToken;
    }

    /**
     * Returns accessToken.
     *
     * @return accessToken.
     */
    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * Returns tokenType.
     *
     * @return tokenType.
     */
    public String getTokenType() {
        return mTokenType;
    }

    /**
     * Returns Access Token Expiration Time.
     *
     * @return AccessTokenExpirationTime.
     */
    public Long getAccessTokenExpirationTime() {
        return mAccessTokenExpirationTime;
    }

    /**
     * Returns refresh token.
     *
     * @return refreshToken.
     */
    public String getRefreshToken() {
        return mRefreshToken;
    }

    /**
     * Returns IDToken Response.
     *
     * @return IDTokenResponse object.
     */
    public IDTokenResponse getIdTokenResponse() {

        return mIdTokenResponse;
    }

    /**
     * Stores IDToken response.
     */
    public class IDTokenResponse implements Serializable {

        private static final long serialVersionUID = -3623225641770681283L;

        /**
         * Returns subject(sub) claim of IdToken.
         *
         * @return subject.
         * @throws ParseException
         */
        public String getSubject() throws ParseException {

            return getJWTClaimsSet().getSubject();
        }

        /**
         * Returns Issuer(iss) claim of IdToken.
         *
         * @return Issuer.
         * @throws ParseException
         */
        public String getIssuer() throws ParseException {

            return getJWTClaimsSet().getIssuer();
        }

        /**
         * Returns Audience(aud) claim of IdTOken.
         *
         * @return Audience.
         * @throws ParseException
         */
        public List<String> getAudience() throws ParseException {

            return getJWTClaimsSet().getAudience();
        }

        /**
         * Returns IssueTime(iat) claim of IdToken.
         *
         * @return IssueTime.
         * @throws ParseException
         */
        public Date getIssueTime() throws ParseException {

            return getJWTClaimsSet().getIssueTime();
        }

        /**
         * Returns ExpiryTime(exp) of IDToken
         *
         * @return ExpiryTime.
         * @throws ParseException
         */
        public Date getExpiryTime() throws ParseException {

            return getJWTClaimsSet().getExpirationTime();
        }

        /**
         * Returns Map of all claims.
         *
         * @return Map of all claims.
         * @throws ParseException
         */
        public Map<String, Object> getClaims() throws ParseException {

            return getJWTClaimsSet().getClaims();
        }

        /**
         * Returns the claim value of the required claim.
         *
         * @param claim ClaimName.
         * @return ClaimValue.
         * @throws ParseException
         */
        public String getClaim(String claim) throws ParseException {

            return (String) getJWTClaimsSet().getStringClaim(claim);
        }

        private JWTClaimsSet getJWTClaimsSet() throws ParseException {

            return SignedJWT.parse(mIdToken).getJWTClaimsSet();
        }
    }
}
