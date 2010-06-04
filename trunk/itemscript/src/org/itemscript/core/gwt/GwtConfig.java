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
import org.itemscript.core.config.JsonConfig;

/**
 * A JsonConfig implementation for the GWT environment that gives access to connectors for HTTP, cookies, and javascript.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 *
 */
public class GwtConfig extends MinimalGwtConfig implements JsonConfig {
    private static final String ITEMSCRIPT_CONNECTORS_PREFIX = "/itemscript/connectors#";

    @Override
    public void seedSystem(JsonSystem system) {
        GwtHttpConnector jsonHttpConnector = new GwtHttpConnector(system);
        system.putNative(ITEMSCRIPT_CONNECTORS_PREFIX + "http", jsonHttpConnector);
        system.putNative(ITEMSCRIPT_CONNECTORS_PREFIX + "https", jsonHttpConnector);
        // The file: scheme is also assigned to the HTTP connector because file: URLs are also resolved through the web browser's facilities...
        system.putNative(ITEMSCRIPT_CONNECTORS_PREFIX + "file", jsonHttpConnector);
        system.putNative(ITEMSCRIPT_CONNECTORS_PREFIX + "cookie", new GwtCookieConnector(system));
        system.putNative(ITEMSCRIPT_CONNECTORS_PREFIX + "javascript", new GwtJavaScriptConnector(system));
        system.putNative(ITEMSCRIPT_CONNECTORS_PREFIX + "jsonp", new GwtJsonpConnector(system));
    }
}