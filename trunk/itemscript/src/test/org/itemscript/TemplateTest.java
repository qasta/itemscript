/*
 * Copyright � 2010, Data Base Architects, Inc. All rights reserved.
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
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;
import org.itemscript.schema.Schema;
import org.itemscript.schema.ValidateAsBooleanFunction;
import org.itemscript.schema.ValidateFunction;
import org.itemscript.schema.ValidateAsNumberFunction;
import org.itemscript.template.Template;
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

    private JsonValue processTemplateToValue(String text) {
        return Template.create(system(), text)
                .interpretToValue(context);
    }

    @Override
    protected void setUp() {
        super.setUp();
        ValidateFunction.init();
        ValidateAsNumberFunction.init();
        ValidateAsBooleanFunction.init();
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
        String text = "a { '{' } c";
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

    @Test
    public void testFunctionOr() {
        String text = "{:x or(:y)}";
        context.asObject()
                .put("x", true);
        context.asObject()
                .put("y", false);
        JsonValue val = processTemplateToValue(text);
        assertTrue(val.booleanValue());
        context.asObject()
                .put("x", false);
        val = processTemplateToValue(text);
        assertFalse(val.booleanValue());
    }

    @Test
    public void testFunctionAnd() {
        String text = "{:x and(:y)}";
        context.asObject()
                .put("x", true);
        context.asObject()
                .put("y", false);
        JsonValue val = processTemplateToValue(text);
        assertFalse(val.booleanValue());
        context.asObject()
                .put("y", true);
        val = processTemplateToValue(text);
        assertTrue(val.booleanValue());
    }

    @Test
    public void testFunctionEquals() {
        String text = "{:x equals(:y)}";
        context.asObject()
                .put("x", "a string");
        context.asObject()
                .put("y", "a string");
        JsonValue val = processTemplateToValue(text);
        assertTrue(val.booleanValue());
        context.asObject()
                .put("y", "some other string");
        val = processTemplateToValue(text);
        assertFalse(val.booleanValue());
    }
    
    @Test
    public void testFunctionValidateStringType() {
    	String text = "{:x validate('string')}";
    	context.asObject()
    			.put("x", "Bob");
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("x", 123);
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    @Test
    public void testFunctionValidateDecimalType() {
    	String text = "{:x validate('decimal')}";
    	context.asObject()
    			.put("x", "-12.5");
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("x", 123);
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    @Test
    public void testFunctionValidateLongType() {
    	String text = "{:x validate('long')}";
    	context.asObject()
    			.put("x", "1990");
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("x", 123);
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    @Test
    public void testFunctionValidateBinaryType() {
    	String text = "{:x validate('binary')}";
    	String hiInput = "hi";
    	byte[] hiBytes = hiInput.getBytes();
    	JsonString hiEncoded = system().createString(hiBytes);
    	context.asObject()
    			.put("x", hiEncoded);
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("x", "al!@");
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    @Test
    public void testFunctionValidateNumberType() {
    	String text = "{:x validate('number')}";
    	context.asObject()
    			.put("x", 1990.1);
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("x", true);
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    @Test
    public void testFunctionValidateIntegerType() {
    	String text = "{:x validate('integer')}";
    	context.asObject()
    			.put("x", 1990);
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("x", 1990.1);
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    @Test
    public void testFunctionValidateArrayType() {
    	String text = "{:x validate('array')}";
    	context.asObject()
    			.put("x", system().createArray());
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("x", true);
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    @Test
    public void testFunctionValidateNullType() {
    	String text = "{:x validate('null')}";
    	context.asObject()
    			.put("x", system().createNull());
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("x", true);
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    @Test
    public void testFunctionValidateBooleanType() {
    	String text = "{:x validate('boolean')}";
    	context.asObject()
    			.put("x", true);
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("x", 123);
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    @Test
    public void testFunctionValidateObjectType() {
    	String text = "{:x validate('object')}";
    	context.asObject()
    			.put("x", system().createObject());
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("x", 123);
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
   
    @Test
    public void testFunctionValidateUrl() {
    	JsonObject phoneDef = system().createObject();
    	phoneDef.put(".extends", "string");
    	phoneDef.put(".isLength", 10);
    	phoneDef.put(".regExPattern", "[0-9]+");
    	
        system().put("mem:/TemplateTest/type/phone", phoneDef);
        
    	String text = "{:x validate(@mem:/TemplateTest/type/phone)}";
    	context.asObject()
    			.put("x", "5105551234");

    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("x", "510555123");
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    
    @Test
    public void testFunctionValidateFieldRef() {
    	String text = "{:x validate(:y)}";
    	context.asObject()
    			.put("x", 123);
    	context.asObject()
    			.put("y", "number");
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("y", "string");
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    @Test
    public void testFunctionValidateSimpleDef() {
    	String text = "{:x validate(:y)}";
    	context.asObject()
    			.put("x", "Bob");
    	JsonObject objDef = system().createObject();
    	objDef.put(".extends", "string");
    	objDef.put(".minLength", 2);
    	context.asObject()
    			.put("y", objDef);
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject()
    			.put("x", "A");
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    @Test
    public void testFunctionValidateAdd() {
    	Schema schema = new Schema(system());
    	JsonObject schemaDef = system().createObject();
    	schemaDef.put("name", "string");
    	schemaDef.put("address", "object");
    	schema.addAllTypes(schemaDef);
    	String text = "{:x validate(:y)}";
    	
    	JsonObject objDef = system().createObject();
    	objDef.put(".extends", "address");
    	objDef.put("streetName", "string");
    	objDef.put("zipcode", "integer");
    	objDef.put(".optional city", "string");
    	context.asObject()
    			.put("y", objDef);    	

    	JsonObject objInst = system().createObject();
    	objInst.put("streetName", "First Ave");
    	objInst.put("zipcode", 91234);
    	context.asObject()
    			.put("x", objInst);
    	
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	objInst.put("city", true);
    	context.asObject().remove("x");
    	context.asObject()
    			.put("x", objInst);
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }
    
    @Test
    public void testFunctionValidateAddWithDef() {
    	Schema schema = new Schema(system());
    	JsonObject schemaDef = system().createObject();
    	schemaDef.put("name", "string");
    	JsonObject addressDef = system().createObject();
    	addressDef.put(".extends", "object");
    	addressDef.put("streetName", "string");
    	addressDef.put("zipcode", "integer");
    	addressDef.put(".optional city", "string");
    	schemaDef.put("address", addressDef);
    	schema.addAllTypes(schemaDef);
    	String text = "{:x validate(:y)}";  	
    	context.asObject()
				.put("y", "address");
    	
    	JsonObject objInst = system().createObject();
    	objInst.put("streetName", "First Ave");
    	objInst.put("zipcode", 91234);
    	context.asObject()
    			.put("x", objInst);
    	
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	objInst.put("city", true);
    	context.asObject().remove("x");
    	context.asObject()
    			.put("x", objInst);
    	val = processTemplateToValue(text);
    	assertFalse(val.asObject().get("valid").booleanValue());
    }

    @Test
    public void testSetErrorMessageAny() {
    	String text = "{:x validate(:y)}";
    	JsonObject anyDef = system().createObject();
    	anyDef.put(".extends", "any");
    	anyDef.put(".string", "decimal");
    	context.asObject()
    			.put("x", 123);
    	context.asObject()
    			.put("y", anyDef);
    	JsonValue val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Your value '123' was not one of the types specified.");
	}
    
    @Test
    public void testSetErrorMessageArraySize() {
    	String text = "{:x validate(:y)}";
    	JsonObject anyDef = system().createObject();
    	anyDef.put(".extends", "array");
    	anyDef.put(".exactSize", 1);
    	context.asObject()
    			.put("x", system().createArray());
    	context.asObject()
    			.put("y", anyDef);
    	JsonValue val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Your array has the wrong number of items. Your size is 0. The correct size is 1.");
	}

    @Test
    public void testSetErrorMessagesEmptyKey() {
    	String text = "{:x validate(:y)}";
    	JsonObject schemaDef = system().createObject();
    	schemaDef.put("name", "string");
    	JsonObject inst = system().createObject();
    	inst.put("name", "Bob");
    	inst.put("", "foo");
    	context.asObject()
    			.put("x", inst);
    	context.asObject()
    			.put("y", schemaDef);
    	JsonValue val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Cannot have an empty key in your instance object.");    	
    }    
    
    @Test
    public void testSetErrorMessageFractionDigits() {
    	String text = "{:x validate(:y)}";
    	JsonObject decDef = system().createObject();
    	decDef.put(".extends", "decimal");
    	decDef.put(".fractionDigits", 2);
    	context.asObject()
    			.put("x", "12.223");
    	context.asObject()
    			.put("y", decDef);
    	JsonValue val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Your value '12.223' has 3 fractional digits. The maximum number of fractional digits is 2.");
	}
    
    @Test
    public void testSetErrorMessagesInArray() {
    	String text = "{:x validate(:y)}";
    	JsonObject intDef = system().createObject();
    	intDef.put(".extends", "integer");
    	JsonArray inArray = system().createArray();
    	inArray.add(123);
    	intDef.put(".inArray", inArray);
    	context.asObject()
    			.put("x", 125);
    	context.asObject()
    			.put("y", intDef);
    	JsonValue val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Your value '125' is not allowed.");
    }
    
    @Test
    public void testSetErrorMessageInteger() {
    	String text = "{:x validate(:y)}";
    	JsonObject anyDef = system().createObject();
    	anyDef.put(".extends", "integer");
    	anyDef.put(".even", true);
    	context.asObject()
    			.put("x", "abc");
    	context.asObject()
    			.put("y", anyDef);
    	JsonValue val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Value '\"abc\"' was not a number.");
	}

    @Test
    public void testSetErrorMessageMissingValue() {
    	String text = "{:x validate(:y)}";
    	JsonObject schemaDef = system().createObject();
    	schemaDef.put("foo", "string");
    	JsonObject inst = system().createObject();
    	context.asObject()
    			.put("x", inst);
    	context.asObject()
    			.put("y", schemaDef);
    	JsonValue val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Missing value for key: foo.");
    }
    
    @Test
    public void testSetErrorMessageNull() {
    	String text = "{:x validate(:y)}";
    	context.asObject()
    			.put("x", 123);
    	context.asObject()
    			.put("y", "null");
    	JsonValue val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Value '123' was not null.");
    }
    
    @Test
    public void testSetErrorMessageStringLength() {
    	String text = "{:x validate(:y)}";
    	JsonObject stringDef = system().createObject();
    	stringDef.put(".extends", "string");
    	stringDef.put(".isLength", 5);
    	context.asObject()
    			.put("x", "123");
    	context.asObject()
    			.put("y", stringDef);
    	JsonValue val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Your value is the wrong length. Your value is 3 characters long. The correct length is 5 characters.");
    }
    
    @Test
    public void testFunctionValidateAsNumberSimple() {
    	String text = "{:x validateAsNumber('number')}";
    	context.asObject()
    		.put("x", 123);
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject().remove("x");
    	context.asObject()
    			.put("x", "+123");
    	val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject().remove("x");
    	context.asObject()
    			.put("x", "abc123");
    	val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Your value '\"abc123\"' does not represent a number.");
    }
    
    @Test
    public void testFunctionValidateAsNumberDef() {
    	String text = "{:x validateAsNumber(:y)}";
    	JsonObject def = system().createObject();
    	def.put(".extends", "integer");
    	context.asObject()
    		.put("y", def);
    	context.asObject()
    		.put("x", 123);
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject().remove("x");
    	context.asObject()
    			.put("x", "-123");
    	val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject().remove("x");
    	context.asObject()
    			.put("x", "1.2");
    	val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Value '1.2' was not an integer.");
    }
    
    @Test
    public void testFunctionValidateAsNumberWrongType() {
    	String text = "{:x validateAsNumber('string')}";
    	context.asObject()
    		.put("x", 123);
    	try {
    		processTemplateToValue(text);
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    }
    
    @Test
    public void testFunctionValidateAsBooleanSimple() {
    	String text = "{:x validateAsBoolean('boolean')}";
    	context.asObject()
    		.put("x", true);
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject().remove("x");
    	context.asObject()
    			.put("x", "false");
    	val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject().remove("x");
    	context.asObject()
    			.put("x", "abc123");
    	val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Your value '\"abc123\"' does not represent a boolean.");
    }
    
    @Test
    public void testFunctionValidateAsBooleanDef() {
    	String text = "{:x validateAsBoolean(:y)}";
    	JsonObject def = system().createObject();
    	def.put(".extends", "boolean");
    	def.put(".booleanValue", true);
    	context.asObject()
    		.put("y", def);
    	context.asObject()
    		.put("x", true);
    	JsonValue val = processTemplateToValue(text);
    	assertTrue(val.asObject().get("valid").booleanValue());
    	context.asObject().remove("x")	;
    	context.asObject()
    			.put("x", false);
    	val = processTemplateToValue(text);
    	String error = val.asObject().getString("message");
    	assertEquals(error, "Your value does not match the required boolean value.");
    }
    
    @Test
    public void testFunctionValidateAsBooleanWrongType() {
    	String text = "{:x validateAsBoolean('string')}";
    	context.asObject()
    		.put("x", "true");
    	try {
    		processTemplateToValue(text);
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    }
  
    @Test
    public void testFunctionSubstring() {
        String text = "{'abcdef' substring(1)}";
        String after = processTemplate(text);
        assertEquals("bcdef", after);
        text = "{'abcdef' substring(1, 5)}";
        after = processTemplate(text);
        assertEquals("bcde", after);
    }

    @Test
    public void testQuotedBraces() {
        String after = processTemplate("{'{'}");
        assertEquals("{", after);
        after = processTemplate("{'}'}");
        assertEquals("}", after);
    }

    @Test
    public void testConstant() {
        String text = "a {*Test.PI} c";
        system().setConstant("Test.PI", 3 + "");
        String after = processTemplate(text);
        assertEquals("a 3 c", after);
    }    
}