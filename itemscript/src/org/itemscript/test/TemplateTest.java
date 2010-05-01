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
        return Template.create(system(), text)
                .interpretToString(context);
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
    public void testArgs() {
        String text = "a {:name substring(1)} c";
        String after = processTemplate(text);
        assertEquals("a acob c", after);
        text = "a {:name substring(1,3)} c";
        after = processTemplate(text);
        assertEquals("a ac c", after);
    }

    @Test
    public void testB64id() {
        String text = "a {b64id} c";
        String after = processTemplate(text);
        assertEquals("a H2eBRN-55bZsRzM6xCdU6Q c".length(), after.length());
    }

    @Test
    public void testBinaryInclude() {
        String text = "{@http://itemscript.org/test.png dataUrl}";
        String after = processTemplate(text);
        assertTrue(after.startsWith("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABoAAABDCAYAAACCyxXxAAAABmJ"));
    }

    @Test
    public void testBraces() {
        String text = "a {left} {right} c";
        String after = processTemplate(text);
        assertEquals("a { } c", after);
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
    public void testColonField() {
        String text = "a {:name} c";
        String after = processTemplate(text);
        assertEquals("a Jacob c", after);
    }

    @Test
    public void testComment() {
        String text = "a {#comment some junk here more junk } c";
        String after = processTemplate(text);
        assertEquals("a  c", after);
    }

    @Test
    public void testContentsTrim() {
        String text = "a { left } c";
        String after = processTemplate(text);
        assertEquals("a { c", after);
    }

    @Test
    public void testEmptyField() {
        context.asObject()
                .put("y", "x");
        String text = "{.section :y}a {:} b{.end}";
        String after = processTemplate(text);
        assertEquals("a x b", after);
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
    public void testForeach() {
        String text = "a {.foreach :items}{:}{.end} c";
        JsonArray array = context.asObject()
                .createArray("items");
        array.add("x");
        array.add("y");
        array.add("z");
        String after = processTemplate(text);
        assertEquals("a xyz c", after);
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
    public void testHtmlEsc() {
        String text = "a {:gnarly html} c";
        context.asObject()
                .put("gnarly", "<>\"&");
        String after = processTemplate(text);
        assertEquals("a &lt;&gt;&quot;&amp; c", after);
    }

    @Test
    public void testHttpForeach() {
        String text = "a {.foreach @http://itemscript.org/test.json#test-object/foo}{:}{.end} c";
        String after = processTemplate(text);
        assertEquals("a xyz c", after);
    }

    @Test
    public void testHttpReference() {
        String text = "a {@http://itemscript.org/test.json#test-object/abc} c";
        String after = processTemplate(text);
        assertEquals("a def c", after);
    }

    @Test
    public void testIf() {
        String text = "a {.if :flag}yes{.else}no{.end} c";
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
    public void testJoin() {
        String text = "a {.foreach :items}{:}{.join}{:name}{.end} c";
        JsonArray array = context.asObject()
                .createArray("items");
        array.add("x");
        array.add("y");
        array.add("z");
        String after = processTemplate(text);
        assertEquals("a xJacobyJacobz c", after);
    }

    @Test
    public void testLiteral() {
        String text = "a {&b%3D%7B%25} c";
        String after = Template.create(system(), text)
                .interpretToString(context());
        assertEquals("a b={% c", after);
    }

    public void testLiteralInObjectTemplate() {
        JsonArray literalArray = system().createArray()
                .a("foo")
                .a("bar");
        JsonObject templateObject = system().createObject();
        templateObject.put("&array", literalArray);
        Template t = Template.create(system(), templateObject);
        JsonValue val = t.interpretToValue(context);
        assertEquals("bar", val.asObject()
                .getArray("array")
                .getString(1));
    }

    @Test
    public void testLiteralTemplates() {
        Template t = Template.create(system(), system().createBoolean(true));
        JsonValue val = t.interpretToValue(context);
        assertTrue(val.isBoolean());
        assertEquals(true, (boolean) val.booleanValue());
        t = Template.create(system(), system().createNumber(1.5));
        val = t.interpretToValue(context);
        assertTrue(val.isNumber());
        assertEquals(1.5, (double) val.doubleValue());
        t = Template.create(system(), system().createNull());
        val = t.interpretToValue(context);
        assertTrue(val.isNull());
    }

    @Test
    public void testMissingEnd() {
        String text = "a {.section :address} c";
        try {
            processTemplate(text);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testNestedForeach() {
        String text = "a {.foreach :items}{.foreach :subItems}{:}{.join} {.end}{.join} {.end} c";
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
    public void testNumericLiteral() {
        String text = "a {1} c";
        String after = processTemplate(text);
        assertEquals("a 1 c", after);
    }

    @Test
    public void testObjectTemplate() {
        context.asObject()
                .put("foo", "bar");
        context.asObject()
                .put("number", 1);
        context.asObject()
                .createArray("array")
                .a("foo")
                .a("bar");
        JsonObject templateObj = system().createObject()
                .p("a", "{:foo}")
                .p("b", "{:number}")
                .p("c", "{:array}");
        Template t = Template.create(system(), templateObj);
        JsonValue val = t.interpretToValue(context);
        assertTrue(val.isObject());
        assertEquals("bar", val.asObject()
                .getString("a"));
        assertEquals((double) 1, (double) val.asObject()
                .getDouble("b"));
        assertEquals("foo", val.asObject()
                .getArray("c")
                .getString(0));
    }

    @Test
    public void testOrSection() {
        String text = "a {.section :address}{:street}{.or}kittens{.end} c";
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
    public void testOverlappingBraceError() {
        String text = "a { 'f'{a }bc";
        try {
            processTemplate(text);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testQuotedString() {
        String text = "a {'b'} c";
        String after = processTemplate(text);
        assertEquals("a b c", after);
    }

    @Test
    public void testReturnObject() {
        system().put("mem:/test/foo", system().createObject()
                .p("foo", "bar"));
        String text = "{@mem:/test/foo}";
        Template t = Template.create(system(), text);
        JsonValue val = t.interpretToValue(context);
        assertTrue(val.isObject());
        assertEquals("bar", val.asObject()
                .getString("foo"));
    }

    @Test
    public void testSection() {
        String text = "a {.section :address}{:street}{.end} c";
        JsonObject address = context.asObject()
                .createObject("address");
        address.put("street", "xyz");
        String after = processTemplate(text);
        assertEquals("a xyz c", after);
    }

    @Test
    public void testStringContext() {
        String text = "a {:} c";
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
    public void testUrlEsc() {
        String text = "a {:gnarly url} c";
        context.asObject()
                .put("gnarly", "x@%()y");
        String after = processTemplate(text);
        assertEquals("a x%40%25()y c", after);
    }

    @Test
    public void testUuid() {
        String text = "a {uuid} c";
        String after = processTemplate(text);
        assertEquals("a 3d9f9533-c32f-4183-b78c-f01f32609de4 c".length(), after.length());
    }
}