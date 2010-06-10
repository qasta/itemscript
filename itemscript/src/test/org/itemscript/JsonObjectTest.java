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

package test.org.itemscript;

import java.util.Map;
import java.util.Set;

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.util.StaticJsonUtil;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;
import org.junit.Test;

public class JsonObjectTest extends ItemscriptTestBase {
    @Test
    public void testChainedPut() {
        JsonObject object = system().createObject()
                .p("1", "foo")
                .p("2", "bar");
        assertEquals(2, object.size());
        assertEquals("foo", object.getString("1"));
        assertEquals("bar", object.getString("2"));
    }

    @Test
    public void testChangedKeys() {
        JsonObject a = system().createObject()
                .p("a", "x")
                .p("b", 1)
                .p("c", true)
                .p("d", system().createObject()
                        .p("foo", "bar"))
                .p("e", system().createArray()
                        .a("foo"))
                .p("f", system().createObject()
                        .p("foo", "bar"))
                .p("g", system().createArray()
                        .a("foo"));
        JsonObject b = a.copy()
                .asObject();
        b.remove("c");
        b.put("h", true);
        b.put("a", "y");
        b.put("b", "z");
        b.getObject("d")
                .put("foo", "new");
        b.getArray("e")
                .add("something");
        JsonObject changedKeys = StaticJsonUtil.changedKeys(a, b);
        assertTrue(changedKeys.containsKey("a"));
        assertTrue(changedKeys.containsKey("b"));
        assertTrue(changedKeys.containsKey("c"));
        assertTrue(changedKeys.containsKey("d"));
        assertTrue(changedKeys.containsKey("e"));
        assertFalse(changedKeys.containsKey("f"));
        assertFalse(changedKeys.containsKey("g"));
        assertTrue(changedKeys.containsKey("h"));
    }

    @Test
    public void testConstruction() {
        JsonObject object = system().createObject();
        assertNotNull(object);
    }

    @Test
    public void testCycles() {
        JsonObject a = system().createObject();
        JsonObject b = system().createObject();
        a.put("foo", b);
        boolean errorThrown = false;
        try {
            b.put("bar", a);
        } catch (ItemscriptError e) {
            errorThrown = true;
        }
        assertTrue(errorThrown);
    }

    @Test
    public void testEntrySet() {
        JsonObject object = system().createObject()
                .p("1", "foo")
                .p("2", "bar");
        for (Map.Entry<String, JsonValue> entry : object.entrySet()) {
            if (entry.getKey()
                    .equals("1")) {
                assertEquals("foo", entry.getValue()
                        .asString()
                        .stringValue());
            } else if (entry.getKey()
                    .equals("2")) {
                assertEquals("bar", entry.getValue()
                        .asString()
                        .stringValue());
            } else {
                fail();
            }
        }
    }

    public void testFragment() {
        JsonObject object = system().createObject()
                .p("foo", system().createArray()
                        .a("value"));
        JsonValue value = object.getArray("foo")
                .get(0);
        assertEquals("value", value.asString()
                .stringValue());
        assertEquals("#foo.0", value.fragment());
        JsonObject obj2 = system().createObject()
                .p("foo", system().createObject()
                        .p("bar", "value"));
        JsonObject obj3 = system().createObject()
                .p("baz", obj2);
        assertEquals("#baz.foo.bar", obj3.getObject("baz")
                .getObject("foo")
                .get("bar")
                .fragment());
    }

    @Test
    public void testGetOrCreateArray() {
        JsonObject object = system().createObject();
        JsonArray array = object.getOrCreateArray("foo");
        array.add(1);
        assertEquals((Integer) 1, object.getArray("foo")
                .getInt(0));
    }

    @Test
    public void testGetOrCreateObject() {
        JsonObject object = system().createObject();
        JsonObject object2 = object.getOrCreateObject("foo");
        object2.put("bar", "baz");
        assertEquals("baz", object.getObject("foo")
                .getString("bar"));
    }

    @Test
    public void testGetRequired() {
        JsonObject object = system().createObject()
                .p("a", true)
                .p("b", Integer.MAX_VALUE)
                .p("c", Float.MAX_VALUE)
                .p("d", Double.MAX_VALUE)
                .p("e", Long.MAX_VALUE)
                .p("f", "A string")
                .p("g", system().createObject())
                .p("h", system().createArray());
        object.getRequiredBoolean("a");
        object.getRequiredInt("b");
        object.getRequiredFloat("c");
        object.getRequiredDouble("d");
        object.getRequiredLong("e");
        object.getRequiredString("f");
        object.getRequiredObject("g");
        object.getRequiredArray("h");
        boolean failedBoolean = false;
        try {
            object.getRequiredBoolean("b");
        } catch (ItemscriptError e) {
            failedBoolean = true;
        }
        assertTrue(failedBoolean);
        boolean failedInt = false;
        try {
            object.getRequiredInt("a");
        } catch (ItemscriptError e) {
            failedInt = true;
        }
        assertTrue(failedInt);
        boolean failedFloat = false;
        try {
            object.getRequiredFloat("a");
        } catch (ItemscriptError e) {
            failedFloat = true;
        }
        assertTrue(failedFloat);
        boolean failedDouble = false;
        try {
            object.getRequiredDouble("a");
        } catch (ItemscriptError e) {
            failedDouble = true;
        }
        assertTrue(failedDouble);
        boolean failedLong = false;
        try {
            object.getRequiredLong("a");
        } catch (ItemscriptError e) {
            failedLong = true;
        }
        assertTrue(failedLong);
        boolean failedString = false;
        try {
            object.getRequiredString("a");
        } catch (ItemscriptError e) {
            failedString = true;
        }
        assertTrue(failedString);
        boolean failedObject = false;
        try {
            object.getRequiredObject("a");
        } catch (ItemscriptError e) {
            failedObject = true;
        }
        assertTrue(failedObject);
        boolean failedArray = false;
        try {
            object.getRequiredArray("a");
        } catch (ItemscriptError e) {
            failedArray = true;
        }
        assertTrue(failedArray);
    }

    public void testHasOptional() {
        JsonObject object = system().createObject()
                .p("exists", "foo")
                .p("not a string", 1);
        assertTrue(object.hasString("exists"));
        assertFalse(object.hasString("does not exist"));
        assertFalse(object.hasString("not a string"));
    }

    public void testKeyChangeOnRemoval() {
        JsonObject object = system().createObject()
                .p("foo", "bar");
        JsonValue value = object.get("foo");
        assertEquals("foo", value.key());
        assertSame(object, value.parent());
        object.remove("foo");
        assertNull(value.key());
        assertNull(value.parent());
        assertNull(value.item());
    }

    @Test
    public void testKeySet() {
        JsonObject object = system().createObject()
                .p("1", "foo")
                .p("2", "bar");
        Set<String> keys = object.keySet();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("1"));
        assertTrue(keys.contains("2"));
    }

    @Test
    public void testNativeObject() {
        JsonObject object = system().createObject();
        object.putNative("foo", new Boolean(true));
        assertEquals(new Boolean(true), object.getNative("foo"));
    }

    @Test
    public void testPut() {
        JsonObject object = system().createObject();
        object.put("foo", "bar");
        assertEquals("bar", object.getString("foo"));
    }

    @Test
    public void testPutByPath() {
        JsonObject object = system().createObject();
        object.putByPath("foo", system().createString("bar"));
        assertEquals("bar", object.getString("foo"));
        object.putByPath("x/y/z", system().createString("bar"));
        assertEquals("bar", object.getByPath("x/y/z")
                .stringValue());
    }
}