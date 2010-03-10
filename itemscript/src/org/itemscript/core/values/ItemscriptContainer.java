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

package org.itemscript.core.values;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.Params;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Fragment;
import org.itemscript.core.util.JsonAccessHelper;

/**
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public abstract class ItemscriptContainer extends ItemscriptValue implements JsonContainer {
    protected ItemscriptContainer(JsonSystem system) {
        super(system);
    }

    @Override
    public final JsonContainer asContainer() {
        return this;
    }

    @Override
    public final JsonArray createArray(String key) {
        JsonArray array = system().createArray();
        putValue(key, array);
        return array;
    }

    @Override
    public final JsonObject createObject(String key) {
        JsonObject object = system().createObject();
        putValue(key, object);
        return object;
    }

    @Override
    public JsonValue dereference() {
        return this;
    }

    @Override
    public void dereference(GetCallback callback) {
        callback.onSuccess(this);
    }

    @Override
    public final JsonValue dereference(String key) {
        return JsonAccessHelper.dereference(getValue(key));
    }

    @Override
    public final void dereference(String key, GetCallback callback) {
        JsonAccessHelper.dereference(getValue(key), callback);
    }

    @Override
    public final JsonArray getArray(String key) {
        return JsonAccessHelper.asArray(getValue(key));
    }

    @Override
    public final byte[] getBinary(String key) {
        return JsonAccessHelper.asBinary(getValue(key));
    }

    @Override
    public final Boolean getBoolean(String key) {
        return JsonAccessHelper.asBoolean(getValue(key));
    }

    public JsonValue getByFragment(Fragment fragment) {
        JsonValue next = this;
        for (int i = 0; i < fragment.size(); ++i) {
            String key = fragment.get(i);
            next = getNext(next, key);
        }
        return next;
    }

    @Override
    public final Double getDouble(String key) {
        return JsonAccessHelper.asDouble(getValue(key));
    }

    @Override
    public final Float getFloat(String key) {
        return JsonAccessHelper.asFloat(getValue(key));
    }

    @Override
    public final Integer getInt(String key) {
        return JsonAccessHelper.asInt(getValue(key));
    }

    @Override
    public final Long getLong(String key) {
        return JsonAccessHelper.asLong(getValue(key));
    }

    @Override
    public final Object getNative(String key) {
        return JsonAccessHelper.asNative(getValue(key));
    }

    private JsonValue getNext(JsonValue next, String key) {
        if (next.isContainer()) {
            next = next.asContainer()
                    .getValue(key);
        } else {
            throw new ItemscriptError("error.itemscript.ItemscriptContainer.getNext.next.was.not.a.container",
                    new Params().p("next", next + "")
                            .p("key", key));
        }
        return next;
    }

    @Override
    public final JsonObject getObject(String key) {
        return JsonAccessHelper.asObject(getValue(key));
    }

    @Override
    public final JsonArray getOrCreateArray(String key) {
        if (containsKey(key)) {
            JsonArray array = getArray(key);
            if (array == null) { throw new ItemscriptError(
                    "error.itemscript.ItemscriptContainer.getOrCreateArray.value.existed.but.was.not.an.array",
                    getValue(key) + ""); }
            return array;
        } else {
            return createArray(key);
        }
    }

    @Override
    public final JsonObject getOrCreateObject(String key) {
        if (containsKey(key)) {
            JsonObject object = getObject(key);
            if (object == null) { throw new ItemscriptError(
                    "error.itemscript.ItemscriptContainer.getOrCreateObject.value.existed.but.was.not.an.object",
                    getValue(key) + ""); }
            return object;
        } else {
            return createObject(key);
        }
    }

    @Override
    public final JsonArray getRequiredArray(String key) {
        JsonValue value = getValue(key);
        if (value == null) { throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredArray.not.present", key); }
        if (value.isArray()) { return value.asArray(); }
        throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredArray.existed.but.was.not.array", value + "");
    }

    @Override
    public final Boolean getRequiredBoolean(String key) {
        JsonValue value = getValue(key);
        if (value == null) { throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredBoolean.not.present", key); }
        if (value.isBoolean()) { return value.asBoolean()
                .booleanValue(); }
        throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredBoolean.existed.but.was.not.boolean", value + "");
    }

    @Override
    public final byte[] getRequiredByteArray(String key) {
        JsonValue value = getValue(key);
        if (value == null) { throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredByteArray.not.present", key); }
        if (value.isString()) { return value.asString()
                .binaryValue(); }
        throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredByteArray.existed.but.was.not.string", value + "");
    }

    @Override
    public final Double getRequiredDouble(String key) {
        JsonValue value = getValue(key);
        if (value == null) { throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredFloat.not.present", key); }
        if (value.isNumber()) { return value.asNumber()
                .doubleValue(); }
        throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredFloat.existed.but.was.not.number", value + "");
    }

    @Override
    public final Float getRequiredFloat(String key) {
        JsonValue value = getValue(key);
        if (value == null) { throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredFloat.not.present", key); }
        if (value.isNumber()) { return value.asNumber()
                .floatValue(); }
        throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredFloat.existed.but.was.not.number", value + "");
    }

    @Override
    public final Integer getRequiredInt(String key) {
        JsonValue value = getValue(key);
        if (value == null) { throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredInt.not.present", key); }
        if (value.isNumber()) { return value.asNumber()
                .intValue(); }
        throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredInt.existed.but.was.not.number", value + "");
    }

    @Override
    public final Long getRequiredLong(String key) {
        JsonValue value = getValue(key);
        if (value == null) { throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredLong.not.present", key); }
        if (value.isString()) {
            try {
                return Long.parseLong(value.asString()
                        .stringValue());
            } catch (NumberFormatException e) {
                throw ItemscriptError.internalError(this, "getRequiredLong.existed.but.could.not.parse.as.long",
                        value + "");
            }
        }
        throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredLong.existed.but.was.not.string", value + "");
    }

    @Override
    public final JsonObject getRequiredObject(String key) {
        JsonValue value = getValue(key);
        if (value == null) { throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredObject.not.present", key); }
        if (value.isObject()) { return value.asObject(); }
        throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredObject.existed.but.was.not.object", value + "");
    }

    @Override
    public final String getRequiredString(String key) {
        JsonValue value = getValue(key);
        if (value == null) { throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredString.not.present", key); }
        if (value.isString()) { return value.asString()
                .stringValue(); }
        throw new ItemscriptError(
                "error.itemscript.ItemscriptContainer.getRequiredString.existed.but.was.not.string", value + "");
    }

    @Override
    public final String getString(String key) {
        return JsonAccessHelper.asString(getValue(key));
    }

    @Override
    public final boolean hasArray(String key) {
        JsonValue value = getValue(key);
        if (value == null) { return false; }
        return value.isArray();
    }

    @Override
    public final boolean hasBoolean(String key) {
        JsonValue value = getValue(key);
        if (value == null) { return false; }
        return value.isBoolean();
    }

    @Override
    public final boolean hasNumber(String key) {
        JsonValue value = getValue(key);
        if (value == null) { return false; }
        return value.isNumber();
    }

    @Override
    public final boolean hasObject(String key) {
        JsonValue value = getValue(key);
        if (value == null) { return false; }
        return value.isObject();
    }

    @Override
    public final boolean hasString(String key) {
        JsonValue value = getValue(key);
        if (value == null) { return false; }
        return value.isString();
    }

    @Override
    public final boolean isContainer() {
        return true;
    }

    protected void prepareValueForPut(String key, JsonValue value) {
        if (value.parent() != null) { throw ItemscriptError.internalError(this,
                "prepareValueForPut.value.is.in.another.container"); }
        ((ItemscriptValue) value).setParent(this);
        ((ItemscriptValue) value).setKey(key);
        ((ItemscriptValue) value).setItem(null);
    }

    @Override
    public final JsonBoolean put(String key, Boolean value) {
        JsonBoolean jsonValue = system().createBoolean(value);
        putValue(key, jsonValue);
        return jsonValue;
    }

    @Override
    public final JsonString put(String key, byte[] value) {
        JsonString jsonValue = system().createString(value);
        putValue(key, jsonValue);
        return jsonValue;
    }

    @Override
    public final JsonNumber put(String key, Double value) {
        JsonNumber jsonValue = system().createNumber(value);
        putValue(key, jsonValue);
        return jsonValue;
    }

    @Override
    public final JsonNumber put(String key, Float value) {
        JsonNumber jsonValue = system().createNumber(value);
        putValue(key, jsonValue);
        return jsonValue;
    }

    @Override
    public final JsonNumber put(String key, Integer value) {
        JsonNumber jsonValue = system().createNumber(value);
        putValue(key, jsonValue);
        return jsonValue;
    }

    @Override
    public final JsonString put(String key, Long value) {
        JsonString jsonValue = system().createString(value);
        putValue(key, jsonValue);
        return jsonValue;
    }

    @Override
    public final JsonString put(String key, String value) {
        JsonString jsonValue = system().createString(value);
        putValue(key, jsonValue);
        return jsonValue;
    }

    @Override
    public final JsonNative putNative(String key, Object nativeValue) {
        JsonNative jsonValue = system().createNative(nativeValue);
        putValue(key, jsonValue);
        return jsonValue;
    }

    @Override
    public abstract JsonValue putValue(String key, JsonValue value);

    public abstract String toJsonString(int indent);

    protected final void updateRemovedValue(JsonValue value) {
        if (value == null) { return; }
        ((ItemscriptValue) value).setParent(null);
        ((ItemscriptValue) value).setKey(null);
        ((ItemscriptValue) value).setItem(null);
    }
}