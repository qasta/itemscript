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

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;
import org.junit.Test;

public class JsonArrayTest extends ItemscriptTestBase {
    @Test
    public void testAdd() {
        JsonArray array = system().createArray();
        array.add("foo");
        array.add("bar");
        assertEquals("foo", array.get(0)
                .asString()
                .stringValue());
        assertEquals("bar", array.get(1)
                .asString()
                .stringValue());
        assertEquals(2, array.size());
    }

    @Test
    public void testAddObjectAndArray() {
        JsonArray array = system().createArray();
        array.addObject();
        array.addArray();
        assertTrue(array.getObject(0) instanceof JsonObject);
        assertTrue(array.getArray(1) instanceof JsonArray);
    }

    @Test
    public void testArrayParsing() {
        JsonArray array = system().parse("[1,2,3]")
                .asArray();
        assertEquals((Integer) 1, array.get(0)
                .asNumber()
                .intValue());
        assertEquals(3, array.size());
    }

    @Test
    public void testChaining() {
        JsonArray array = system().createArray()
                .a(1)
                .a(2)
                .a(3);
        array.set(0, 99);
        assertEquals(3, array.size());
        assertEquals((Integer) 99, array.get(0)
                .asNumber()
                .intValue());
    }

    @Test
    public void testConstruction() {
        JsonArray array = system().createArray();
        assertEquals(0, array.size());
    }

    @Test
    public void testCopy() {
        JsonArray array = system().createArray()
                .a(1)
                .a(2)
                .a(3);
        JsonObject object = array.createObject(3);
        object.put("foo", "bar");
        JsonArray array2 = array.copy()
                .asArray();
        assertEquals(array.get(0), array2.get(0));
        assertEquals(array.getObject(3)
                .getString("foo"), array2.getObject(3)
                .getString("foo"));
    }

    @Test
    public void testCreateArray() {
        JsonArray array = system().createArray();
        JsonArray array2 = array.createArray(0);
        array2.set(0, "foo");
        assertEquals("foo", array.getArray(0)
                .getString(0));
    }

    @Test
    public void testCreateObject() {
        JsonArray array = system().createArray();
        JsonObject object = array.createObject(0);
        object.put("foo", "bar");
        assertEquals("bar", array.getObject(0)
                .getString("foo"));
    }

    @Test
    public void testDetectAttemptToPutValueAlreadyInAnotherContainer() {
        JsonArray array1 = system().createArray()
                .a(true);
        JsonValue val = array1.get(0);
        JsonArray array2 = system().createArray();
        boolean failed = false;
        try {
            array2.add(val);
        } catch (ItemscriptError e) {
            failed = true;
        }
        assertTrue(failed);
        failed = false;
        try {
            array2.set(0, val);
        } catch (ItemscriptError e) {
            failed = true;
        }
        assertTrue(failed);
        array1.remove(0);
        assertNotNull(val);
        assertNull(val.item());
        assertNull(val.key());
        assertNull(val.parent());
        array2.add(val);
        assertSame(val, array2.get(0));
    }

    @Test
    public void testFragment() {
        JsonArray array = system().createArray()
                .a("foo")
                .a("bar")
                .a(system().createObject()
                        .p("baz", "value"));
        JsonValue foo = array.get(0);
        JsonValue bar = array.get(1);
        JsonValue baz = array.getObject(2)
                .get("baz");
        assertEquals("foo", foo.asString()
                .stringValue());
        assertEquals("bar", bar.asString()
                .stringValue());
        assertEquals("value", baz.asString()
                .stringValue());
        assertEquals("#0", foo.fragment());
        assertEquals("#1", bar.fragment());
        assertEquals("#2.baz", baz.fragment());
        array.add(1, system().createString("kitten"));
        assertEquals("#0", foo.fragment());
        assertEquals("#2", bar.fragment());
        assertEquals("#3.baz", baz.fragment());
        array.remove(2);
        assertEquals("#0", foo.fragment());
        assertEquals("#", bar.fragment());
        assertEquals("#2.baz", baz.fragment());
        assertNull(bar.key());
        assertNull(bar.parent());
    }

    @Test
    public void testPut() {
        JsonArray array = system().createArray();
        array.put("0", "foo");
        array.put("1", "bar");
        assertEquals("foo", array.get(0)
                .asString()
                .stringValue());
        assertEquals("bar", array.get(1)
                .asString()
                .stringValue());
        assertEquals(2, array.size());
    }

    @Test
    public void testRemove() {
        JsonArray array = system().createArray()
                .a(1)
                .a(2)
                .a(3);
        array.remove(1);
        assertEquals(2, array.size());
        assertEquals((Integer) 3, array.get(1)
                .asNumber()
                .intValue());
    }

    @Test
    public void testSet() {
        JsonArray array = system().createArray();
        array.set(0, "foo");
        array.set(1, "bar");
        assertEquals("foo", array.get(0)
                .asString()
                .stringValue());
        assertEquals("bar", array.get(1)
                .asString()
                .stringValue());
        assertEquals(2, array.size());
    }

    @Test
    public void testTypedGets() {
        JsonArray array = system().createArray()
                .a("foo")
                .a(1)
                .a(1.5)
                .a(true)
                .a(123L);
        assertEquals("foo", array.getString(0));
        assertEquals((Integer) 1, array.getInt(1));
        assertEquals(1.5, array.getDouble(2));
        assertEquals(1.5f, array.getFloat(2));
        assertEquals((Boolean) true, array.getBoolean(3));
        assertEquals((Long) 123L, array.getLong(4));
    }
}