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

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.Params;
import org.itemscript.core.connectors.AsyncGetConnector;
import org.itemscript.core.connectors.AsyncPostConnector;
import org.itemscript.core.connectors.AsyncPutConnector;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.connectors.PutCallback;
import org.itemscript.core.connectors.RemoveCallback;
import org.itemscript.core.events.Event;
import org.itemscript.core.events.EventType;
import org.itemscript.core.events.Handler;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.itemscript.core.util.StaticJsonUtil;
import org.itemscript.core.values.ItemscriptPutResponse;
import org.itemscript.core.values.ItemscriptRemoveResponse;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

/**
 * A connector for retrieving JSON values via HTTP.
 * <p>
 * This connector is associated with the <code>http:</code>, <code>https:</code>, and <code>file:</code> schemes in the GWT configuration.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public class GwtHttpConnector implements AsyncGetConnector, AsyncPutConnector, AsyncPostConnector, HasSystem {
    public static final String POST_ENCAPSULATED_KEY = "postEncapsulated";
    public static final String CONFIG_URL = "mem:/itemscript/GwtHttpConnector#" + POST_ENCAPSULATED_KEY;
    public static final String PUT_METHOD = "PUT";
    private JsonSystem system;
    private boolean postEncapsulated;

    /**
     * Create a new GwtHttpConnector.
     * 
     * @param system The associated JsonSystem.
     */
    public GwtHttpConnector(JsonSystem system) {
        this.system = system;
        JsonValue postEncapsulatedVal = system.get(CONFIG_URL);
        if (postEncapsulatedVal == null || !postEncapsulatedVal.isBoolean()) {
            postEncapsulated = true;
            postEncapsulatedVal = system.put(CONFIG_URL, postEncapsulated)
                    .value();
        } else {
            postEncapsulated = postEncapsulatedVal.booleanValue();
        }
        // Now attach an event handler in case someone changes it...
        postEncapsulatedVal.item()
                .addHandler(new Handler() {
                    @Override
                    public void handle(Event event) {
                        // If it's removed that's an error.
                        if (event.eventType()
                                .equals(EventType.REMOVE)) {
                            throw ItemscriptError.internalError(this, "handle.postEncapsulated.removed");
                        } else {
                            Boolean value = event.value()
                                    .asObject()
                                    .getBoolean(POST_ENCAPSULATED_KEY);
                            if (value == null) { throw ItemscriptError.internalError(this,
                                    "handle.postEncapsulated.changed.to.non.boolean.value", value + ""); }
                            postEncapsulated = value;
                        }
                    }
                });
    }

    private JsonObject createMeta(Response response) {
        JsonObject meta = system().createObject();
        // responses with a status code of 0 don't have headers.
        if (!(response.getStatusCode() == 0)) {
            Header[] headers = response.getHeaders();
            for (int i = 0; i < headers.length; ++i) {
                Header header = headers[i];
                if (header != null) {
                    meta.put(header.getName(), headers[i].getValue());
                }
            }
        }
        return meta;
    }

    @Override
    public void get(final Url url, final GetCallback callback) {
        RequestUtils.sendGetRequest(url + "", new RequestCallback() {
            @Override
            public void onError(Request request, Throwable e) {
                callback.onError(e);
            }

            @Override
            public void onResponseReceived(Request request, Response response) {
                String statusCode = response.getStatusCode() + "";
                // Currently only handles 2xx status codes... 3xx and so on are treated as errors.
                // Status code "0" is returned when retrieving "file:" URLs
                String contentType = response.getHeader("Content-Type");
                // Default to content type of HTML.
                if (contentType == null) {
                    contentType = "text/html";
                }
                if ((url.scheme()
                        .equals("file") && statusCode.equals("0")) || statusCode.startsWith("2")) {
                    // If it looks like JSON, parse it as JSON.
                    JsonValue value;
                    if (StaticJsonUtil.looksLikeJson(url, contentType)) {
                        try {
                            value = system().parse(response.getText());
                        } catch (ItemscriptError e) {
                            callback.onError(e);
                            return;
                        }
                    } else {
                        // Otherwise return it as text.
                        value = system().createString(response.getText());
                    }
                    callback.onSuccess(system().createItem(url + "", createMeta(response), value)
                            .value());
                } else {
                    callback.onError(ItemscriptError.internalError(this, "get.returned.non.2xx.status",
                            new Params().p("url", url + "")
                                    .p("status", statusCode)
                                    .p("text", response.getStatusText())));
                }
            }
        });
    }

    @Override
    public void post(final Url url, JsonValue value, final PutCallback callback) {
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void onError(Request request, Throwable exception) {
                callback.onError(exception);
            }

            @Override
            public void onResponseReceived(Request request, Response response) {
                String statusCode = response.getStatusCode() + "";
                // Treat any 2xx or 3xx response code as successful... we should really treat certain 3xx
                // status codes as indicating the real location of the resource but this will do for now.
                if (statusCode.startsWith("2") || statusCode.startsWith("3")) {
                    try {
                        callback.onSuccess(new ItemscriptPutResponse(url + "", createMeta(response),
                                system().createItem(url + "", system().parse(response.getText()))
                                        .value()));
                    } catch (ItemscriptError e) {
                        callback.onError(e);
                        return;
                    }
                } else {
                    callback.onError(ItemscriptError.internalError(this, "post.returned.non.2xx.or.3xx.status",
                            new Params().p("url", url + "")
                                    .p("status", statusCode)
                                    .p("text", response.getStatusText())));
                }
            }
        };
        if (!postEncapsulated) {
            RequestUtils.sendJsonPostRequest(url + "", value, requestCallback);
        } else {
            RequestUtils.sendPostEncapsulatedRequest(system, url + "", "POST", value, requestCallback);
        }
    }

    @Override
    public void put(final Url url, JsonValue value, final PutCallback callback) {
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void onError(Request request, Throwable exception) {
                callback.onError(exception);
            }

            @Override
            public void onResponseReceived(Request request, Response response) {
                String statusCode = response.getStatusCode() + "";
                // Currently any response to a put that isn't a 2xx is considered an error.
                if (statusCode.startsWith("2")) {
                    try {
                        callback.onSuccess(new ItemscriptPutResponse(url + "", createMeta(response),
                                system().createItem(url + "", system().parse(response.getText()))
                                        .value()));
                    } catch (ItemscriptError e) {
                        callback.onError(e);
                        return;
                    }
                } else {
                    callback.onError(ItemscriptError.internalError(this, "put.returned.non.2xx.status",
                            new Params().p("url", url + "")
                                    .p("status", statusCode)
                                    .p("text", response.getStatusText())));
                }
            }
        };
        if (!postEncapsulated) {
            RequestUtils.sendJsonPutRequest(url + "", value, requestCallback);
        } else {
            RequestUtils.sendPostEncapsulatedRequest(system, url + "", PUT_METHOD, value, requestCallback);
        }
    }

    @Override
    public void remove(final Url url, final RemoveCallback callback) {
        RequestCallback requestCallback = new RequestCallback() {
            @Override
            public void onError(Request request, Throwable exception) {
                callback.onError(exception);
            }

            @Override
            public void onResponseReceived(Request request, Response response) {
                String statusCode = response.getStatusCode() + "";
                // Currently any response to a delete that isn't a 2xx is considered an error.
                if (statusCode.startsWith("2")) {
                    callback.onSuccess(new ItemscriptRemoveResponse(createMeta(response)));
                } else {
                    callback.onError(ItemscriptError.internalError(this, "put.returned.non.2xx.status",
                            new Params().p("url", url + "")
                                    .p("status", response.getStatusCode() + "")
                                    .p("text", response.getStatusText())));
                }
            }
        };
        if (!postEncapsulated) {
            RequestUtils.sendDeleteRequest(url + "", requestCallback);
        } else {
            RequestUtils.sendPostEncapsulatedRequest(system, url + "", "DELETE", null, requestCallback);
        }
    }

    @Override
    public JsonSystem system() {
        return system;
    }
}