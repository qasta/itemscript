/*
 * Copyright � 2010, Data Base Architects, Inc. All rights reserved.
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

package org.itemscript.core;

import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.connectors.PutCallback;
import org.itemscript.core.connectors.RemoveCallback;
import org.itemscript.core.values.JsonCreator;
import org.itemscript.core.values.JsonGetAccess;
import org.itemscript.core.values.JsonPutAccess;
import org.itemscript.core.values.JsonValue;
import org.itemscript.core.values.PutResponse;
import org.itemscript.core.values.RemoveResponse;

/**
 * A JsonSystem provides access to facilities for parsing, storing, and manipulating JSON values.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 *
 */
public interface JsonSystem extends JsonGetAccess, JsonPutAccess, HasSystem, JsonCreator {
    /**
     * The root URL for the system (<code>"mem:/"</code>).
     */
    public static final String ROOT_URL = "mem:/";

    /**
     * Copy a value from one URL to another.
     * 
     * @param fromUrl The URL to copy from.
     * @param toUrl The URL to copy to.
     */
    public void copy(String fromUrl, String toUrl);

    /**
     * Get the {@link JsonUtil} object for this system. The JsonUtil object contains various implementation
     * methods that may be useful but aren't really part of the core system interface.
     * 
     * @return The associated JsonUtil.
     */
    public JsonUtil util();

    /**
     * Get a value from the given URL.
     * <p>
     * If the URL is relative, it will be interpreted as relative to the URL "mem:/".
     * 
     * @param url The URL to get the value from.
     * @return The value returned from the URL.
     */
    public JsonValue get(String url);

    /**
      * Get a value from the given URL, asynchronously.
      * <p>
      * If the URL is relative, it will be interpreted as relative to the URL "mem:/".
      * <p>
      * Calls the given callback when the value has been retrieved.
      * 
      * @param url The URL to get the value from.
      * @param callback Called when the value is returned.
      */
    public void get(String url, GetCallback callback);

    /**
     * Parse the contents of the given string as JSON.
     * 
     * @param json The string containing JSON to be parsed.
     * @return The JsonValue corresponding to the string.
     */
    public JsonValue parse(String json);

    /**
     * Parse the contents of the given Reader as JSON.
     * <p>
     * The argument is specified as Object only because GWT does not contain Reader.
     * <p>
     * Note: this method is inoperative in the GWT environment.
     * 
     * @param reader The Reader to parse.
     * @return The JsonValue corresponding to the contents of the Reader.
     */
    public JsonValue parseReader(Object reader);

    /**
     * Put a value at the given URL.
     * 
     * @param url The URL where the value is to be put.
     * @param value The value to put.
     * @return The PutResponse returned by the put operation.
     */
    public PutResponse put(String url, JsonValue value);

    /**
     * Put a value at the given URL, calling the given callback when the operation is complete.
     * 
     * @param url The URL where the value is to be put.
     * @param value The value to put.
     * @param callback Called when the put operation is complete.
     */
    public void put(String url, JsonValue value, PutCallback callback);

    /**
     * Remove the value at the given URL.
     * 
     * @param url The URL of the value to remove.
     * @return The RemoveResponse returned by the remove operation.
     */
    public RemoveResponse remove(String url);

    /**
     * Remove the value at the given URL, calling the given callback when the operation is complete.
     * 
     * @param url The URL of the value to remove.
     * @param callback Called when the remove operation has completed.
     */
    public void remove(String url, RemoveCallback callback);
}