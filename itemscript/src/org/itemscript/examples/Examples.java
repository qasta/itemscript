
package org.itemscript.examples;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.events.Event;
import org.itemscript.core.events.EventType;
import org.itemscript.core.events.Handler;
import org.itemscript.core.util.Base64;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonItem;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;
import org.itemscript.standard.StandardConfig;

public class Examples {
    private static JsonSystem system = StandardConfig.createSystem();

    public static void main(String args[]) {
        // JsonObject implements Map<String,JsonValue>; JsonArray implements List<JsonValue>
        {
            JsonObject object = system.createObject();
            Map<String, JsonValue> map = object;
            JsonArray array = system.createArray();
            List<JsonValue> list = array;
        }
        // Null-safe get methods.
        {
            JsonObject object = system.createObject();
            object.put("abc", "xyz");
            // Convenience method:
            {
                String stringValue = object.getString("abc");
            }
            // Compare to:
            {
                String stringValue;
                JsonValue value = object.get("abc");
                if (value != null) {
                    if (value instanceof JsonString) {
                        stringValue = ((JsonString) value).stringValue();
                    }
                }
            }
        }
        //  Typed put methods.
        {
            JsonObject object = system.createObject();
            // Convenience method:
            {
                object.put("abc", true);
            }
            // Compare to:
            {
                object.put("abc", system.createBoolean(true));
            }
        }
        // Get required value (checks existence & type, throws exception if not present or wrong type).
        {
            JsonObject object = system.createObject();
            object.put("abc", "xyz");
            // Convenience method:
            {
                String stringValue = object.getRequiredString("abc");
            }
            // Compare to:
            {
                String stringValue;
                String key = "abc";
                JsonValue value = object.get(key);
                if (value == null) { throw new RuntimeException("Missing required string value for key: " + key); }
                if (!(value instanceof JsonString)) { throw new RuntimeException("Wrong type: " + value.getClass()
                        .getSimpleName() + " for key: " + key); }
                stringValue = ((JsonString) value).stringValue();
            }
        }
        // Checking type & casting to specific JsonValue subtypes without instanceof or explicit casts
        {
            JsonValue value = system.createString("abc");
            // Convenience method:
            {
                boolean isString = value.isString();
                JsonString jsonStringValue = value.asString();
            }
            // Compare to:
            {
                boolean isString = value instanceof JsonString;
                JsonString jsonStringValue = (JsonString) value;
            }
        }
        // Check whether a value exists and is of the correct type.
        {
            JsonObject object = system.createObject();
            object.put("abc", "xyz");
            // Convenience method:
            {
                boolean hasAbcString = object.hasString("abc");
            }
            // Compare to:
            {
                boolean hasAbcString = object.containsKey("abc") && object.get("abc")
                        .isString();
            }
        }
        // Store Java longs in JsonStrings.
        {
            JsonObject object = system.createObject();
            // Convenience methods:
            {
                object.put("abc", Long.MAX_VALUE);
                Long longValue = object.getLong("abc");
            }
            // Compare to:
            {
                object.put("abc", system.createString(Long.toString(Long.MAX_VALUE)));
                Long longValue;
                String stringValue = object.getString("abc");
                if (stringValue != null) {
                    longValue = Long.valueOf(stringValue);
                }
            }
        }
        // Transparent, lazy base64 encoding for storing binary values in JsonStrings.
        {
            JsonObject object = system.createObject();
            byte[] binaryData = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
            // Convenience methods:
            {
                object.put("abc", binaryData);
                String base64Value = object.getString("abc"); // Only encoded when retrieved.
                object.put("def", base64Value);
                byte[] retrievedBinaryData = object.getBinary("def"); // Only decoded when retrieved.
            }
            // Compare to:
            {
                object.put("abc", new String(Base64.encode(binaryData))); // Must encode before putting.
                String base64Value = object.getString("abc");
                object.put("def", base64Value);
                byte[] retrievedBinaryData;
                retrievedBinaryData = Base64.decode(object.getString("def"));
            }
        }
        // Chainable put and add methods for containers.
        {
            JsonObject object = system.createObject();
            // Convenience methods:
            {
                object.put("def", system.createObject()
                        .p("abc", "xyz")
                        .put("123", 456));
            }
            // Compare to:
            {
                JsonObject anotherObject = system.createObject();
                anotherObject.put("abc", "xyz");
                anotherObject.put("123", 456);
                object.put("def", anotherObject);
            }
        }
        // Methods for creating an object or array inside another container.
        {
            JsonObject object = system.createObject();
            // Convenience methods:
            {
                object.createObject("abc");
                object.createArray("def");
            }
            // Compare to:
            {
                object.put("abc", system.createObject());
                object.put("def", system.createArray());
            }
        }
        // Methods for getting-or-creating-if-absent a container inside another container.
        {
            JsonObject object = system.createObject();
            object.createObject("abc");
            object.put("ghi", true);
            // Convenience methods:
            {
                JsonObject anotherObject = object.getOrCreateObject("abc"); // returns the existing object
                JsonObject aThirdObject = object.getOrCreateObject("def"); // creates a new object and returns it
                // JsonObject aFourthObject = object.getOrCreateObject("ghi"); // throws an exception for the wrong type
            }
            // Compare to:
            {
                // (a single example for brevity)
                JsonObject anotherObject;
                String key = "abc";
                JsonValue value = object.get(key);
                if (value == null) {
                    anotherObject = system.createObject();
                    object.put(key, anotherObject);
                } else if (value instanceof JsonObject) {
                    anotherObject = (JsonObject) value;
                } else {
                    throw new RuntimeException("value existed but was not a JsonObject for key: " + key);
                }
            }
        }
        // Store and retrieve native objects in containers.
        {
            JsonObject object = system.createObject();
            object.putNative("abc", new StringReader("xyz"));
            Reader reader = (Reader) object.getNative("abc");
        }
        // Virtual DB for storing and retrieving JSON values under URLs.
        {
            system.put("mem:/abc", "xyz");
            String stringValue = system.getString("mem:/abc"); // xyz
        }
        // Navigation inside JsonItems by URL fragment.
        {
            system.put("mem:/def", system.createObject()
                    .p("ghi", "xyz"));
            String stringValue = system.getString("mem:/def#ghi"); // xyz
        }
        // Dereferencing of URLs contained inside JsonItems relative to the JsonItem's source.
        {
            system.put("mem:/abc", system.createObject()
                    .p("internal", "#def")
                    .p("def", "xyz")
                    .p("absolute", "mem:/ghi")
                    .p("relative", "ghi"));
            system.put("mem:/ghi", 123);
            String internalString = system.dereference("mem:/abc#internal")
                    .stringValue(); // xyz
            int absoluteInt = system.dereference("mem:/abc#absolute")
                    .intValue(); // 123
            int relativeInt = system.dereference("mem:/abc#relative")
                    .intValue(); // 123
        }
        // Connectors for HTTP & classpath.
        {
            // JsonObject object = system.getObject("http://itemscript.org/test.json");
            String string = system.getString("classpath:org/itemscript/examples/string.json");
        }
        // Event handlers for listening to changes to JsonItems.
        {
            system.put("abc", "xyz");
            JsonItem item = system.get("abc")
                    .item();
            item.addHandler(EventType.PUT, "#", new Handler() {
                public void handle(Event event) {
                    int newValue = event.value()
                            .intValue();
                }
            });
            system.put("abc", 123); // triggers event handler
        }
        // Factory/Foundry interface to assist in creating Java objects from JSON declarations.
        {
            // For a fully worked example, see org/itemscript/test/JsonFoundryTest.java
        }
    }
}