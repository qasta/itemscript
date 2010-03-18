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

package org.itemscript.core.gwt;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.values.ItemscriptCreator;
import org.itemscript.core.values.JsonValue;

/**
 * A JsonFactory for the GWT environment.
 * <p>
 * It uses an unsafe eval-based parser derived from the GWT-JSON module's JSON parser.
 * 
 * @author Jacob Davies<br/><a href="mailto:jacob@itemscript.org">jacob@itemscript.org</a>
 *
 */
public class GwtJsonCreator extends ItemscriptCreator {
    /**
     * Create a new GwtJsonFactory.
     * 
     * @param system The associated JsonSystem.
     */
    public GwtJsonCreator(JsonSystem system) {
        super(system);
    }

    @Override
    public JsonValue parse(String json) {
        return GwtJsonParser.parse(system(), json);
    }

// This code converts GWT JSON values into Itemscript JSON values. It's commented because I only use it during debugging.
//    private JsonValue convert(JSONValue value) {
//        if (value.isString() != null) {
//            return system().createString(value.isString()
//                    .stringValue());
//        } else if (value.isBoolean() != null) {
//            return system().createBoolean(value.isBoolean()
//                    .booleanValue());
//        } else if (value.isNull() != null) {
//            return system().createNull();
//        } else if (value.isNumber() != null) {
//            return system().createNumber(value.isNumber()
//                    .doubleValue());
//        } else if (value.isArray() != null) {
//            JSONArray origArray = value.isArray();
//            JsonArray array = system().createArray();
//            for (int i = 0; i < origArray.size(); ++i) {
//                array.add(convert(origArray.get(i)));
//            }
//            return array;
//        } else if (value.isObject() != null) {
//            JSONObject origObject = value.isObject();
//            JsonObject object = system().createObject();
//            for (String key : origObject.keySet()) {
//                object.put(key, convert(origObject.get(key)));
//            }
//            return object;
//        } else {
//            // Should never happen
//            throw new RuntimeException("Uknown value type: " + value);
//        }
//    }

    @Override
    public JsonValue parseReader(Object input) {
        throw new UnsupportedOperationException("error.itemscript.parseReader.not.supported");
    }
}