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

package org.itemscript.core.values;

import java.util.Map;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.connectors.GetCallback;

/**
 * Provides access to contained {@link JsonValue} by string locators, which may be keys in the
 * case of {@link JsonContainer} classes, or URLs in the case of {@link JsonItem} or {@link JsonSystem} classes.
 * 
 * For compatibility with the Map and List interfaces, the simple get, put, and remove methods on this class
 * are named getValue, putValue, and removeValue. Implementing classes should add get, put, and remove methods compatible
 * with Map and List as appropriate for their interface.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 *
 */
public interface JsonAccess {
    /**
     * Dereference a value under the given key.
     * <p>
     * If the value is a JsonString, the string is treated as a URL and retrieved.
     * <p>
     * If the value is a JsonContainer, the value itself is returned.
     * <p>
     * If the value is of any other type, an exception will be thrown.
     * 
     * @param key The key used to find the value to dereference.
     * @return The dereferenced value.
     */
    public JsonValue dereference(String key);

    /**
     * Dereference a value under the given key, asynchronously, calling the supplied callback when it returns.
     * <p>
     * If the value is a JsonString, the string is treated as a URL and retrieved.
     * <p>
     * If the value is a JsonContainer, the value itself is returned.
     * <p>
     * If the value is of any other type, an exception will be thrown.   
     * 
     * @param key The key used to find the value to dereference.
     * @param callback The callback to call when the dereference operation completes.
     */
    public void dereference(String key, GetCallback callback);

    /**
     * Get a JsonArray value. If the element does not exist or is not an JsonArray, returns null.
     * 
     * @param key The key used to find the value.
     * @return The JsonArray, or null if it did not exist or was not a JsonArray.
     */
    public JsonArray getArray(String key);

    /**
     * Get a binary value. If the element does not exist or is not a JsonString, returns null.
     * If the value is a string but cannot be base64-decoded, throws an exception.
     * 
     * @param key The key used to find the value.
     * @return The binary value, or null if it it did not exist or was not a JsonString.
     */
    public byte[] getBinary(String key);

    /**
     * Get a boolean value. If the element does not exist or is not a JsonBoolean, returns null.
     * 
     * @param key The key used to find the value.
     * @return The Boolean value, or null if it did not exist or was not a JsonBoolean.
     */
    public Boolean getBoolean(String key);

    /**
     * Get a double value. If the element does not exist or is not a JsonNumber, returns null.
     * 
     * @param key The key used to find the value.
     * @return The Double value, or null if it did not exist or was not a JsonNumber.
     */
    public Double getDouble(String key);

    /**
     * Get a float value. If the element does not exist or is not a JsonNumber, returns null.
     * 
     * @param key The key used to find the value.
     * @return The Float value, or null if it did not exist or was not a JsonNumber.
     */
    public Float getFloat(String key);

    /**
     * Get an int value. If the element does not exist or is not a JsonNumber, returns null.
     * 
     * @param key The key used to find the value.
     * @return The Integer value, or null if it did not exist or was not a JsonNumber.
     */
    public Integer getInt(String key);

    /**
     * Get a long value. If the element does not exist or is not a JsonString, returns null.
     * 
     * If the value is a string but cannot be parsed as a long, throws an exception.
     * 
     * @param key The key used to find the value.
     * @return The Long value, or null if it did not exist or was not a JsonString.
     */
    public Long getLong(String key);

    /**
     * Get a native value. If the element does not exist or is not a JsonNative, returns null.
     * 
     * @param key The key used to find the value.
     * @return The native object, or null if it did not exist or was not a JsonNative.
     */
    public Object getNative(String key);

    /**
     * Get a JsonObject value. If the element does not exist or is not a JsonObject, returns null.
     * 
     * @param key The key used to find the value.
     * @return The JsonObject value, or null if the value did not exist or was not a JsonObject.
     */
    public JsonObject getObject(String key);

