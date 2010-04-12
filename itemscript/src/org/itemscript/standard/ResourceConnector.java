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

package org.itemscript.standard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.connectors.ConnectorBase;
import org.itemscript.core.connectors.SyncGetConnector;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonValue;

/**
 * Resource Connector for the standard-Java environment.
 * <p>
 * Associated with the <code>classpath:</code> scheme in the standard-Java configuration.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public final class ResourceConnector extends ConnectorBase implements SyncGetConnector {
    /**
     * Create a new ResourceConnector.
     * 
     * @param system The associated JsonSystem.
     */
    public ResourceConnector(JsonSystem system) {
        super(system);
    }

    @Override
    public JsonValue get(Url url) {
        return getResource(url);
    }

    private JsonValue getResource(Url url) {
        try {
            Url noFragmentUrl = url.withoutFragment();
            URL resourceUrl =
                    new URL(null, noFragmentUrl + "", new ResourceHandler(ClassLoader.getSystemClassLoader()));
            URLConnection connection = resourceUrl.openConnection();
            connection.connect();
            String filename;
            String noFragmentUrlString = noFragmentUrl + "";
            int lastSlash = noFragmentUrlString.lastIndexOf('/');
            if (lastSlash == -1) {
                filename = noFragmentUrl.remainder();
            } else {
                filename = noFragmentUrlString.substring(lastSlash + 1);
            }
            String contentType = URLConnection.guessContentTypeFromName(filename);
            if (contentType == null) {
                if (filename.endsWith(".json")) {
                    contentType = "application/json";
                } else {
                    contentType = "application/octet-stream";
                }
            }
            if (contentType.equals("application/json")) {
                return system().createItem(url + "",
                        StandardUtil.readJson(system(), new InputStreamReader(connection.getInputStream())))
                        .value();
            } else if (contentType.startsWith("text")) {
                return system().createItem(
                        url + "",
                        StandardUtil.readText(system(), new BufferedReader(new InputStreamReader(
                                connection.getInputStream()))))
                        .value();
            } else {
                return system().createItem(url + "",
                        StandardUtil.readBinary(system(), connection.getInputStream()))
                        .value();
            }
        } catch (IOException e) {
            throw ItemscriptError.internalError(this, "getResource.IOException", e);
        }
    }
}