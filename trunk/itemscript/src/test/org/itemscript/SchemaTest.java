
package test.org.itemscript;

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonBoolean;
import org.itemscript.core.values.JsonNull;
import org.itemscript.core.values.JsonNumber;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonString;
import org.itemscript.schema.Schema;
import org.itemscript.schema.Type;
import org.junit.Test;

public class SchemaTest extends ItemscriptTestBase {
    private Schema schema;
    private JsonString string;
    private JsonBoolean booleanVal;
    private JsonNumber number;
    private JsonNull nullVal;
    private JsonNumber intVal;
    private JsonString emptyString;
    private JsonObject object;
    private JsonArray array;
    private JsonString binary;
    private JsonString decVal;
    private boolean threwException;

    public Schema schema() {
        return schema;
    }

    @Override
    protected void setUp() {
        super.setUp();
        this.schema = new Schema(system());
        this.string = system().createString("string");
        this.booleanVal = system().createBoolean(true);
        this.number = system().createNumber(0.5);
        this.nullVal = system().createNull();
        this.intVal = system().createNumber(1);
        this.emptyString = system().createString("");
        this.object = system().createObject()
                .p("key", "value");
        this.array = system().createArray()
                .a("value");
        this.binary = system().get("classpath:test/org/itemscript/test.png")
                .asString();
        this.decVal = system().createString("1.0");
        this.threwException = false;
    }