    /**
     * Get a string value. If the value does not exist or is not a JsonString, returns null.
     * 
     * @param key The key used to find the value.
     * @return The String value, or null if the value did not exist or was not a JsonString.
     */
    public String getString(String key);

    /**
     * Get a JsonValue. If the value does not exist, returns null.
     * <p>
     * This method is named "getValue" to avoid conflicts with the List and Map "get" methods.
     * A method named "get" will be provided by any class implementing this interface, and that
     * method should be used instead of this one, for brevity.
     * 
     * @param key The key used to find the value.
     * @return The value, or null if the value did not exist.
     */
    public JsonValue getValue(String key);

    /**
     * Put a boolean value as a JsonBoolean.
     * 
     * @param key The key to put the value under.
     * @param value The boolean value to store.
     * @return The new JsonBoolean.
     */
    public JsonBoolean put(String key, Boolean value);

    /**
     * Put a binary value. The value will be base64-encoded and stored in a JsonString.
     * <p>
     * Note: The JsonString object will point to the underlying byte[] that was supplied; it will
     * not copy it. The internal operations of the JsonSystem will not change it, but it will
     * be made directly available through the JsonString object's {@link JsonString#binaryValue}
     * method. So, if you need to make sure that the original is not changed, you must copy it before
     * supplying it to this method. 
     * 
     * @param key The key to put the value under.
     * @param value The binary value to store.
     * @return The new JsonString.
     */
    public JsonString put(String key, byte[] value);

    /**
     * Put a double value as a JsonNumber.
     * 
     * @param key The key to put the value under.
     * @param value The double value to store.
     * @return The new JsonNumber.
     */
    public JsonNumber put(String key, Double value);

    /**
     * Put a float value as a JsonNumber.
     * 
     * @param key The key to put the value under.
     * @param value The float value to store.
     * @return The new JsonNumber.
     */
    public JsonNumber put(String key, Float value);

    /**
     * Put an int value as a JsonNumber.
     * 
     * @param key The key to put the value under.
     * @param value The int value to store.
     * @return The new JsonNumber.
     */
    public JsonNumber put(String key, Integer value);

    /**
     * Put a long value as a JsonString.
     * 
     * @param key The key to put the value under.
     * @param value The long value to store.
     * @return The new JsonString.
     */
    public JsonString put(String key, Long value);

    /**
     * Put a string value as a JsonString.
     * 
     * @param key The key to put the value under.
     * @param value The String value to store.
     * @return The new JsonString.
     */
    public JsonString put(String key, String value);

    /**
     * Put a native object as a JsonNative.
     * 
     * @param key The key to put the value under.
     * @param value The native object to store.
     * @return The new JsonNative.
     */
    public JsonNative putNative(String key, Object value);

    /**
     * Put a JsonValue.
     * <p>
     * Note that the return from this method is the value that was returned from the put operation, which differs
     * from the {@link Map#put} method which returns any previous value that the map had under that key.<br/>
     * <p>
     * Care should be taken when using the {@link JsonObject} <code>put</code> method which has the <code>Map put</code> behavior; it is not
     * interchangeable with this <code>putValue</code> method because of the different return values; if you don't care about the return
     * value, there is no difference.
     * <p>
     * The <code>put</code> methods on JsonSystem and JsonItem are interchangeable
     * with this method.
     * 
     * @param key The key to put the value under.
     * @param value The JsonValue to store.
     * @return The JsonValue returned by the put operation, which may not be the same value that was given to store.
     */
    public JsonValue putValue(String key, JsonValue value);

    /**
     * Remove a value.
     * <p>
     * This method is named "removeValue" to avoid conflicts with the List and Map "remove" methods.
     * A method named "remove" will be provided by any class implementing this interface, and that
     * method should be used instead of this one, for brevity.
     * 
     * @param key The key indicating the value to remove.
     */
    public void removeValue(String key);
}