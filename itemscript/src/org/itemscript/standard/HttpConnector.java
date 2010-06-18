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
 *     * Neither the names of Kalinda Software, DBA Software, Data Base Architects,
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

package org.itemscript.standard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.connectors.ConnectorBase;
import org.itemscript.core.connectors.SyncGetConnector;
import org.itemscript.core.connectors.SyncPostConnector;
import org.itemscript.core.connectors.SyncPutConnector;
import org.itemscript.core.connectors.SyncQueryConnector;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.itemscript.core.util.StaticJsonUtil;
import org.itemscript.core.values.ItemscriptPutResponse;
import org.itemscript.core.values.ItemscriptRemoveResponse;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;
import org.itemscript.core.values.PutResponse;
import org.itemscript.core.values.RemoveResponse;

/**
 * HTTP Connector for the standard-Java configuration.
 * <p>
 * Associated with the <code>http:</code> and <code>https:</code> schemes in the standard-Java configuration.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public final class HttpConnector extends ConnectorBase
        implements
            SyncGetConnector,
            SyncPutConnector,
            SyncPostConnector,
            SyncQueryConnector {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    /**
     * Create a new HttpConnector.
     * 
     * @param system The associated JsonSystem.
     */
    public HttpConnector(JsonSystem system) {
        super(system);
    }

    private JsonObject createMeta(URLConnection connection) {
        JsonObject meta = system().createObject();
        Map<String, List<String>> headers = connection.getHeaderFields();
        for (String key : headers.keySet()) {
            List<String> values = headers.get(key);
            if (values.size() == 1) {
                meta.put(key, values.get(0));
            } else {
                JsonArray headerList = meta.createArray(key);
                for (int i = 0; i < values.size(); ++i) {
                    headerList.add(values.get(i));
                }
            }
        }
        return meta;
    }

    @Override
    public JsonValue get(Url url) {
        try {
            URLConnection connection = new URL(url + "").openConnection();
            // Note: we are ignoring content-encoding for now...
            return createItemFromResponse(url, connection);
        } catch (IOException e) {
            throw ItemscriptError.internalError(this, "get.IOException", e);
        }
    }

    private JsonValue createItemFromResponse(Url url, URLConnection connection) throws IOException {
        String contentType = connection.getContentType();
        if (StaticJsonUtil.looksLikeJson(url, contentType)) {
            return system().createItem(url + "", createMeta(connection),
                    StandardUtil.readJson(system(), new InputStreamReader(connection.getInputStream())))
                    .value();
        } else if (contentType.startsWith("text")) {
            return system().createItem(
                    url + "",
                    createMeta(connection),
                    StandardUtil.readText(system(), new BufferedReader(new InputStreamReader(
                            connection.getInputStream()))))
                    .value();
        } else {
            return system().createItem(url + "", createMeta(connection),
                    StandardUtil.readBinary(system(), connection.getInputStream()))
                    .value();
        }
    }

    @Override
    public PutResponse post(Url url, JsonValue value) {
        try {
            URL javaUrl = new URL(url + "");
            HttpURLConnection connection = (HttpURLConnection) javaUrl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
            connection.connect();
            Writer w = new OutputStreamWriter(connection.getOutputStream());
            w.write(value.toCompactJsonString());
            w.close();
            JsonValue retValue = null;
            if (connection.getContentLength() > 0) {
                retValue = createItemFromResponse(url, connection);
            }
            return new ItemscriptPutResponse(url + "", createMeta(connection), retValue);
        } catch (IOException e) {
            throw ItemscriptError.internalError(this, "post.IOException", e);
        }
    }

    @Override
    public PutResponse put(Url url, JsonValue value) {
        try {
            URL javaUrl = new URL(url + "");
            HttpURLConnection connection = (HttpURLConnection) javaUrl.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
            connection.connect();
            Writer w = new OutputStreamWriter(connection.getOutputStream());
            w.write(value.toCompactJsonString());
            w.close();
            JsonValue retValue = null;
            if (connection.getContentLength() > 0) {
                retValue = createItemFromResponse(url, connection);
            }
            return new ItemscriptPutResponse(url + "", createMeta(connection), retValue);
        } catch (IOException e) {
            throw ItemscriptError.internalError(this, "put.IOException", e);
        }
    }

    @Override
    public JsonValue query(Url url) {
        return get(url);
    }

    @Override
    public RemoveResponse remove(Url url) {
        try {
            URL javaUrl = new URL(url + "");
            HttpURLConnection connection = (HttpURLConnection) javaUrl.openConnection();
            connection.setRequestMethod("DELETE");
            connection.connect();
            int response = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            return new ItemscriptRemoveResponse(createMeta(connection));
        } catch (IOException e) {
            throw ItemscriptError.internalError(this, "remove.IOException", e);
        }
    }
}