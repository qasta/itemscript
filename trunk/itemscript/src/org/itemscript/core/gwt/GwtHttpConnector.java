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
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.ItemscriptPutResponse;
import org.itemscript.core.values.ItemscriptRemoveResponse;
import org.itemscript.core.values.JsonValue;

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
    private JsonSystem system;

    /**
     * Create a new GwtHttpConnector.
     * 
     * @param system The associated JsonSystem.
     */
    public GwtHttpConnector(JsonSystem system) {
        this.system = system;
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
                if (response.getStatusCode() == 200) {
                    callback.onSuccess(system().createItem(url + "", system().parse(response.getText()))
                            .value());
                } else {
                    callback.onError(ItemscriptError.internalError(this, "get.returned.non.200.status",
                            new Params().p("status", response.getStatusCode() + "")
                                    .p("text", response.getStatusText())));
                }
            }
        });
    }

    @Override
    public void post(final Url url, JsonValue value, final PutCallback callback) {
        RequestUtils.sendJsonPostRequest(url + "", value, new RequestCallback() {
            @Override
            public void onError(Request request, Throwable exception) {
                callback.onError(exception);
            }

            @Override
            public void onResponseReceived(Request request, Response response) {
                callback.onSuccess(new ItemscriptPutResponse(url + "", null, system().createItem(url + "",
                        system().parse(response.getText()))
                        .value()));
            }
        });
    }

    @Override
    public void put(final Url url, JsonValue value, final PutCallback callback) {
        RequestUtils.sendJsonPutRequest(url + "", value, new RequestCallback() {
            @Override
            public void onError(Request request, Throwable exception) {
                callback.onError(exception);
            }

            @Override
            public void onResponseReceived(Request request, Response response) {
                callback.onSuccess(new ItemscriptPutResponse(url + "", null, system().createItem(url + "",
                        system().parse(response.getText()))
                        .value()));
            }
        });
    }

    @Override
    public void remove(Url url, final RemoveCallback callback) {
        RequestUtils.sendDeleteRequest(url + "", new RequestCallback() {
            @Override
            public void onError(Request request, Throwable exception) {
                callback.onError(exception);
            }

            @Override
            public void onResponseReceived(Request request, Response response) {
                callback.onSuccess(new ItemscriptRemoveResponse(null));
            }
        });
    }

    @Override
    public JsonSystem system() {
        return system;
    }
}