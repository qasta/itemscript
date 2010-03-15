
package org.itemscript.core.util;

import org.itemscript.core.Params;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonContainer;
import org.itemscript.core.values.JsonGetAccess;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

/**
 * Contains methods for implementing the {@link JsonGetAccess} interface. 
 * 
 * This class is not intended for general use, and the interface is not promised to be stable; it has to be public because it
 * is used by various other classes in different packages.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 *
 */
public final class JsonAccessHelper {
    public static JsonArray asArray(JsonValue value) {
        if (value == null) { return null; }
        return value.asArray();
    }

    public static byte[] asBinary(JsonValue value) {
        if (value == null) { return null; }
        if (!value.isString()) { return null; }
        return value.binaryValue();
    }

    public static Boolean asBoolean(JsonValue value) {
        if (value == null) { return null; }
        if (!value.isBoolean()) { return null; }
        return value.booleanValue();
    }

    public static Double asDouble(JsonValue value) {
        if (value == null) { return null; }
        if (!value.isNumber()) { return null; }
        return value.doubleValue();
    }

    public static Float asFloat(JsonValue value) {
        if (value == null) { return null; }
        if (!value.isNumber()) { return null; }
        return value.floatValue();
    }

    public static Integer asInt(JsonValue value) {
        if (value == null) { return null; }
        if (!value.isNumber()) { return null; }
        return value.intValue();
    }

    public static Long asLong(JsonValue value) {
        if (value == null) { return null; }
        if (!value.isString()) { return null; }
        return value.longValue();
    }

    public static Object asNative(JsonValue value) {
        if (value == null) { return null; }
        if (!value.isNative()) { return null; }
        return value.nativeValue();
    }

    public static JsonObject asObject(JsonValue value) {
        if (value == null) { return null; }
        return value.asObject();
    }

    public static String asString(JsonValue value) {
        if (value == null) { return null; }
        if (!value.isString()) { return null; }
        return value.stringValue();
    }

    public static JsonValue dereference(JsonValue value) {
        if (value == null) { throw new ItemscriptError(
                "error.itemscript.JsonAccessHelper.dereference.value.was.null"); }
        return value.dereference();
    }

    public static void dereference(JsonValue value, GetCallback callback) {
        if (value == null) { throw new ItemscriptError(
                "error.itemscript.JsonAccessHelper.dereference.value.was.null"); }
        value.dereference(callback);
    }

    public static JsonArray getRequiredArray(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredArray.not.present", key
                + ""); }
        if (value.isArray()) { return value.asArray(); }
        throw ItemscriptError.internalError(container, "getRequiredArray.existed.but.was.not.array",
                keyValueParams(key, value));
    }

    public static byte[] getRequiredBinary(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredByteArray.not.present",
                key + ""); }
        if (value.isString()) { return value.asString()
                .binaryValue(); }
        throw ItemscriptError.internalError(container, "getRequiredByteArray.existed.but.was.not.string",
                keyValueParams(key, value));
    }

    public static Boolean getRequiredBoolean(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredBoolean.not.present", key
                + ""); }
        if (value.isBoolean()) { return value.asBoolean()
                .booleanValue(); }
        throw ItemscriptError.internalError(container, "getRequiredBoolean.existed.but.was.not.boolean",
                keyValueParams(key, value));
    }

    public static Double getRequiredDouble(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredFloat.not.present", key
                + ""); }
        if (value.isNumber()) { return value.asNumber()
                .doubleValue(); }
        throw new ItemscriptError("getRequiredFloat.existed.but.was.not.number", keyValueParams(key, value));
    }

    public static Float getRequiredFloat(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredFloat.not.present", key
                + ""); }
        if (value.isNumber()) { return value.asNumber()
                .floatValue(); }
        throw ItemscriptError.internalError(container, "getRequiredFloat.existed.but.was.not.number",
                keyValueParams(key, value));
    }

    public static Integer getRequiredInt(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredInt.not.present", key + ""); }
        if (value.isNumber()) { return value.asNumber()
                .intValue(); }
        throw ItemscriptError.internalError(container, "getRequiredInt.existed.but.was.not.number",
                keyValueParams(key, value));
    }

    public static Long getRequiredLong(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError("getRequiredLong.not.present", key + ""); }
        if (value.isString()) {
            try {
                return Long.parseLong(value.asString()
                        .stringValue());
            } catch (NumberFormatException e) {
                throw ItemscriptError.internalError(container,
                        "getRequiredLong.existed.but.could.not.parse.as.long", keyValueParams(key, value));
            }
        }
        throw ItemscriptError.internalError(container, "getRequiredLong.existed.but.was.not.string",
                keyValueParams(key, value));
    }

    public static JsonObject getRequiredObject(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredObject.not.present", key
                + ""); }
        if (value.isObject()) { return value.asObject(); }
        throw ItemscriptError.internalError(container, "getRequiredObject.existed.but.was.not.object",
                keyValueParams(key, value));
    }

    public static String getRequiredString(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredString.not.present", key
                + ""); }
        if (value.isString()) { return value.asString()
                .stringValue(); }
        throw ItemscriptError.internalError(container, "getRequiredString.existed.but.was.not.string",
                keyValueParams(key, value));
    }

    public static Params keyValueParams(Object key, JsonValue value) {
        return new Params().p("key", key + "")
                .p("value", value.toCompactJsonString());
    }
}