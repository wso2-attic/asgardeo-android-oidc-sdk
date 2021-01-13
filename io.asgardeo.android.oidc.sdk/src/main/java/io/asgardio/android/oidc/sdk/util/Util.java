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

package io.asgardeo.android.oidc.sdk.util;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This is the util class contains some utility methods required by the library.
 */
public class Util {

    /**
     * Handles adding query parameters to URL.
     *
     * @param url         URL
     * @param queryParams Map of query params.
     * @return URL appended with query params.
     * @throws UnsupportedEncodingException
     */
    public static String buildURLWithQueryParams(String url, Map<String, String> queryParams)
            throws UnsupportedEncodingException {

        List<String> queryParam1 = new ArrayList<>();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String encodedValue = URLEncoder
                    .encode(entry.getValue(), String.valueOf(Charset.forName("UTF-8")));
            queryParam1.add(entry.getKey() + "=" + encodedValue);
        }

        String queryString = StringUtils.join(queryParam1, "&");
        return appendQueryParamsStringToUrl(url, queryString);
    }

    /**
     * Append a query param to the URL (URL may already contain query params)
     */
    public static String appendQueryParamsStringToUrl(String url, String queryParamString) {
        String queryAppendedUrl = url;
        // Check whether param string to append is blank.
        if (StringUtils.isNotEmpty(queryParamString)) {
            // Check whether the URL already contains query params.
            String appender;
            if (url.contains("?")) {
                appender = "&";
            } else {
                appender = "?";
            }

            // Remove leading anchor or question mark in query params.
            if (queryParamString.startsWith("?") || queryParamString.startsWith("&")) {
                queryParamString = queryParamString.substring(1);
            }

            queryAppendedUrl += appender + queryParamString;
        }
        return queryAppendedUrl;
    }

    /**
     * Util method to convert JSONObject to Map.
     *
     * @param object JSONObject.
     * @return Map of the JSONObject.
     * @throws JSONException
     */
    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    private static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
