
package org.itemscript.core.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonBoolean;
import org.itemscript.core.values.JsonContainer;
import org.itemscript.core.values.JsonItem;
import org.itemscript.core.values.JsonNative;
import org.itemscript.core.values.JsonNull;
import org.itemscript.core.values.JsonNumber;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

/**
 * A pseudo-JsonObject that wraps a list of other JsonObjects. When a value is requested from this
 * ChainObject, it is looked for in each of the objects inside this one in turn; the first object that
 * has a value under that key causes that value to be returned. If none of the objects contains a value
 * under that key, {@link #getValue(String)} returns null.
 * <p>
 * All of the JsonObject methods that change the state of the object are not supported; objects of this class
 * also cannot be placed in JsonContainers. Some methods that do not change state are not supported either,
 * although they could be at some point if they were needed. 
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 */
public class ChainObject implements JsonObject {
    private final JsonSystem system;
    private final List<JsonObject> objects;

    /**
     * Create a new ChainObject from the given list of JsonObjects.
     * 
     * @param system The associated JsonSystem.
     * @param objects The list of objects to search.
     */
    public ChainObject(JsonSystem system, List<JsonObject> objects) {
        this.system = system;
        this.objects = objects;
        for (int i = 0; i < objects.size(); ++i) {
            JsonObject object = objects.get(i);
            if (object == this) { throw ItemscriptError.internalError(this,
                    "constructor.objects.list.contained.this.object", objects + ""); }
        }
    }

    @Override
    public JsonObject p(String key, Boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonObject p(String key, Double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonObject p(String key, Integer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonObject p(String key, JsonValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonObject p(String key, Long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonObject p(String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(String key) {
        return getValue(key) != null;
    }

    @Override
    public JsonArray createArray(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonObject createObject(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonArray getOrCreateArray(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonObject getOrCreateObject(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final JsonArray getRequiredArray(String key) {
        return JsonAccessHelper.getRequiredArray(this, key, getValue(key));
    }

    @Override
    public final Boolean getRequiredBoolean(String key) {
        return JsonAccessHelper.getRequiredBoolean(this, key, getValue(key));
    }

    @Override
    public final byte[] getRequiredBinary(String key) {
        return JsonAccessHelper.getRequiredBinary(this, key, getValue(key));
    }

    @Override
    public final Double getRequiredDouble(String key) {
        return JsonAccessHelper.getRequiredDouble(this, key, getValue(key));
    }

    @Override
    public final Float getRequiredFloat(String key) {
        return JsonAccessHelper.getRequiredFloat(this, key, getValue(key));
    }

    @Override
    public final Integer getRequiredInt(String key) {
        return JsonAccessHelper.getRequiredInt(this, key, getValue(key));
    }

    @Override
    public final Long getRequiredLong(String key) {
        return JsonAccessHelper.getRequiredLong(this, key, getValue(key));
    }

    @Override
    public final JsonObject getRequiredObject(String key) {
        return JsonAccessHelper.getRequiredObject(this, key, getValue(key));
    }

    @Override
    public final String getRequiredString(String key) {
        return JsonAccessHelper.getRequiredString(this, key, getValue(key));
    }

    @Override
    public final String getString(String key) {
        return JsonAccessHelper.asString(getValue(key));
    }

    @Override
    public JsonValue getValue(String key) {
        for (int i = 0; i < objects.size(); ++i) {
            JsonValue value = objects.get(i)
                    .get(key);
            if (value != null) { return value; }
        }
        return null;
    }

    @Override
    public boolean hasArray(String key) {
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
    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeValue(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
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
        return this;
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
        return this;
    }

    @Override
    public JsonString asString() {
        return null;
    }

    @Override
    public byte[] binaryValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean booleanValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonValue copy() {
        throw new UnsupportedOperationException();
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
    public Double doubleValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Float floatValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String fragment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer intValue() {
        throw new UnsupportedOperationException();
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
        return true;
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
        return true;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public JsonItem item() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String key() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long longValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object nativeValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonContainer parent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String stringValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonSystem system() {
        return system;
    }

    @Override
    public String toCompactJsonString() {
        return toString();
    }

    @Override
    public String toJsonString() {
        throw new UnsupportedOperationException();
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

    @Override
    public final JsonObject getObject(String key) {
        return JsonAccessHelper.asObject(getValue(key));
    }

    @Override
    public JsonBoolean put(String key, Boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonString put(String key, byte[] value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNumber put(String key, Double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNumber put(String key, Float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNumber put(String key, Integer value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonString put(String key, Long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonString put(String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNative putNative(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonValue putValue(String key, JsonValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String)) { return false; }
        return getValue((String) key) != null;
    }

    @Override
    public boolean containsValue(Object arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<java.util.Map.Entry<String, JsonValue>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonValue get(Object key) {
        if (!(key instanceof String)) { return null; }
        return getValue((String) key);
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonValue put(String arg0, JsonValue arg1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends JsonValue> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonValue remove(Object arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<JsonValue> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonObject p(String key, Float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[ChainObject objects=");
        for (int i = 0; i < objects.size(); ++i) {
            if (i > 0) {
                sb.append(",");
            }
            JsonObject jsonObject = objects.get(i);
            if (jsonObject == null) {
                sb.append("null");
            } else {
                sb.append(jsonObject.toCompactJsonString());
            }
        }
        sb.append("]");
        return sb.toString();
    }
}