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

import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;
import org.junit.Test;

public class JsonStringTest extends ItemscriptTestBase {
    @Test
    public void testBinaryConversion() {
        byte[] array = new byte[3];
        array[0] = Byte.MIN_VALUE;
        array[1] = Byte.MAX_VALUE;
        array[2] = 0;
        JsonString string = system().createString(array);
        assertNotNull(string);
        String base64 = string.stringValue();
        assertEquals("gH8A", base64);
        JsonString string2 = system().createString(base64);
        byte[] array2 = string2.binaryValue();
        assertEquals(array.length, array2.length);
        assertEquals(array[0], array2[0]);
        assertEquals(array[1], array2[1]);
        assertEquals(array[2], array2[2]);
    }

    @Test
    public void testCopy() {
        JsonString string = system().createString("foo");
        JsonString string2 = string.copy()
                .asString();
        assertEquals("foo", string2.stringValue());
        byte[] array = new byte[3];
        array[0] = Byte.MIN_VALUE;
        array[1] = Byte.MAX_VALUE;
        array[2] = 0;
        JsonString string3 = system().createString(array);
        JsonString string4 = string3.copy()
                .asString();
        byte[] retrievedValue = string4.binaryValue();
        assertEquals(Byte.MIN_VALUE, retrievedValue[0]);
        assertEquals(string3.stringValue(), string4.stringValue());
     }

    @Test
    public void testConstruction() {
        JsonString string = system().createString("xyz");
        assertEquals("xyz", string.stringValue());
    }

    @Test
    public void testDereference() {
        JsonString value = system().get("classpath:org/itemscript/test/test2.json#key")
                .asString();
        JsonValue test = value.dereference();
        assertEquals("bar", test.asObject()
                .getString("foo"));
    }

    @Test
    public void testLongConversion() {
        JsonString string = system().createString(123L);
        assertEquals(new Long(123L), string.longValue());
        assertEquals("123", string.stringValue());
    }

    @Test
    public void testStringParsing() {
        JsonString string = system().parse("\"xyz\"")
                .asString();
        assertEquals("xyz", string.stringValue());
        JsonString string2 = system().parse("\"\\\"\"")
                .asString();
        assertEquals("\"", string2.stringValue());
    }
}