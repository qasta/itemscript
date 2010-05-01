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
import org.itemscript.core.connectors.SyncGetConnector;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonValue;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A connector class that allows you to access values in JavaScript objects defined in the host page.
 * <p>
 * Only the path portion of supplied URLs will be used to distinguish between cookies.
 * <p>
 * Note: This is experimental and might change or go away if it proves not to be useful.
 * There are some potential problems with multiple browser windows attempting to access
 * the same cookie that have not been addressed.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public class GwtJavaScriptConnector implements SyncGetConnector, HasSystem {
    private final JsonSystem system;

    /**
     * Create a new GwtCookieConnector.
     * 
     * @param system The associated JsonSystem.
     */
    public GwtJavaScriptConnector(JsonSystem system) {
        this.system = system;
    }

    private native JavaScriptObject get(String name) /*-{
                                                     return { "value" : $wnd[name] };
                                                     }-*/;

    @Override
    public JsonValue get(Url url) {
        JsonValue value = GwtJsonParser.convert(system(), get(url.remainder()));
        if (value == null) { throw ItemscriptError.internalError(this, "get.value.was.null", url + ""); }
        return system().createItem(url + "", value)
                .value();
    }

    @Override
    public JsonSystem system() {
        return system;
    }
}