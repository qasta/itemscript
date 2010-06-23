/*
 * Copyright © 2010, Data Base Architects, Inc. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the names of Kalinda Software, DBA Software, Data Base Architects, Itemscript
 *       nor the names of its contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * Author: Jacob Davies
 */

package org.itemscript.core.gwt;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;

/**
 * This class assists in the creation and use of GWT {@link Request}s.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 * 
 */
public class RequestUtils {
    public static final String VALUE_KEY = "value";
    public static final String METHOD_KEY = "method";

    /**
     * Build a query string from a JsonObject.
     * 
     * @param params The JsonObject to build from.
     * @return The query string.
     */
    public static String buildQueryString(JsonObject params) {
        if (params != null) {
            boolean first = true;
            StringBuffer sb = new StringBuffer();
            for (String key : params.keySet()) {
                JsonValue entry = params.get(key);
                if (entry.isArray()) {
                    for (JsonValue value : entry.asArray()) {
                        processQueryStringEntry(first, sb, key, value);
                    }
                } else {
                    processQueryStringEntry(first, sb, key, entry);
                }
                if (first) {
                    first = false;
                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * Build a request URL from a base URL and a JsonObject of
     * parameters.
     * 
     * @param baseUrl The base URL.
     * @param params The query parameters.
     * @return The request URL.
     */
    public static String buildRequestUrl(String baseUrl, JsonObject params) {
        if (params != null) {
            String queryString = buildQueryString(params);
            baseUrl += "?" + queryString;
        }
        return baseUrl;
    }

    private static void processQueryStringEntry(boolean first, StringBuffer sb, String key, JsonValue value) {
        if (!first) {
            sb.append("&");
        }
        // encode the characters in the name
        String encodedName = URL.encodeComponent(key);
        sb.append(encodedName);
        sb.append("=");
        // encode the characters in the value
        String encodedValue = URL.encodeComponent(value.asString()
                .stringValue());
        sb.append(encodedValue);
        first = false;
    }

    /**
     * Send a DELETE request.
     * 
     * @param url The URL to DELETE.
     * @param callback The callback to call when the DELETE request completes.
     * @return The Request object for this request.
     */
    public static Request sendDeleteRequest(String url, RequestCallback callback) {
        Request request = null;
        try {
            request = new RequestBuilder(RequestBuilder.DELETE, url).sendRequest(null, callback);
        } catch (RequestException e) {
            if (callback != null) {
                callback.onError(request, e);
            }
        }
        return request;
    }

    /**
     * Send a GET request with parameters.
     * 
     * @param url The base URL to GET (without query string).
     * @param params The query parameters.
     * @param callback The callback to call when the GET request completes.
     * @return The Request object for this request.
     */
    public static Request sendGetRequest(String url, JsonObject params, RequestCallback callback) {
        Request request = null;
        try {
            request =
                    new RequestBuilder(RequestBuilder.GET, buildRequestUrl(url, params)).sendRequest(null,
                            callback);
        } catch (RequestException e) {
            if (callback != null) {
                callback.onError(request, e);
            }
        }
        return request;
    }

    /**
     * Send a GET request without specifying parameters, or with parameters already encoded in the
     * query string.
     * 
     * @param url The URL to GET (with or without query string).
     * @param callback The callback to call when the GET request completes.
     * @return The Request object for this request.
     */
    public static Request sendGetRequest(String url, RequestCallback callback) {
        return sendGetRequest(url, null, callback);
    }

    /**
     * Send a GET request with a JSON payload in the query string.
     * 
     * @param url The base URL to GET (without a query string).
     * @param json The JSON to send in the query string.
     * @param callback The callback to call when the GET request completes.
     * @return The Request object for this request.
     */
    public static Request sendJsonGetRequest(String url, final JsonValue json, RequestCallback callback) {
        Request request = null;
        try {
            request =
                    new RequestBuilder(RequestBuilder.GET, url + "?" + Url.encode(json.toCompactJsonString())).sendRequest(
                            null, callback);
        } catch (RequestException e) {
            if (callback != null) {
                callback.onError(request, e);
            }
        }
        return request;
    }

    /**
     * Send a POST request with a JSON payload.
     * 
     * @param url The URL to POST to.
     * @param json The JSON to send in the POST request.
     * @param callback The callback to call when the POST request completes.
     * @return The Request object for this request.
     */
    public static Request sendJsonPostRequest(String url, JsonValue json, RequestCallback callback) {
        Request request = null;
        try {
            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
            builder.setHeader("content-type", "application/json");
            request = builder.sendRequest(json.toCompactJsonString(), callback);
        } catch (RequestException e) {
            if (callback != null) {
                callback.onError(request, e);
            }
        }
        return request;
    }

    /**
     * Send a PUT request with a JSON payload.
     * 
     * @param url The URL to PUT to.
     * @param json The JSON to send.
     * @param callback The callback to call when the PUT request completes.
     * @return The Request object for this request.
     */
    public static Request sendJsonPutRequest(String url, JsonValue json, RequestCallback callback) {
        Request request = null;
        try {
            RequestBuilder builder = new RequestBuilder(RequestBuilder.PUT, url);
            builder.setHeader("content-type", "application/json");
            request = builder.sendRequest(json.toCompactJsonString(), callback);
        } catch (RequestException e) {
            if (callback != null) {
                callback.onError(request, e);
            }
        }
        return request;
    }

    /**
     * Send a standard POST request.
     * 
     * The parameters will be encoded as application/x-www-form-urlencoded.
     * 
     * @param url The URL to POST to.
     * @param params The query parameters.
     * @param callback The callback to call when the POST request completes.
     * @return The Request object for this request.
     */
    public static Request sendPostRequest(String url, JsonObject params, RequestCallback callback) {
        Request request = null;
        try {
            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
            builder.setHeader("content-type", "application/x-www-form-urlencoded");
            request = builder.sendRequest(buildQueryString(params), callback);
        } catch (RequestException e) {
            if (callback != null) {
                callback.onError(request, e);
            } else {
                throw new ItemscriptError("error.util.requestUtils.post.request.failed.no.callback", e);
            }
        }
        return request;
    }

    public static Request sendPostEncapsulatedRequest(JsonSystem system, String url, String method,
            JsonValue value, RequestCallback callback) {
        Request request = null;
        try {
            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
            builder.setHeader("content-type", "application/x-www-form-urlencoded");
            request = builder.sendRequest(buildQueryString(system.createObject()
                    .p(METHOD_KEY, method)
                    .p(VALUE_KEY, value != null ? value.toCompactJsonString() : "")), callback);
        } catch (RequestException e) {
            if (callback != null) {
                callback.onError(request, e);
            } else {
                throw new ItemscriptError("error.util.requestUtils.post.request.failed.no.callback", e);
            }
        }
        return request;
    }
}