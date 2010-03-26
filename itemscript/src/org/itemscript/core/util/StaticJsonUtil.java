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

package org.itemscript.core.util;

import java.util.List;
import java.util.Map;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.mappings.Mapper;
import org.itemscript.core.mappings.Mapping;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.ItemscriptCreator;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

/**
 * Utility methods for working with JSON values.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 *
 */
public final class StaticJsonUtil {
    private static final String TO_HTML_JSON_DEFAULT_COLOR_MAP_PATH = "/itemscript/toHtmlJson#defaultColorMap";
    public static final String STRING = "string";
    public static final String NUMBER = "number";
    public static final String NULL = "null";
    public static final String BOOLEAN = "boolean";
    public static final String ARRAY_OPEN = "array-open";
    public static final String ARRAY_CLOSE = "array-close";
    public static final String ARRAY_COMMA = "array-comma";
    public static final String OBJECT_OPEN = "object-open";
    public static final String OBJECT_CLOSE = "object-close";
    public static final String OBJECT_KEY = "object-key";
    public static final String OBJECT_COLON = "object-colon";

    /**
     * Take the given {@link JsonArray}, and return a {@link List}<JsonObject> corresponding to it,
     * where any values that are not of type {@link JsonObject} will be null.
     * 
     * @param array The JsonArray to process.
     * @return A JsonArray of JsonObjects.
     */
    public static List<JsonObject> arrayToObjectList(JsonArray array) {
        return Mapper.listToList(array, new Mapping<JsonValue, JsonObject>() {
            public JsonObject map(JsonValue value) {
                return value.asObject();
            }
        });
    }

    /**
     * Take the given {@link JsonArray}, and return a {@link List}<String> corresponding to it,
     * where any values that are not of type {@link JsonString} will be null.
     * 
     * @param array The JsonArray to process.
     * @return A list of strings.
     */
    public static List<String> arrayToStringList(JsonArray array) {
        return Mapper.listToList(array, new Mapping<JsonValue, String>() {
            public String map(JsonValue value) {
                if (value.isString()) { return value.stringValue(); }
                return null;
            }
        });
    }

    /**
     * HTML-encode a string. This simple method only replaces the five characters &, <, >, ", and '.
     * 
     * @param input the String to convert
     * @return a new String with HTML encoded characters
     */
    public static String htmlEncode(String input) {
        String output = input.replaceAll("&", "&amp;");
        output = output.replaceAll("<", "&lt;");
        output = output.replaceAll(">", "&gt;");
        output = output.replaceAll("\"", "&quot;");
        output = output.replaceAll("'", "&#039;");
        return output;
    }

    private static void indent(int indent, StringBuffer sb) {
        for (int i = 0; i < indent; ++i) {
            sb.append("    ");
        }
    }

    /**
     * Returns true if the supplied int is even, false if it is odd.
     * 
     * @param i The number to test.
     * @return True if the number was even, false if it was odd.
     */
    public static boolean isEven(int i) {
        return (i % 2) == 0;
    }

