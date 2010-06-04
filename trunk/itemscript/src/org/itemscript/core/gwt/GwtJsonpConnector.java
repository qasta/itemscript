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

package org.itemscript.core.gwt;

import org.itemscript.core.HasSystem;
import org.itemscript.core.JsonSystem;
import org.itemscript.core.connectors.AsyncGetConnector;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A connector for retrieving values via JSONP in GWT.
 * <p>
 * JSONP is a method for retrieving values from an HTTP server using an HTML SCRIPT tag to circumvent the Same Origin Policy
 * that applies to ordinary HTTP requests. This connector uses a particular URL format described here:
 * <a href="http://code.google.com/p/itemscript/wiki/JsonpUrlsInGwt">JSONP URLs in GWT</a>.
 * <p>
 * Note that retrieval of JSONP values necessarily involves complete trust of the site supplying them; you should be careful
 * not to allow third-party data in your application to be used as a complete URL.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public class GwtJsonpConnector implements AsyncGetConnector, HasSystem {
    private final JsonSystem system;

    /**
     * Create a new GwtJsonpConnector.
     * 
     * @param system The associated JsonSystem.
     */
    public GwtJsonpConnector(JsonSystem system) {
        this.system = system;
    }

    @Override
    public void get(final Url url, final GetCallback callback) {
        // Split the URL.
        String remainder = url.remainder();
        int firstSemiColon = remainder.indexOf(';');
        int firstComma = remainder.indexOf(',');
        if (firstComma == -1) { throw ItemscriptError.internalError(this, "get.invalid.url", url + ""); }
        String callbackParam;
        String errorCallbackParam = null;
        // Is there a semicolon in the first section?
        if (firstSemiColon != -1 && firstSemiColon < firstComma) {
            callbackParam = remainder.substring(0, firstSemiColon);
            errorCallbackParam = remainder.substring(firstSemiColon + 1, firstComma);
        } else {
            callbackParam = remainder.substring(0, firstComma);
        }
        final String jsonpUrl = remainder.substring(firstComma + 1);
        JsonpRequestBuilder builder = new JsonpRequestBuilder();
        builder.setCallbackParam(callbackParam);
        if (errorCallbackParam != null) {
            builder.setFailureCallbackParam(errorCallbackParam);
        }
        builder.requestObject(jsonpUrl, new AsyncCallback<JavaScriptObject>() {
            @Override
            public void onFailure(Throwable e) {
                callback.onError(e);
            }

            public void onSuccess(JavaScriptObject result) {
                callback.onSuccess(system().createItem(url + "", system.createObject(),
                        GwtJsonParser.convertObject(system, result))
                        .value());
            }
        });
    }

    @Override
    public JsonSystem system() {
        return system;
    }
}
