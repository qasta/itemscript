/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This class is the mutant offspring of the GWT JSON package's JSONParser, JSONObject, and
 * JSONArray classes, modified to directly create Itemscript JSON types.
 * 
 * Modified by Jacob Davies, 2010.  
 */

package org.itemscript.core.gwt;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Parses the string representation of a JSON object into a set of
 * JsonValue-derived objects. Derived from the GWT JSON package's JSONParser, JSONObject,
 * and JSONArray classes.
 */
public final class GwtJsonParser {
    private static final JavaScriptObject typeMap = initTypeMap();

    private static void addToArray(JsonArray array, JsonValue value) {
        array.add(value);
    }

    private static native JsonValue arrayGet(JsonSystem system, JavaScriptObject array, int index) /*-{
        var v = array[index];
        var func = @org.itemscript.core.gwt.GwtJsonParser::typeMap[typeof v];
        return func ? func(system, v) : @org.itemscript.core.gwt.GwtJsonParser::throwUnknownTypeException(Ljava/lang/String;)(typeof v);
    }-*/;

    public static native JsonValue convert(JsonSystem system, JavaScriptObject value) /*-{
        var v = value["value"];
        var func = @org.itemscript.core.gwt.GwtJsonParser::typeMap[typeof v];
        return func ? func(system,v) : @org.itemscript.core.gwt.GwtJsonParser::throwUnknownTypeException(Ljava/lang/String;)(typeof v);
    }-*/;

    static native JsonValue convertObject(JsonSystem system, JavaScriptObject v) /*-{
        var func = @org.itemscript.core.gwt.GwtJsonParser::typeMap[typeof v];
        return func ? func(system,v) : @org.itemscript.core.gwt.GwtJsonParser::throwUnknownTypeException(Ljava/lang/String;)(typeof v);
    }-*/;

    private static JsonValue createArray(JsonSystem system) {
        return system.createArray();
    }

    private static JsonValue createBoolean(JsonSystem system, boolean v) {
        return system.createBoolean(v);
    }

    private static JsonValue createNull(JsonSystem system) {
        return system.createNull();
    }

    private static JsonValue createNumber(JsonSystem system, double v) {
        return system.createNumber(v);
    }

    private static native JsonValue createObject(JsonSystem system, Object o) /*-{
        if (!o) {
        return @org.itemscript.core.gwt.GwtJsonParser::createNull(Lorg/itemscript/core/JsonSystem;)(system);
        }
        var v = o.valueOf ? o.valueOf() : o;
        if (v !== o) {
        // It was a primitive wrapper, unwrap it and try again.
        var func = @org.itemscript.core.gwt.GwtJsonParser::typeMap[typeof v];
        return func ? func(system,v) : @org.itemscript.core.gwt.GwtJsonParser::throwUnknownTypeException(Ljava/lang/String;)(typeof v);
        } else if (o instanceof Array || o instanceof $wnd.Array) {
        // Looks like an Array; wrap as JsonArray.
        // NOTE: this test can fail for objects coming from a different window,
        // but we know of no reliable tests to determine if something is an Array
        // in all cases.
        var array = @org.itemscript.core.gwt.GwtJsonParser::createArray(Lorg/itemscript/core/JsonSystem;)(system);
        for (var i = 0; i < o.length; ++i) {
        var value = o[i];
        var func2 = @org.itemscript.core.gwt.GwtJsonParser::typeMap[typeof value];
        var converted = func2 ? func2(system,value) : @org.itemscript.core.gwt.GwtJsonParser::throwUnknownTypeException(Ljava/lang/String;)(typeof value);
        @org.itemscript.core.gwt.GwtJsonParser::addToArray(Lorg/itemscript/core/values/JsonArray;Lorg/itemscript/core/values/JsonValue;)(array,converted);
        }
        return array;
        } else {
        // This is a basic JavaScript object; wrap as JsonObject.
        var object = @org.itemscript.core.gwt.GwtJsonParser::reallyCreateObject(Lorg/itemscript/core/JsonSystem;)(system);
        for (var key in o) {
        if (o.hasOwnProperty(key)) {
        var value = o[key];
        var func2 = @org.itemscript.core.gwt.GwtJsonParser::typeMap[typeof value];
        var converted = func2 ? func2(system,value) : @org.itemscript.core.gwt.GwtJsonParser::throwUnknownTypeException(Ljava/lang/String;)(typeof value);
        @org.itemscript.core.gwt.GwtJsonParser::putInObject(Lorg/itemscript/core/values/JsonObject;Ljava/lang/String;Lorg/itemscript/core/values/JsonValue;)(object,key,converted);
        }
        }
        return object;
        }
    }-*/;

    private static JsonValue createString(JsonSystem system, String v) {
        return system.createString(v);
    }

    private static JsonValue createUndefined() {
        return null;
    }

    private static native JsonValue evaluate(JsonSystem system, String jsonString) /*-{
        var v;
        if (typeof(JSON) === 'object' && typeof(JSON.parse) === 'function') {
        v = JSON.parse(jsonString);
        } else {
        v = eval('(' + jsonString + ')');
        }
        var func = @org.itemscript.core.gwt.GwtJsonParser::typeMap[typeof v];
        return func ? func(system,v) : @org.itemscript.core.gwt.GwtJsonParser::throwUnknownTypeException(Ljava/lang/String;)(typeof v);
    }-*/;

    private static native JavaScriptObject initTypeMap() /*-{
        return {
        "boolean": @org.itemscript.core.gwt.GwtJsonParser::createBoolean(Lorg/itemscript/core/JsonSystem;Z),
        "number": @org.itemscript.core.gwt.GwtJsonParser::createNumber(Lorg/itemscript/core/JsonSystem;D),
        "string": @org.itemscript.core.gwt.GwtJsonParser::createString(Lorg/itemscript/core/JsonSystem;Ljava/lang/String;),
        "object": @org.itemscript.core.gwt.GwtJsonParser::createObject(Lorg/itemscript/core/JsonSystem;Ljava/lang/Object;),
        "function": @org.itemscript.core.gwt.GwtJsonParser::createObject(Lorg/itemscript/core/JsonSystem;Ljava/lang/Object;),
        "undefined": @org.itemscript.core.gwt.GwtJsonParser::createUndefined(),
        }
    }-*/;

    /**
     * Parse a String as JSON.
     * 
     * @param system The associated JsonSystem.
     * @param jsonString The JSON to parse.
     * @return The JsonValue corresponding to the JSON string.
     */
    public static JsonValue parse(JsonSystem system, String jsonString) {
        if (jsonString == null) { throw new NullPointerException(); }
        if (jsonString.length() == 0) { throw new IllegalArgumentException("empty argument"); }
        try {
            return evaluate(system, jsonString);
        } catch (JavaScriptException ex) {
            throw new ItemscriptError("error.itemscript.GwtJsonParser.parse.JavaScriptException", ex);
        }
    }

    private static void putInObject(JsonObject object, String key, JsonValue value) {
        object.put(key, value);
    }

    private static JsonValue reallyCreateObject(JsonSystem system) {
        return system.createObject();
    }

    private static void throwUnknownTypeException(String typeString) {
        throw new ItemscriptError("Unexpected typeof result: " + typeString);
    }
}