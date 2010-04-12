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

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.template.Template;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;
import org.junit.Test;

public class TemplateTest extends ItemscriptTestBase {
    private JsonValue context;
    private boolean threwException;

    public JsonValue context() {
        return context;
    }

    private String processTemplate(String text) {
        return new Template(system(), text).interpret(context);
    }

    @Override
    protected void setUp() {
        super.setUp();
        context = system().createObject()
                .p("name", "Jacob");
        system().put("mem:/TemplateTest/context", context);
        threwException = false;
    }

    @Test
    public void testLiteral() {
        String text = "a {&b%3D%7B%25} c";
        String after = new Template(system(), text).interpret(context());
        assertEquals("a b={% c", after);
    }

    @Test
    public void testEmptyField() {
        context.asObject()
                .put("y", "x");
        String text = "{.section y}a {} b{.end}";
        String after = processTemplate(text);
        assertEquals("a x b", after);
    }

    @Test
    public void testCarriageReturnError() {
        String text = "a {xyz\nabc} c";
        try {
            processTemplate(text);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testStringContext() {
        String text = "a {} c";
        context = system().createString("b");
        String after = processTemplate(text);
        assertEquals("a b c", after);
    }

    @Test
    public void testUnbalancedBraceError() {
        String text = "a { \nbc";
        try {
            processTemplate(text);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testOverlappingBraceError() {
        String text = "a { f{a }bc";
        try {
            processTemplate(text);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testNullContextError() {
        String text = "a";
        context = null;
        try {
            processTemplate(text);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testFragmentOnlyNoBaseUrlNoItemError() {
        String text = "a {@#field} c";
        context = system().createObject();
        try {
            processTemplate(text);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testReference() {
        system().put("mem:/TemplateTest/value", "b");
        String text = "a {@mem:/TemplateTest/value} c";
        String after = processTemplate(text);
        assertEquals("a b c", after);
        context.asObject()
                .put("x", "y");
        text = "a {@#x} c";
        after = processTemplate(text);
        assertEquals("a y c", after);
        system().put("mem:/TemplateTest/value2", "q");
        text = "a {@value2} c";
        after = processTemplate(text);
        assertEquals("a q c", after);
        text = "a {@mem:/TemplateTest/context#x} c";
        after = processTemplate(text);
        assertEquals("a y c", after);
    }

    @Test
    public void testCoerceFail() {
        String text = "a {@mem:/TemplateTest/context} c";
        try {
            processTemplate(text);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testBraces() {
        String text = "a {(} {)} c";
        String after = processTemplate(text);
        assertEquals("a { } c", after);
    }

    @Test
    public void testContentsTrim() {
        String text = "a { ( } c";
        String after = processTemplate(text);
        assertEquals("a { c", after);
    }

    @Test
    public void testSection() {
        String text = "a {.section address}{street}{.end} c";
        JsonObject address = context.asObject()
                .createObject("address");
        address.put("street", "xyz");
        String after = processTemplate(text);
        assertEquals("a xyz c", after);
    }

    @Test
    public void testOrSection() {
        String text = "a {.section address}{street}{.or}kittens{.end} c";
        JsonObject address = context.asObject()
                .createObject("address");
        address.put("street", "xyz");
        String after = processTemplate(text);
        assertEquals("a xyz c", after);
        context.asObject()
                .remove("address");
        after = processTemplate(text);
        assertEquals("a kittens c", after);
    }

    @Test
    public void testMissingEnd() {
        String text = "a {.section address} c";
        try {
            processTemplate(text);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testForeach() {
        String text = "a {.foreach items}{}{.end} c";
        JsonArray array = context.asObject()
                .createArray("items");
        array.add("x");
        array.add("y");
        array.add("z");
        String after = processTemplate(text);
        assertEquals("a xyz c", after);
    }

    @Test
    public void testJoin() {
        String text = "a {.foreach items}{}{.join}{name}{.end} c";
        JsonArray array = context.asObject()
                .createArray("items");
        array.add("x");
        array.add("y");
        array.add("z");
        String after = processTemplate(text);
        assertEquals("a xJacobyJacobz c", after);
    }

    @Test
    public void testNestedForeach() {
        String text = "a {.foreach items}{.foreach subItems}{}{.join} {.end}{.join} {.end} c";
        JsonObject contents = system().createObject();
        contents.createArray("subItems")
                .a("x")
                .a("y")
                .a("z");
        JsonArray array = context.asObject()
                .createArray("items")
                .a(contents.copy())
                .a(contents.copy())
                .a(contents.copy());
        String after = processTemplate(text);
        assertEquals("a x y z x y z x y z c", after);
    }

    @Test
    public void testUrlEsc() {
        String text = "a {gnarly url} c";
        context.asObject()
                .put("gnarly", "x@%()y");
        String after = processTemplate(text);
        assertEquals("a x%40%25()y c", after);
    }

    @Test
    public void testHtmlEsc() {
        String text = "a {:gnarly html} c";
        context.asObject()
                .put("gnarly", "<>\"&");
        String after = processTemplate(text);
        assertEquals("a &lt;&gt;&quot;&amp; c", after);
    }

    @Test
    public void testColonField() {
        String text = "a {:name} c";
        String after = processTemplate(text);
        assertEquals("a Jacob c", after);
    }

    @Test
    public void testUnknownParamsError() {
        String text = "a {name foo} c";
        try {
            processTemplate(text);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testExtraParamsError() {
        String text = "a {name html foo} c";
        try {
            processTemplate(text);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testIf() {
        String text = "a {.if flag}yes{.else}no{.end} c";
        context.asObject()
                .put("flag", true);
        String after = processTemplate(text);
        assertEquals("a yes c", after);
        context.asObject()
                .put("flag", false);
        after = processTemplate(text);
        assertEquals("a no c", after);
    }

    @Test
    public void testB64id() {
        String text = "a {!b64id} c";
        String after = processTemplate(text);
        assertEquals("a H2eBRN-55bZsRzM6xCdU6Q c".length(), after.length());
    }
}