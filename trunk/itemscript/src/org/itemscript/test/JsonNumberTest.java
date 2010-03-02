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


import org.itemscript.core.values.JsonNumber;
import org.junit.Test;

public class JsonNumberTest extends ItemscriptTestBase {
    @Test
    public void testConstruction() {
        JsonNumber number = system().createNumber(123);
        assertEquals((Integer) 123, number.intValue());
    }

    @Test
    public void testDoubleConversion() {
        JsonNumber number = system().createNumber(Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, number.doubleValue());
        JsonNumber number2 = system().createNumber(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, number2.doubleValue());
    }

    @Test
    public void testFloatConversion() {
        JsonNumber number = system().createNumber(Float.MIN_VALUE);
        assertEquals(Float.MIN_VALUE, number.floatValue());
        JsonNumber number2 = system().createNumber(Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, number2.floatValue());
    }

    @Test
    public void testFloatingPoint() {
        JsonNumber number = system().parse("1.2")
                .asNumber();
        assertEquals("1.2", number.floatValue() + "");
    }

    @Test
    public void testIntConversion() {
        JsonNumber number = system().createNumber(Integer.MIN_VALUE);
        assertEquals((Integer) Integer.MIN_VALUE, number.intValue());
        JsonNumber number2 = system().createNumber(Integer.MAX_VALUE);
        assertEquals((Integer) Integer.MAX_VALUE, number2.intValue());
    }

    @Test
    public void testNumberParsing() {
        JsonNumber number = system().parse("123")
                .asNumber();
        assertEquals((Integer) 123, number.intValue());
    }
}