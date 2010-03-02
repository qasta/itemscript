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

import org.itemscript.core.JsonSystem;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;

abstract class ItemscriptValue implements JsonValue {
    protected static String indent(int indent) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < indent; ++i) {
            sb.append("    ");
        }
        return sb.toString();
    }

    private final JsonSystem system;
    private String key = null;
    private JsonContainer parent;
    private JsonItem item;

    protected ItemscriptValue(JsonSystem system) {
        this.system = system;
    }

    @Override
    public JsonValue dereference() {
        throw ItemscriptError.internalError(this, "dereference.value.was.not.JsonString.or.JsonContainer");
    }

    @Override
    public void dereference(GetCallback callback) {
        callback.onError(ItemscriptError.internalError(this,
                "dereference.value.was.not.JsonString.or.JsonContainer"));
    }

    @Override
    public JsonArray asArray() {
        return null;
    }

    @Override
    public JsonBoolean asBoolean() {
        return null;
    }

    @Override
    public JsonContainer asContainer() {
        return null;
    }

    @Override
    public JsonNative asNative() {
        return null;
    }

    @Override
    public JsonNull asNull() {
        return null;
    }

    @Override
    public JsonNumber asNumber() {
        return null;
    }

    @Override
    public JsonObject asObject() {
        return null;
    }

    @Override
    public JsonString asString() {
        return null;
    }

    @Override
    public byte[] binaryValue() {
        throw new UnsupportedOperationException("binaryValue() called on a value that was not a JsonString");
    }

    @Override
    public Boolean booleanValue() {
        throw new UnsupportedOperationException("booleanValue() called on a value that was not a JsonBoolean");
    }

    @Override
    public Double doubleValue() {
        throw new UnsupportedOperationException("doubleValue() called on a value that was not a JsonNumber");
    }

    @Override
    public Float floatValue() {
        throw new UnsupportedOperationException("floatValue() called on a value that was not a JsonNumber");
    }

    @Override
    public final String fragment() {
        if (parent() == null) {
            return "#";
        } else {
            if (parent().parent() == null) {
                return "#" + Url.encode(key());
            } else {
                return parent().fragment() + "." + Url.encode(key());
            }
        }
    }

    @Override
    public Integer intValue() {
        throw new UnsupportedOperationException("intValue() called on a value that was not a JsonNumber");
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public boolean isNative() {
        return false;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public final JsonItem item() {
        if (parent() == null) {
            return item;
        } else {
            return parent().item();
        }
    }

    @Override
    public final String key() {
        return key;
    }

    @Override
    public Long longValue() {
        throw new UnsupportedOperationException("longValue() called on a value that was not a JsonString");
    }

    @Override
    public Object nativeValue() {
        throw new UnsupportedOperationException("nativeObject() called on a value that was not a JsonNative");
    }

    @Override
    public final JsonContainer parent() {
        return parent;
    }

    protected final void setItem(JsonItem item) {
        // It's okay to set item to null if it's not null, but not to any other value.
        if (item != null && item() != null) {
            // Should not occur, but just in case...
            throw ItemscriptError.internalError(this, "setItem.item.was.already.set");
        }
        this.item = item;
    }

    protected final void setKey(String key) {
        // It's okay to set key to null if it's not null, but not to any other value.
        if (key != null && key() != null) {
            // Should not occur, but just in case...
            throw ItemscriptError.internalError(this, "setKey.key.was.already.set");
        }
        this.key = key;
    }

    protected final void setParent(JsonContainer parent) {
        // It's okay to set parent to null if it's not null, but not to any other value.
        if (parent != null && parent() != null) {
            // Should not occur, but just in case...
            throw ItemscriptError.internalError(this, "setParent.parent.was.already.set");
        }
        this.parent = parent;
    }

    @Override
    public String stringValue() {
        throw new UnsupportedOperationException("stringValue() called on a value that was not a JsonString");
    }

    @Override
    public final JsonSystem system() {
        return system;
    }

    @Override
    public final String toString() {
        return toJsonString();
    }
}