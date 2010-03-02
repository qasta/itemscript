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

package org.itemscript.core;

import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.connectors.PutCallback;
import org.itemscript.core.connectors.RemoveCallback;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonAccess;
import org.itemscript.core.values.JsonFactory;
import org.itemscript.core.values.JsonValue;

/**
 * A JsonSystem provides access to facilities for parsing, storing, and manipulating JSON values.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 *
 */
public interface JsonSystem extends JsonAccess, JsonFactory {
    /**
     * The root URL for the system (<code>"mem:/"</code>).
     */
    public static final String ROOT_URL_STRING = "mem:/";
    /**
     * The root URL for the system (<code>"mem:/"</code>).
     */
    public static final Url ROOT_URL = Url.create(ROOT_URL_STRING);

    /**
     * Copy a value from one URL to another.
     * 
     * @param fromUrl The URL to copy from.
     * @param toUrl The URL to copy to.
     */
    public void copy(String fromUrl, String toUrl);

    /**
     * Copy a value from one URL to another.
     * 
     * @param fromUrl The URL to copy from.
     * @param toUrl The URL to copy to.
     */
    public void copy(Url fromUrl, Url toUrl);

    /**
     * Generate a random UUID string.
     * 
     * @return The new UUID.
     */
    public String generateUuid();

    /**
     * Get a value from the given URL.
     * 
     * If the URL is relative, it will be interpreted as relative to the URL "mem:/".
     * 
     * @param url The URL to get the value from.
     * @return The value returned from the URL.
     */
    public JsonValue get(String url);

    /**
      * Get a value from the given URL, asynchronously.
      * 
      * If the URL is relative, it will be interpreted as relative to the URL "mem:/".
      * 
      * Calls the given callback when the value has been retrieved.
      * 
      * @param url The URL to get the value from.
      * @param callback Called when the value is returned.
      */
    public void get(String url, GetCallback callback);

    /**
     * Get a value from the given URL.
     * 
     * If the URL is relative, it will be interpreted as relative to the URL "mem:/".
     * 
     * @param url The URL to get the value from.
     * @return The value returned from the URL.
     */
    public JsonValue get(Url url);

    /**
     * Get a value from the given URL, asynchronously.
     * 
     * If the URL is relative, it will be interpreted as relative to the URL "mem:/".
     * 
     * Calls the given callback when the value has been retrieved.
     * 
     * @param url The URL to get the value from.
     * @param callback Called when the value is returned.
     */
    public void get(Url url, GetCallback callback);

    /**
     * Parse the contents of the given string as JSON.
     * 
     * @param json The string containing JSON to be parsed.
     * @return The JsonValue corresponding to the string.
     */
    public JsonValue parse(String json);

    /**
     * Parse the contents of the given Reader as JSON.
     * 
     * The argument is specified as Object only because GWT does not contain Reader.
     * 
     * This method is inoperative in the GWT environment.
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
     * @return The value returned by the put, which may not be the same as the value that was put.
     */
    public JsonValue put(String url, JsonValue value);

    /**
     * Put a value at the given URL, calling the given callback when the operation is complete.
     * 
     * @param url The URL where the value is to be put.
     * @param value The value to put.
     * @param callback Called when the put operation is complete.
     */
    public void put(String url, JsonValue value, PutCallback callback);

    /**
     * Put a value at the given URL.
     * 
     * @param url The URL where the value is to be put.
     * @param value The value to put.
     * @return The value returned by the put, which may not be the same as the value that was put.
     */
    public JsonValue put(Url url, JsonValue value);

    /**
     * Put a value at the given URL, calling the given callback when the operation is complete.
     * 
     * @param url The URL where the value is to be put.
     * @param value The value to put.
     * @param callback Called when the put operation is complete.
     */
    public void put(Url url, JsonValue value, PutCallback callback);

    /**
     * Remove the value at the given URL.
     * 
     * @param url The URL of the value to remove.
     */
    public void remove(String url);

    /**
     * Remove the value at the given URL, calling the given callback when the operation is complete.
     * 
     * @param url The URL of the value to remove.
     * @param callback Called when the remove operation has completed.
     */
    public void remove(String url, RemoveCallback callback);

    /**
     * Remove the value at the given URL.
     * 
     * @param url The URL of the value to remove.
     */
    public void remove(Url url);

    /**
     * Remove the value at the given URL, calling the given callback when the operation is complete.
     * 
     * @param url The URL of the value to remove.
     * @param callback Called when the remove operation has completed.
     */
    public void remove(Url url, RemoveCallback callback);
}