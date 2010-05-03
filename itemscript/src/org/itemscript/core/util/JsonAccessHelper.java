
package org.itemscript.core.util;

import org.itemscript.core.Params;
import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.url.Url;
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

    public static JsonValue getByPath(final JsonContainer container, String path) {
        if (path == null || path.length() == 0) { throw ItemscriptError.internalError(container,
                "getByPath.path.was.empty"); }
        String[] pathComponents = path.split("/");
        JsonValue next = container;
        for (int i = 0; i < pathComponents.length; ++i) {
            String key = Url.decode(pathComponents[i]);
            if (next == null) { return null; }
            if (next.isContainer()) {
                next = next.asContainer()
                        .getValue(key);
            } else {
                throw ItemscriptError.internalError(container, "getByPath.next.was.not.a.container",
                        new Params().p("next", next + "")
                                .p("path", path)
                                .p("key", key));
            }
        }
        return next;
    }

    public static void putByPath(final JsonContainer container, String path, JsonValue value) {
        String[] pathComponents = path.split("/");
        JsonValue next = container;
        for (int i = 0; i < (pathComponents.length - 1); ++i) {
            String key = pathComponents[i];
            if (!next.isContainer()) { throw ItemscriptError.internalError(next,
                    "putByPath.next.was.not.a.container", new Params().p("next", next + "")
                            .p("path", path)
                            .p("key", key)); }
            JsonContainer currentContainer = next.asContainer();
            next = currentContainer.getValue(key);
            if (next == null) {
                next = currentContainer.createObject(key);
            }
        }
        JsonContainer lastContainer = next.asContainer();
        // Then put the value in that container.
        lastContainer.putValue(pathComponents[pathComponents.length - 1], value);
    }

    public static JsonArray getRequiredArray(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredArray.not.present",
                keyValueParams(container, key, value)); }
        if (value.isArray()) { return value.asArray(); }
        throw ItemscriptError.internalError(container, "getRequiredArray.existed.but.was.not.array",
                keyValueParams(container, key, value));
    }

    public static byte[] getRequiredBinary(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredByteArray.not.present",
                keyValueParams(container, key, value)); }
        if (value.isString()) { return value.asString()
                .binaryValue(); }
        throw ItemscriptError.internalError(container, "getRequiredByteArray.existed.but.was.not.string",
                keyValueParams(container, key, value));
    }

    public static Boolean getRequiredBoolean(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredBoolean.not.present",
                keyValueParams(container, key, value)); }
        if (value.isBoolean()) { return value.asBoolean()
                .booleanValue(); }
        throw ItemscriptError.internalError(container, "getRequiredBoolean.existed.but.was.not.boolean",
                keyValueParams(container, key, value));
    }

    public static Double getRequiredDouble(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredFloat.not.present",
                keyValueParams(container, key, value)); }
        if (value.isNumber()) { return value.asNumber()
                .doubleValue(); }
        throw new ItemscriptError("getRequiredFloat.existed.but.was.not.number", keyValueParams(container, key,
                value));
    }

    public static Float getRequiredFloat(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredFloat.not.present",
                keyValueParams(container, key, value)); }
        if (value.isNumber()) { return value.asNumber()
                .floatValue(); }
        throw ItemscriptError.internalError(container, "getRequiredFloat.existed.but.was.not.number",
                keyValueParams(container, key, value));
    }

    public static Integer getRequiredInt(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredInt.not.present",
                keyValueParams(container, key, value)); }
        if (value.isNumber()) { return value.asNumber()
                .intValue(); }
        throw ItemscriptError.internalError(container, "getRequiredInt.existed.but.was.not.number",
                keyValueParams(container, key, value));
    }

    public static Long getRequiredLong(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredLong.not.present",
                keyValueParams(container, key, value)); }
        if (value.isString()) {
            try {
                return Long.parseLong(value.asString()
                        .stringValue());
            } catch (NumberFormatException e) {
                throw ItemscriptError.internalError(container,
                        "getRequiredLong.existed.but.could.not.parse.as.long", keyValueParams(container, key,
                                value));
            }
        }
        throw ItemscriptError.internalError(container, "getRequiredLong.existed.but.was.not.string",
                keyValueParams(container, key, value));
    }

    public static JsonObject getRequiredObject(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredObject.not.present",
                keyValueParams(container, key, value)); }
        if (value.isObject()) { return value.asObject(); }
        throw ItemscriptError.internalError(container, "getRequiredObject.existed.but.was.not.object",
                keyValueParams(container, key, value));
    }

    public static String getRequiredString(JsonContainer container, Object key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredString.not.present",
                keyValueParams(container, key, value)); }
        if (value.isString()) { return value.asString()
                .stringValue(); }
        throw ItemscriptError.internalError(container, "getRequiredString.existed.but.was.not.string",
                keyValueParams(container, key, value));
    }

    public static JsonValue getRequiredValue(JsonContainer container, String key, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(container, "getRequiredValue.not.present",
                keyValueParams(container, key, value)); }
        return value;
    }

    public static Params keyValueParams(JsonContainer container, Object key, JsonValue value) {
        return new Params().p("container", container.toCompactJsonString())
                .p("key", key + "")
                .p("value", value != null ? value.toCompactJsonString() : "null");
    }
}