    @Test
    public void testAny() {
        schema.validate("any", string);
        schema.validate("any", booleanVal);
        schema.validate("any", number);
        schema.validate("any", nullVal);
        schema.validate("any", intVal);
        schema.validate("any", emptyString);
        schema.validate("any", object);
        schema.validate("any", array);
        schema.validate("any", decVal);
        try {
            schema.validate("any", null);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testArray() {
        schema.validate("array", array);
        try {
            schema.validate("array", string);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testBinary() {
        schema.validate("binary", binary);
        try {
            schema.validate("binary", string);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testBoolean() {
        schema.validate("boolean", booleanVal);
        try {
            schema.validate("boolean", string);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testBooleanHasBooleanValue() {
        JsonObject def = system().createObject();
        def.put(".extends", "boolean");
        def.put(".booleanValue", true);
        Type type = schema().resolve(def);
        schema.validate(type, system().createBoolean(true));
        try {
            schema.validate(type, system().createBoolean(false));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testBooleanHasNoBooleanValue() {
        JsonObject def = system().createObject();
        def.put(".extends", "boolean");
        Type type = schema().resolve(def);
        schema.validate(type, system().createBoolean(true));
        schema.validate(type, system().createBoolean(false));
    }
    
    @Test
    public void testDecimal() {
        schema.validate("decimal", decVal);
        try {
            schema.validate("decimal", null);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate("decimal", booleanVal);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testDecimalInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "decimal");
        JsonArray inArray = def.createArray(".inArray");
        inArray.add("1");
        inArray.add("2.3");
        inArray.add(".9");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("1"));
        schema.validate(type, system().createString("2.3"));
        schema.validate(type, system().createString("000000.9"));
        try {
            schema.validate(type, system().createString("00.6"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testDecimalNotInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "decimal");
        JsonArray notInArray = def.createArray(".notInArray");
        notInArray.add("1");
        notInArray.add("2.3");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("5"));
        schema.validate(type, system().createString("-2.3"));
        try {
            schema.validate(type, system().createString("2.3"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testDecimalGreaterThan() {
    	JsonObject def = system().createObject();
        def.put(".extends", "decimal");
        def.put(".greaterThan", "5");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("9"));
        try {
            schema.validate(type, system().createString("4"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        try {
        	schema.validate(type, system().createString("5"));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testInteger() {
        schema.validate("integer", intVal);
        try {
            schema.validate("integer", number);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testIntegerGreaterThan() {
    	JsonObject def = system().createObject();
        def.put(".extends", "integer");
        def.put(".greaterThan", 5);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(6));
        try {
            schema.validate(type, system().createNumber(4));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        try {
        	schema.validate(type, system().createNumber(5));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testIntegerLessThan() {
    	JsonObject def = system().createObject();
        def.put(".extends", "integer");
        def.put(".lessThan", 5);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(4));
        try {
            schema.validate(type, system().createNumber(6));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        try {
        	schema.validate(type, system().createNumber(5));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testIntegerGreaterThanOrEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "integer");
        def.put(".greaterThanOrEqualTo", 5);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(5));
        schema.validate(type, system().createNumber(6));
        try {
            schema.validate(type, system().createNumber(4));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testIntegerLessThanOrEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "integer");
        def.put(".lessThanOrEqualTo", 5);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(5));
        schema.validate(type, system().createNumber(4));
        try {
            schema.validate(type, system().createNumber(6));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testIntegerEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "integer");
        def.put(".equalTo", -5);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(-5));
        try {
            schema.validate(type, system().createNumber(4));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testIntegerEvenTrue() {
    	JsonObject def = system().createObject();
        def.put(".extends", "integer");
        def.put(".even", true);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(4));
        try {
            schema.validate(type, system().createNumber(5));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testIntegerEvenFalse() {
    	JsonObject def = system().createObject();
        def.put(".extends", "integer");
        def.put(".even", false);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(-5));
        try {
            schema.validate(type, system().createNumber(4));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testIntegerOddTrue() {
    	JsonObject def = system().createObject();
        def.put(".extends", "integer");
        def.put(".odd", true);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(-5));
        try {
            schema.validate(type, system().createNumber(4));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testIntegerOddFalse() {
    	JsonObject def = system().createObject();
        def.put(".extends", "integer");
        def.put(".odd", false);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(4));
        try {
            schema.validate(type, system().createNumber(-5));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testNull() {
        schema.validate("null", nullVal);
        try {
            schema.validate("null", string);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testNumber() {
        schema.validate("number", number);
        try {
            schema.validate("number", null);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate("number", booleanVal);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testNumberGreaterThan() {
    	JsonObject def = system().createObject();
        def.put(".extends", "number");
        def.put(".greaterThan", 5);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(5.1));
        try {
            schema.validate(type, system().createNumber(4));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        try {
        	schema.validate(type, system().createNumber(5));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testNumberLessThan() {
    	JsonObject def = system().createObject();
        def.put(".extends", "number");
        def.put(".lessThan", 5);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(4.9));
        try {
            schema.validate(type, system().createNumber(5.1));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        try {
        	schema.validate(type, system().createNumber(5));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testNumberGreaterThanOrEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "number");
        def.put(".greaterThanOrEqualTo", 5);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(5));
        schema.validate(type, system().createNumber(5.1));
        try {
            schema.validate(type, system().createNumber(4.9));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testNumberLessThanOrEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "number");
        def.put(".lessThanOrEqualTo", 5);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(5));
        schema.validate(type, system().createNumber(4.9));
        try {
            schema.validate(type, system().createNumber(5.1));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testNumberEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "number");
        def.put(".equalTo", -5.789);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(-5.789));
        try {
            schema.validate(type, system().createNumber(4.9));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testObject() {
        schema.validate("object", object);
        try {
            schema.validate("object", string);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testSimpleObjectType() {
        JsonObject def = system().createObject();
        def.put("name", "string");
        def.createObject("address");
        def.put("OPTIONAL phone", "string");
        Type type = schema().resolve(def);
        JsonObject instance = system().createObject();
        instance.put("name", "Jacob");
        instance.createObject("address");
        schema.validate(type, instance);
        try {
            schema.validate(type, object);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testSinglePattern() {
        JsonObject def = system().createObject();
        def.put(".extends", "string");
        def.put(".pattern", "xyz*");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("xyz"));
        schema.validate(type, system().createString("xyz 123"));
        try {
            schema.validate(type, system().createString("abc 123"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testString() {
        schema.validate("string", string);
        try {
            schema.validate("string", null);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate("string", booleanVal);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testStringMax() {
        JsonObject def = system().createObject();
        def.put(".extends", "string");
        def.put(".maxLength", 3);
        Type type = schema().resolve(def);
        try {
            schema.validate(type, system().createString("xyzijoij"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testStringMin() {
        JsonObject def = system().createObject();
        def.put(".extends", "string");
        def.put(".minLength", 10);
        Type type = schema().resolve(def);
        try {
            schema.validate(type, system().createString("xyz"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testStringIs() {
    	JsonObject def = system().createObject();
    	def.put(".extends", "string");
    	def.put(".isLength", 5);
    	Type type = schema().resolve(def);
    	schema.validate(type, system().createString("abcde"));
    	try {
    		schema.validate(type, system().createString("four"));
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    }
    
    @Test
    public void testStringEquals() {
    	JsonObject def = system().createObject();
    	def.put(".extends", "string");
    	def.put(".equals", "flower");
    	Type type = schema().resolve(def);
    	schema.validate(type, system().createString("flower"));
    	try {
    		schema.validate(type, system().createString("flour"));
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    }
    
    @Test
    public void testStringRegExPattern() {
    	JsonObject def = system().createObject();
    	def.put(".extends", "string");
    	def.put(".regExPattern", "[a-z]+");
    	Type type = schema().resolve(def);
    	schema.validate(type, system().createString("abc"));
    	try {
    		schema.validate(type, system().createString("a3d"));
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    }
    
    @Test
    public void testStringInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "string");
        JsonArray inArray = def.createArray(".inArray");
        inArray.add("xyz");
        inArray.add("abc");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("xyz"));
        schema.validate(type, system().createString("abc"));
        try {
            schema.validate(type, system().createString("abc 123"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testStringNotInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "string");
        JsonArray notInArray = def.createArray(".notInArray");
        notInArray.add("xyz");
        notInArray.add("abc");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("abc 123"));
        schema.validate(type, system().createString("xy"));
        try {
            schema.validate(type, system().createString("abc"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testStringPattern() {
        JsonObject def = system().createObject();
        def.put(".extends", "string");
        JsonArray pattern = def.createArray(".pattern");
        pattern.add("xyz*");
        pattern.add("*abc");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("xyz"));
        schema.validate(type, system().createString("xyz 123"));
        schema.validate(type, system().createString("hey hey hey abc"));
        try {
            schema.validate(type, system().createString("abc 123"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    public void testWrongTypeForKey() {
        JsonObject def = system().createObject();
        def.put("name", "string");
        def.put("phone", "string");
        Type type = schema().resolve(def);
        JsonObject instance = system().createObject();
        instance.put("name", "Jacob");
        instance.put("phone", 123);
        try {
            schema.validate(type, instance);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testWrongTypeForOptionalKey() {
        JsonObject def = system().createObject();
        def.put("name", "string");
        def.put(".optional phone", "string");
        Type type = schema().resolve(def);
        JsonObject instance = system().createObject();
        instance.put("name", "Jacob");
        instance.put("phone", 123);
        try {
            schema.validate(type, instance);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
}