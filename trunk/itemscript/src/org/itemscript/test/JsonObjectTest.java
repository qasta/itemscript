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

package org.itemscript.test;

import java.util.Map;
import java.util.Set;

import org.itemscript.core.exceptions.ItemscriptError;
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
}