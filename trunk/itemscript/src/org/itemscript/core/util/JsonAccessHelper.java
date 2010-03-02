
package org.itemscript.core.util;

import org.itemscript.core.connectors.GetCallback;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonAccess;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

/**
 * Contains methods for implementing the {@link JsonAccess} interface. 
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
}