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
public final class JsonUtil {
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
}