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

import org.itemscript.core.events.Event;
import org.itemscript.core.events.EventType;
import org.itemscript.core.events.Handler;
import org.itemscript.core.values.JsonBoolean;
import org.itemscript.core.values.JsonItem;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;
import org.junit.Test;

public class JsonItemTest extends ItemscriptTestBase {
    public static boolean putEventTriggered = false;
    public static String putEventValue = null;
    public static boolean removeEventTriggered = false;
    public static String removeEventFragment = null;

    @Test
    public void testConstruction() {
        JsonObject object = system().createObject()
                .p("1", "foo")
                .p("2", "bar");
        JsonItem item = system().createItem("mem:/test", object);
        assertNotNull(item);
    }

    @Test
    public void testDetachFromItem() {
        JsonValue value = system().get("http://itemscript.org/test.json");
        assertNotNull(value.item());
        JsonItem item = value.item();
        item.detachValue();
        assertNull(value.item());
        assertNull(item.value());
        system().put("/foo", value);
        JsonValue retValue = system().get("/foo");
        assertEquals(value, retValue);
        assertNotNull(value.item());
        assertEquals("mem:/foo", value.item()
                .source());
    }

    @Test
    public void testEventHandlers() {
        {
            JsonValue value = system().put("mem:/test", "value")
                    .value();
            JsonItem item = value.item();
            item.addHandler(new Handler() {
                public void handle(Event event) {
                    putEventTriggered = event.eventType()
                            .equals(EventType.PUT);
                    putEventValue = event.value()
                            .stringValue();
                }
            });
            item.put("#", "new value");
            assertTrue(putEventTriggered);
            assertEquals("new value", putEventValue);
        }
        {
            JsonValue value = system().put("mem:/test2#foo", "value")
                    .value();
            JsonItem item = value.item();
            item.addHandler(new Handler() {
                public void handle(Event event) {
                    removeEventTriggered = event.eventType()
                            .equals(EventType.REMOVE);
                    removeEventFragment = event.fragment();
                }
            });
            item.remove("#foo");
            assertTrue(removeEventTriggered);
            assertEquals("#foo", removeEventFragment);
        }
    }

    @Test
    public void testGetFromItem() {
        JsonObject object = system().createObject()
                .p("1", "foo")
                .p("2", "bar");
        system().put("/test", object);
        system().put("/test2", "bar");
        system().put("/test3", system().createObject()
                .p("a", "b"));
        JsonItem item = system().get("/test")
                .item();
        // Testing getting an element from the item itself.
        assertEquals("foo", item.get("#1")
                .stringValue());
        // Testing getting an element from a sibling item to the item.
        assertEquals("bar", item.get("test2")
                .stringValue());
        // Testing getting an sub-element from a sibling item.
        assertEquals("b", item.get("test3#a")
                .stringValue());
    }

    @Test
    public void testGetHttp() {
        JsonValue value = system().get("http://itemscript.org/test.json#test-string");
        assertNotNull(value);
        assertEquals("value", value.asString()
                .stringValue());
        JsonItem item = value.item();
        assertNotNull(item);
    }

    @Test
    public void testGetRelative() {
        system().put("mem:/foo/bar#one", "kittens");
        system().put("mem:/foo/baz#two", "bunnies");
        JsonValue kittens = system().get("mem:/foo/bar");
        JsonItem kittensItem = kittens.item();
        JsonValue bunnies = kittensItem.get("bar#one");
    }

    @Test
    public void testPut() {
        JsonObject object = system().createObject();
        JsonItem item = system().createItem("mem:/test", object);
        item.put("#foo/bar/baz", "value");
        assertEquals("value", item.get("#foo/bar/baz")
                .asString()
                .stringValue());
        JsonObject object2 = object.getObject("foo");
        JsonObject object3 = object2.getObject("bar");
        assertEquals("value", object3.getString("baz"));
    }

    @Test
    public void testSource() {
        JsonBoolean value = system().put("mem:/test", true)
                .value()
                .asBoolean();
        assertEquals("mem:/test", value.item()
                .source() + "");
    }
}