    /**
     * Join a list of strings with the given joining string.
     * 
     * @param strings The strings to join.
     * @param join The string to join them with, or null if no string is to be used.
     * @return The string consisting of the strings joined together.
     */
    public static String join(List<String> strings, String join) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < strings.size(); ++i) {
            String entry = strings.get(i);
            buffer.append(entry);
            if (join != null) {
                if (i < (strings.size() - 1)) {
                    buffer.append(join);
                }
            }
        }
        return buffer.toString();
    }

    /**
     * Take a {@link Map}<String,String> and return a new {@link JsonObject} corresponding to it,
     * where keys in the original map will be keys in the JsonObject and values in the original
     * map will be {@link JsonString} values in the JsonObject.
     * 
     * @param system The associated JsonSystem.
     * @param map The map containing strings.
     * @return A JsonObject.
     */
    public static JsonObject stringMapToObject(JsonSystem system, Map<String, String> map) {
        JsonObject object = system.createObject();
        for (String key : map.keySet()) {
            object.put(key, map.get(key));
        }
        return object;
    }

    /**
     * Convert the supplied JsonValue to a pretty-printed HTML representation, using the default
     * color map.
     * 
     * @param value The value to convert.
     * @return An HTML string.
     */
    public static String toHtmlJson(JsonValue value) {
        JsonObject defaultColorMap = value.system()
                .getObject(TO_HTML_JSON_DEFAULT_COLOR_MAP_PATH);
        if (defaultColorMap == null) {
            defaultColorMap = value.system()
                    .createObject(TO_HTML_JSON_DEFAULT_COLOR_MAP_PATH)
                    .value()
                    .asObject();
            defaultColorMap.p(STRING, "#483D8B")
                    .p(NUMBER, "#8B008B")
                    .p(BOOLEAN, "#CD5C5C")
                    .p(NULL, "#696969")
                    .p(ARRAY_OPEN, "#8B0000")
                    .p(ARRAY_CLOSE, "#8B0000")
                    .p(ARRAY_COMMA, "#8B0000")
                    .p(OBJECT_OPEN, "#8B0000")
                    .p(OBJECT_CLOSE, "#8B0000")
                    .p(OBJECT_KEY, "#006400")
                    .p(OBJECT_COLON, "#8B0000");
        }
        return toHtmlJson(value, defaultColorMap);
    }

    /**
     * Convert the supplied JsonValue to a pretty-printed HTML string, using the supplied color map.
     * 
     * @param value The value to convert.
     * @param colorMap The color map to use; see {@link StaticJsonUtil} for constants to use as keys in the map.
     * @return An HTML string.
     */
    public static String toHtmlJson(JsonValue value, JsonObject colorMap) {
        StringBuffer sb = new StringBuffer();
        sb.append("<pre>\n");
        toHtmlJson(value, sb, colorMap, 0);
        if (value.isContainer()) {
            sb.append("\n");
        }
        sb.append("</pre>");
        return sb.toString();
    }

    private static void toHtmlJson(JsonValue value, StringBuffer sb, JsonObject colorMap, int indent) {
        if (value.isString()) {
            sb.append("<span style='color:" + colorMap.getString(STRING) + ";'>"
                    + htmlEncode(value.toJsonString()) + "</span>");
        } else if (value.isNumber()) {
            sb.append("<span style='color:" + colorMap.getString(NUMBER) + ";'>"
                    + htmlEncode(value.toJsonString()) + "</span>");
        } else if (value.isNull()) {
            sb.append("<span style='color:" + colorMap.getString(NULL) + ";'>null</span>");
        } else if (value.isBoolean()) {
            sb.append("<span style='color:" + colorMap.getString(BOOLEAN) + ";'>"
                    + htmlEncode(value.toJsonString()) + "</span>");
        } else if (value.isArray()) {
            JsonArray array = value.asArray();
            if (array.size() == 0) {
                sb.append("<span style='color:" + colorMap.getString(ARRAY_OPEN)
                        + ";'>[</span><span style='color:" + colorMap.getString(ARRAY_CLOSE) + ";'>]</span>");
            } else {
                sb.append("<span style='color:" + colorMap.getString(ARRAY_OPEN) + ";'>[</span>\n");
                for (int i = 0; i < array.size(); ++i) {
                    indent(indent + 1, sb);
                    toHtmlJson(array.get(i), sb, colorMap, indent + 1);
                    if (i + 1 != array.size()) {
                        sb.append("<span style='color:" + colorMap.getString(ARRAY_COMMA) + ";'>,</span>");
                    }
                    sb.append("\n");
                }
                indent(indent, sb);
                sb.append("<span style='color:" + colorMap.getString(ARRAY_CLOSE) + ";'>]</span>");
            }
        } else if (value.isObject()) {
            JsonObject object = value.asObject();
            if (object.size() == 0) {
                sb.append("<span style='color:" + colorMap.getString(OBJECT_OPEN)
                        + ";'>{</span><span style='color:" + colorMap.getString(OBJECT_CLOSE) + ";'>}</span>");
            } else {
                sb.append("<span style='color:" + colorMap.getString(OBJECT_OPEN) + ";'>{</span>\n");
                int i = 0;
                for (String key : object.keySet()) {
                    indent(indent + 1, sb);
                    sb.append("<span style='color:" + colorMap.getString(OBJECT_KEY) + ";'>"
                            + ItemscriptCreator.quotedString(key) + "</span> <span style='color:"
                            + colorMap.getString(OBJECT_COLON) + ";'>:</span> ");
                    toHtmlJson(object.get(key), sb, colorMap, indent + 1);
                    if (i + 1 != object.size()) {
                        sb.append(",");
                    }
                    sb.append("\n");
                    ++i;
                }
                indent(indent, sb);
                sb.append("<span style='color:" + colorMap.getString(OBJECT_CLOSE) + ";'>}</span>");
            }
        }
    }

    /**
     * Test the supplied URL and Content-Type to see if they look like JSON. A pretty crude test - if the filename
     * ends with .json, or the content type is application/json, text/json, or text/x-json, or application/x-javascript,
     * we will treat it as JSON.
     * 
     * @param url The URL of the resource.
     * @param contentType The Content-Type string of the resource.
     * @return True if the resource looks like JSON, false otherwise.
     */
    public static boolean looksLikeJson(Url url, String contentType) {
        return url.filename()
                .toLowerCase()
                .endsWith(".json") || contentType.startsWith("application/json")
                || contentType.startsWith("text/json") || contentType.startsWith("text/x-json")
                || contentType.startsWith("application/x-javascript");
    }
}