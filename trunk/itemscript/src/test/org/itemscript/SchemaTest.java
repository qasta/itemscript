
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
    private JsonString longVal;
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
        this.longVal = system().createString("1");
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
        schema.validate("any", longVal);
        try {
            schema.validate("any", null);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testAnyTypes() {	 
    	JsonObject def = system().createObject();
    	def.put(".extends", "any");
        def.put(".string", "long");
        def.put(".number",	"integer");
        def.put(".string", "decimal");
        
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("2"));
        schema.validate(type, system().createNumber(1));
        schema.validate(type, system().createString("-1.565"));

        try {
        	schema.validate(type, system().createArray());
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
        	schema.validate(type, system().createNumber(1.5));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
        	schema.validate(type, system().createString("abcdefg"));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);    
    }

    @Test
    public void testArray() {
    	JsonArray emptyArray = system().createArray();
        schema.validate("array", array);
        schema.validate("array", emptyArray);
        try {
           	schema.validate("array", null);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate("array", string);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testArrayExactSize() {	    	
    	JsonObject def = system().createObject();
    	def.put(".extends", "array");
        def.put(".exactSize", 3);
        
    	JsonArray validArray = system().createArray();      
    	validArray.add("one");
    	validArray.add("two");
    	validArray.add("three");
    	
    	JsonArray invalidArray = system().createArray();      
    	invalidArray.add("one");
    	invalidArray.add("two");
    	invalidArray.add("three");
    	invalidArray.add("four");
        
        Type type = schema().resolve(def);
        schema.validate(type, validArray);
        try {
            schema.validate(type, invalidArray);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testArrayMinSize() {	    	
    	JsonObject def = system().createObject();
    	def.put(".extends", "array");
        def.put(".minSize", 2);
        
    	JsonArray validArray = system().createArray();      
    	validArray.add("one");
    	validArray.add("two");
    	
    	JsonArray invalidArray = system().createArray();      
    	invalidArray.add("one");
        
        Type type = schema().resolve(def);
        schema.validate(type, validArray);
        try {
            schema.validate(type, invalidArray);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testArrayMaxSize() {	    	
    	JsonObject def = system().createObject();
    	def.put(".extends", "array");
        def.put(".maxSize", 3);
        
    	JsonArray validArray = system().createArray();      
    	validArray.add("one");
    	validArray.add("two");
    	validArray.add("three");
    	
    	JsonArray invalidArray = system().createArray();      
    	invalidArray.add("one");
    	invalidArray.add("two");
    	invalidArray.add("three");
    	invalidArray.add("four");
        
        Type type = schema().resolve(def);
        schema.validate(type, validArray);
        try {
            schema.validate(type, invalidArray);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testArrayContainsDecimal() {	 
    	JsonObject def = system().createObject();
    	def.put(".extends", "array");
        def.put(".contains", "decimal");
        
    	JsonArray validArray = system().createArray();      
    	validArray.add("1.12");
    	validArray.add("0");
    	validArray.add("-12.0");
        Type type = schema().resolve(def);
        
        schema.validate(type, validArray);
        
    	JsonArray invalidArray = system().createArray();      
    	invalidArray.add("1");
    	invalidArray.add("2.0");
    	invalidArray.add("three");
        try {
            schema.validate(type, invalidArray);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testArrayContainsObject() {	 
    	JsonObject def = system().createObject();
    	def.put(".extends", "array");
        def.put(".contains", "object");
              
    	JsonArray validArray = system().createArray();
    	JsonObject item1 = system().createObject();
    	JsonObject item2 = system().createObject();
    	JsonObject item3 = system().createObject();
    	item2.p("bool", true);
    	item3.p("int", 1.0);
    	validArray.add(item1);
    	validArray.add(item2);
    	validArray.add(item3);
        Type type = schema().resolve(def);
        
        schema.validate(type, validArray);
    }
    
    @Test
    public void testArrayContainsNumber() {	 
    	JsonObject def = system().createObject();
    	def.put(".extends", "array");
    	
    	JsonObject numObject = system().createObject();
        def.put(".contains", numObject);
        numObject.put(".extends", "number");
        numObject.put(".greaterThan", 2);
        
        Type type = schema().resolve(def);
        
    	JsonArray validArray = system().createArray();
    	JsonNumber item1 = system().createNumber(5.0);
    	validArray.add(3);
    	validArray.add(4.0);
    	validArray.add(item1);        
        schema.validate(type, validArray);
        
        validArray.add(1);
        try {
            schema.validate(type, validArray);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testArrayContainsArray() {	 
    	JsonObject def = system().createObject();
    	def.put(".extends", "array");
        def.put(".contains", "array");
        
    	JsonArray validArray = system().createArray();
    	JsonArray array1 = system().createArray();
    	array1.add(true);
    	array1.add("string");
    	array1.add(5.02000);
    	validArray.add(array1);

        Type type = schema().resolve(def);
        
        schema.validate(type, validArray);
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
    public void testBinaryMaxBytes() {
    	
    	String hiInput = "hi";
    	byte[] hiBytes = hiInput.getBytes();
    	JsonString hiEncoded = system().createString(hiBytes);
    	
    	String worldInput = "world";
    	byte[] worldBytes = worldInput.getBytes();
    	JsonString worldEncoded = system().createString(worldBytes);
    	
        JsonObject def = system().createObject();
        def.put(".extends", "binary");
        def.put(".maxBytes", 4);
        Type type = schema().resolve(def);
		schema.validate(type, hiEncoded);
        try {
            schema.validate(type, worldEncoded);
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
        schema.validate("decimal", system().createString(" 9.0 "));
        schema.validate("decimal", system().createString("+9.0"));
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
        threwException = false;
        
        JsonString invalidDec;
        invalidDec = system().createString("9.");
        try {
            schema.validate("decimal", invalidDec);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        
        invalidDec = system().createString("98abc23");
        try {
            schema.validate("decimal", invalidDec);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        
        invalidDec = system().createString("1. 23");
        try {
            schema.validate("decimal", invalidDec);
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
        inArray.add("-2.3");
        inArray.add(".9");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("1"));
        schema.validate(type, system().createString("-2.3"));
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
            schema.validate(type, system().createString("02.3000"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testDecimalEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "decimal");
        def.put(".equalTo", "15.95");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("15.95"));
        schema.validate(type, system().createString("+15.95"));
        schema.validate(type, system().createString("15.9500"));
        schema.validate(type, system().createString("000015.95"));
        schema.validate(type, system().createString("00015.950000"));
        try {
            schema.validate(type, system().createString("1.5950"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate(type, system().createString("0015.95001200"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testDecimalZeroEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "decimal");
        def.put(".equalTo", "0");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("0.0"));
        schema.validate(type, system().createString("000.0000"));
        schema.validate(type, system().createString("000000"));
        schema.validate(type, system().createString(".0"));
        schema.validate(type, system().createString("-0"));
        schema.validate(type, system().createString("-0.0"));
        schema.validate(type, system().createString("-000.0000"));
        schema.validate(type, system().createString("-000000"));
        schema.validate(type, system().createString("-.0"));
        schema.validate(type, system().createString("+0"));
        schema.validate(type, system().createString("+0.0"));
        schema.validate(type, system().createString("+000.0000"));
        schema.validate(type, system().createString("+000000"));
        schema.validate(type, system().createString("+.0"));
        try {
            schema.validate(type, system().createString("0.000010000"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate(type, system().createString("0."));
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
        assertTrue(threwException);
        threwException = false;
        try {
        	schema.validate(type, system().createString("5"));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testDecimalLessThan() {
    	JsonObject def = system().createObject();
        def.put(".extends", "decimal");
        def.put(".lessThan", "5");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("4"));
        try {
            schema.validate(type, system().createString("9"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
        	schema.validate(type, system().createString("5"));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testDecimalGreaterThanOrEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "decimal");
        def.put(".greaterThanOrEqualTo", "5");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("9"));
        schema.validate(type, system().createString("5"));
        try {
            schema.validate(type, system().createString("4"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testDecimalLessThanOrEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "decimal");
        def.put(".lessThanOrEqualTo", "5");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("4"));
        schema.validate(type, system().createString("5"));
        try {
            schema.validate(type, system().createString("9"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testDecimalFractionDigits() {
    	JsonObject def = system().createObject();
        def.put(".extends", "decimal");
        def.put(".fractionDigits", 2);
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("4"));
        schema.validate(type, system().createString("5.12"));
        try {
            schema.validate(type, system().createString(".134"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testDecimalEven() {
    	JsonObject def = system().createObject();
        def.put(".extends", "decimal");
        def.put(".even", true);
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("4"));
        schema.validate(type, system().createString("094.000"));
        schema.validate(type, system().createString("0000"));
        try {
            schema.validate(type, system().createString(".134"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate(type, system().createString("5"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testDecimalOdd() {
    	JsonObject def = system().createObject();
        def.put(".extends", "decimal");
        def.put(".odd", false);
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("4"));
        schema.validate(type, system().createString("094.000"));
        try {
            schema.validate(type, system().createString(".134"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
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
        assertTrue(threwException);
        threwException = false;
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
        assertTrue(threwException);
        threwException = false;
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
    public void testIntegerEven() {
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
    public void testIntegerOdd() {
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
    public void testIntegerInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "integer");
        JsonArray inArray = def.createArray(".inArray");
        inArray.add(5);
        inArray.add(56.0);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(5));
        schema.validate(type, system().createNumber(56.0));
        try {
            schema.validate(type, system().createNumber(12));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testIntegerNotInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "integer");
        JsonArray notInArray = def.createArray(".notInArray");
        notInArray.add(5);
        notInArray.add(56.0);
        notInArray.add(49);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(57.0));
        try {
            schema.validate(type, system().createNumber(56));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
        	schema.validate(type, system().createNumber(49.0));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
    }
    
    @Test
    public void testLong() {
        schema.validate("long", longVal);
        schema.validate("long", system().createString("-9"));
        schema.validate("long", system().createString("9223372036854775807"));
        schema.validate("long", system().createString("-9223372036854775808"));
        try {
            schema.validate("long", null);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate("long", booleanVal);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        
        JsonString invalidDec;
        
        invalidDec = system().createString("98abc23");
        try {
            schema.validate("long", invalidDec);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        
        invalidDec = system().createString("1. 00");
        try {
            schema.validate("long", invalidDec);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        
        invalidDec = system().createString("1.");
        try {
            schema.validate("long", invalidDec);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        
        invalidDec = system().createString("9223372036854775808");
        try {
            schema.validate("long", invalidDec);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        
        invalidDec = system().createString("-9223372036854775809");
        try {
            schema.validate("long", invalidDec);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testLongInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "long");
        JsonArray inArray = def.createArray(".inArray");
        inArray.add("1");
        inArray.add("-2");
        inArray.add("00999");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("1"));
        schema.validate(type, system().createString("-2"));
        schema.validate(type, system().createString("00000999"));
        try {
            schema.validate(type, system().createString("2"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testLongNotInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "long");
        JsonArray notInArray = def.createArray(".notInArray");
        notInArray.add("1");
        notInArray.add("2");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("5"));
        schema.validate(type, system().createString("-2"));
        try {
            schema.validate(type, system().createString("02"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testLongEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "long");
        def.put(".equalTo", "1595");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("01595"));
        try {
            schema.validate(type, system().createString("15950"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testLongZeroEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "long");
        def.put(".equalTo", "0");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("-0"));
        schema.validate(type, system().createString("00"));
        try {
            schema.validate(type, system().createString("0.0000"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate(type, system().createString("0."));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testLongGreaterThan() {
    	JsonObject def = system().createObject();
        def.put(".extends", "long");
        def.put(".greaterThan", "5");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("9"));
        try {
            schema.validate(type, system().createString("4"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
        	schema.validate(type, system().createString("5"));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testLongLessThan() {
    	JsonObject def = system().createObject();
        def.put(".extends", "long");
        def.put(".lessThan", "5");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("4"));
        try {
            schema.validate(type, system().createString("9"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
        	schema.validate(type, system().createString("5"));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testLongGreaterThanOrEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "long");
        def.put(".greaterThanOrEqualTo", "5");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("9"));
        schema.validate(type, system().createString("5"));
        try {
            schema.validate(type, system().createString("4"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testLongLessThanOrEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "long");
        def.put(".lessThanOrEqualTo", "5");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("4"));
        schema.validate(type, system().createString("5"));
        try {
            schema.validate(type, system().createString("9"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testLongEven() {
    	JsonObject def = system().createObject();
        def.put(".extends", "long");
        def.put(".even", true);
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("94"));
        schema.validate(type, system().createString("0000"));
        try {
            schema.validate(type, system().createString("13"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testLongOdd() {
    	JsonObject def = system().createObject();
        def.put(".extends", "long");
        def.put(".odd", false);
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("4"));
        schema.validate(type, system().createString("094"));
        try {
            schema.validate(type, system().createString("13"));
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
        assertTrue(threwException);
        threwException = false;
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
        assertTrue(threwException);
        threwException = false;
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
    public void testNumberInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "number");
        JsonArray inArray = def.createArray(".inArray");
        inArray.add(5);
        inArray.add(-9.01);
        inArray.add(00000.5000);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(5));
        schema.validate(type, system().createNumber(-9.01));
        schema.validate(type, system().createNumber(.5));
        try {
            schema.validate(type, system().createNumber(12));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testNumberNotInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "number");
        JsonArray notInArray = def.createArray(".notInArray");
        notInArray.add(-56);
        notInArray.add(9.01);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(12));
        try {
            schema.validate(type, system().createNumber(9.01));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate(type, system().createNumber(-56));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testNumberEven() {
    	JsonObject def = system().createObject();
        def.put(".extends", "number");
        def.put(".even", true);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(4));
        schema.validate(type, system().createNumber(6.000));
        try {
            schema.validate(type, system().createNumber(5));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
        	schema.validate(type, system().createNumber(5.0123));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testNumberOdd() {
    	JsonObject def = system().createObject();
        def.put(".extends", "number");
        def.put(".odd", false);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(4));
        schema.validate(type, system().createNumber(6.000));
        try {
            schema.validate(type, system().createNumber(5));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
        	schema.validate(type, system().createNumber(5.0123));
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
        def.put(".optional phone", "string");
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
    public void testSimpleObjectTypePattern() {
        JsonObject def = system().createObject();
        def.put("name", "string");
        def.put(".optional phone", "string");
        def.put(".optional pocketChangefoo", "number");
        def.put("zipfoo", "integer");
        def.put(".pattern *foo", "integer");
        Type type = schema().resolve(def);
        JsonObject instance = system().createObject();
        instance.put("name", "Jacob");
        instance.put("zipfoo", 91234);
        instance.put("notright", "hi");

        schema.validate(type, instance);
        
        instance.put("pocketChangefoo", 56.5);
        try {
        	schema.validate(type, instance);
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testSimpleObjectTypeWildcard() {
        JsonObject def = system().createObject();
        def.put("name", "string");
        def.put(".optional phone", "string");
        def.put(".wildcard", "integer");
        Type type = schema().resolve(def);
        JsonObject instance = system().createObject();
        instance.put("name", "Jacob");
        instance.put("zip", 91234);

        schema.validate(type, instance);
        instance.put("phone", "510-123-5454");
        try {
        	schema.validate(type, instance);
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
        schema.validate(type, system().createString("abc"));
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
        schema.validate(type, system().createString("abcdefghij"));
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