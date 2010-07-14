
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
    private JsonArray array;
    private JsonString binary;
    private JsonBoolean booleanVal;
    private JsonString decVal;
    private JsonString emptyString;
    private JsonNumber intVal;
    private JsonString longVal;
    private JsonNull nullVal;
    private JsonNumber number;
    private JsonObject object;
    private JsonString string;
    private boolean threwException;

    public Schema schema() {
        return schema;
    }

    @Override
    protected void setUp() {
        super.setUp();
        this.schema = new Schema(system());
        this.array = system().createArray()
        	.a("value");
        this.binary = system().get("classpath:test/org/itemscript/test.png")
        	.asString();
        this.booleanVal = system().createBoolean(true);
        this.decVal = system().createString("1.0");
        this.emptyString = system().createString("");
        this.intVal = system().createNumber(1);
        this.longVal = system().createString("1");
        this.nullVal = system().createNull();
        this.number = system().createNumber(0.5);
        this.object = system().createObject()
                .p("key", "value");
        this.string = system().createString("string");
        this.threwException = false;
    }

    @Test
    public void testAny() {
        schema.validate("any", array);
        schema.validate("any", binary);
        schema.validate("any", booleanVal);
        schema.validate("any", decVal);
        schema.validate("any", emptyString);
        schema.validate("any", intVal);
        schema.validate("any", longVal);
        schema.validate("any", nullVal);
        schema.validate("any", number);
        schema.validate("any", object);
        schema.validate("any", string);
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
        def.put(".number",	"integer");
        def.put(".string", "long");
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
    public void testAnyTypeSpecs() {	 
    	JsonObject def = system().createObject();
    	def.put(".extends", "any");
    	JsonObject intObj = system().createObject();
    	intObj.p(".extends", "integer");
        def.put(".number",	intObj);
        
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(1));

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
    public void testAnyInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "any");
        JsonArray inArray = def.createArray(".inArray");
        
        JsonArray arrayObject = system().createArray();
        arrayObject.add("5");
        inArray.add(arrayObject);
        
    	String hiInput = "hi";
    	byte[] hiBytes = hiInput.getBytes();
    	JsonString hiEncoded = system().createString(hiBytes);
    	inArray.add(hiEncoded);
    	
    	JsonObject objectObject = system().createObject();
        objectObject.put(".wildcard", "integer");
        inArray.add(objectObject);
        
        JsonObject objectClone = system().createObject();
        objectClone.put(".wildcard", "integer");
        
        JsonObject badObjectClone = system().createObject();
        badObjectClone.put(".wildcard", "integer");
        badObjectClone.put(".key", "foo");
        
        inArray.add(true);
        inArray.add("5.0");
        inArray.add(7);
        inArray.add("1");
        inArray.add(12.2);
        inArray.add("abc");       

        Type type = schema().resolve(def);
        schema.validate(type, arrayObject);
        schema.validate(type, hiEncoded);
        schema.validate(type, objectClone);
        schema.validate(type, system().createBoolean(true));
        schema.validate(type, system().createString("5"));
        schema.validate(type, system().createNumber(7.0));
        schema.validate(type, system().createString("1"));
        schema.validate(type, system().createNumber(12.2));
        schema.validate(type, system().createString("abc"));

        try {
            schema.validate(type, system().createString("7.0"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate(type, badObjectClone);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testAnyNotInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "any");
        JsonArray notInArray = def.createArray(".notInArray");
        
    	JsonObject badObject = system().createObject();
        badObject.put(".wildcard", "integer");
        notInArray.add(badObject);
        notInArray.add(true);
        
        JsonArray arrayObject = system().createArray();
        arrayObject.add("5");        
    	String hiInput = "hi";
    	byte[] hiBytes = hiInput.getBytes();
    	JsonString hiEncoded = system().createString(hiBytes);    	
        Type type = schema().resolve(def);
        schema.validate(type, arrayObject);
        schema.validate(type, hiEncoded);
        schema.validate(type, system().createBoolean(false));
        schema.validate(type, system().createString("5"));

        try {
            schema.validate(type, system().createBoolean(true));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate(type, badObject);
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
    public void testArrayContainsDecimal() {	 
    	JsonObject def = system().createObject();
    	def.put(".extends", "array");
        def.put(".contains", "decimal");
        
    	JsonArray array = system().createArray();      
    	array.add("1.12");
    	array.add("0");
    	array.add("-12.0");
        Type type = schema().resolve(def);
        
        schema.validate(type, array);
        array.add("three");
        try {
            schema.validate(type, array);
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
        
    	JsonArray mainArray = system().createArray();
    	JsonArray array1 = system().createArray();
    	JsonArray array2 = system().createArray();
    	array1.add(true);
    	array1.add("string");
    	array2.add(5.02000);
    	mainArray.add(array1);
    	mainArray.add(array2);

        Type type = schema().resolve(def);
        schema.validate(type, mainArray);
    }
    
    @Test
    public void testArrayContainsObject() {	 
    	JsonObject def = system().createObject();
    	def.put(".extends", "array");
        def.put(".contains", "object");
              
    	JsonArray array = system().createArray();
    	JsonObject item1 = system().createObject();
    	JsonObject item2 = system().createObject();
    	JsonObject item3 = system().createObject();
    	item2.p("bool", true);
    	item3.p("int", 1.0);
    	array.add(item1);
    	array.add(item2);
    	array.add(item3);
        Type type = schema().resolve(def);
        
        schema.validate(type, array);
    }
    
    @Test
    public void testArrayContainsNumberObject() {	 
    	JsonObject def = system().createObject();
    	def.put(".extends", "array");
    	
    	JsonObject numObject = system().createObject();
        def.put(".contains", numObject);
        numObject.put(".extends", "number");
        numObject.put(".greaterThan", 2);
        
        Type type = schema().resolve(def);
    	JsonArray array = system().createArray();
    	array.add(5); 
        schema.validate(type, array);
        
        array.add(1);
        try {
            schema.validate(type, array);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testArrayInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "array");
        JsonArray inArray = def.createArray(".inArray");
        JsonArray array1 = system().createArray();
        array1.add("one");
        array1.add("two");
        JsonArray array2 = system().createArray();
        array2.add(1);
        array2.add(2);
        JsonArray array1Clone = system().createArray();
        array1Clone.add("one");
        array1Clone.add("two");
        inArray.add(array1);
        Type type = schema().resolve(def);
        schema.validate(type, array1);
        schema.validate(type, array1Clone);
        try {
            schema.validate(type, array2);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testArrayNotInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "array");
        JsonArray notInArray = def.createArray(".notInArray");
        JsonArray array1 = system().createArray();
        array1.add("one");
        array1.add("two");
        JsonArray array2 = system().createArray();
        array2.add(1);
        array2.add(2);
        JsonArray array1Clone = system().createArray();
        array1Clone.add("one");
        array1Clone.add("two");
        notInArray.add(array1);
        Type type = schema().resolve(def);
        schema.validate(type, array2);
        try {
            schema.validate(type, array1);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate(type, array1Clone);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testBinary() {
        schema.validate("binary", binary);
        try {
            schema.validate("binary", number);
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
        schema.validate(type, system().createNumber(56));
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
        notInArray.add(49);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(57.0));
        try {
        	schema.validate(type, system().createNumber(49.0));
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
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
        
        invalidDec = system().createString("1 ");
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
    public void testNumberEqualTo() {
    	JsonObject def = system().createObject();
        def.put(".extends", "number");
        def.put(".equalTo", -5.789);
        Type type = schema().resolve(def);
        schema.validate(type, system().createNumber(-5.7890));
        try {
            schema.validate(type, system().createNumber(5.789));
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
    public void testObjectSimple() {
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
    public void testObjectLayers() {
        JsonObject def = system().createObject();
        def.put("name", "string");
        def.put(".optional phone", "string");
        JsonObject addressObj = system().createObject();
        addressObj.put("street", "string");
        addressObj.put(".optional city", "string");
        addressObj.put("zip", "integer");
        def.put("address", addressObj);
        Type type = schema().resolve(def);
        JsonObject instance = system().createObject();
        instance.put("name", "King Strawberry");
        JsonObject addressInst = system().createObject();
        addressInst.put("street", "126 Lolipop Lane");
        addressInst.put("zip", 61234);
        instance.put("address", addressInst);
        schema.validate(type, instance);
        
        addressInst.put("city", true);
        try {
            schema.validate(type, instance);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testObjectPattern() {
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
    public void testObjectMultiplePatterns() {
        JsonObject def = system().createObject();
        def.put("name", "string");
        def.put(".pattern foo-*", "string");
        def.put(".pattern *-bar", "decimal");
        def.put("foo-zip", "long");
        def.put("long-bar", "decimal");
        def.put("foo-Number-bar", "string");
        def.put(".optional foo-Wrong-bar", "string");
        
        Type type = schema().resolve(def);
        JsonObject instance = system().createObject();
        instance.put("name", "Bob");
        instance.put("foo-zip", "91234");
        instance.put("long-bar", "-5.5");
        instance.put("foo-Number-bar", "1.2345");
        schema.validate(type, instance);
        
        instance.put("foo-Wrong-bar", "1.abc");
        try {
        	schema.validate(type, instance);
        } catch (ItemscriptError e) {
        	threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testObjectInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "object");
        JsonArray inArray = def.createArray(".inArray");
        JsonObject object1 = system().createObject();
        object1.put(".extends", "integer");
        object1.put(".greaterThan", 5);
        JsonObject object2 = system().createObject();
        object2.put(".extends", "string");
        object2.put("minLength", 7);
        inArray.add(object1);
        inArray.add(object2);
        Type type = schema().resolve(def);
        
        JsonObject validObject = system().createObject();
        validObject.put(".extends", "integer");
        validObject.put(".greaterThan", 5);
        schema.validate(type, validObject);
        
        JsonObject invalidObject = system().createObject();
        invalidObject.put(".extends", "string");
        try {
            schema.validate(type, invalidObject);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testObjectNotInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "object");
        JsonArray notInArray = def.createArray(".notInArray");
        JsonObject object1 = system().createObject();
        object1.put(".extends", "integer");
        object1.put(".greaterThan", 5);
        JsonObject object2 = system().createObject();
        object2.put(".extends", "string");
        object2.put("minLength", 7);
        notInArray.add(object1);
        notInArray.add(object2);
        Type type = schema().resolve(def);
        
        JsonObject validObject = system().createObject();
        validObject.put(".extends", "integer");
        schema.validate(type, validObject);
        
        JsonObject invalidObject = system().createObject();
        invalidObject.put(".extends", "string");
        invalidObject.put("minLength", 7);
        try {
            schema.validate(type, invalidObject);
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }
    
    @Test
    public void testObjectWildcard() {
        JsonObject def = system().createObject();
        def.put(".extends", "object");
        def.put("name", "string");
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
    
    public void testObjectWrongTypeForKey() {
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
    public void testObjectWrongTypeForOptionalKey() {
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
    
    @Test
    public void testNoAddType() {
    	JsonObject def = system().createObject();
    	def.put("name", "string");
    	def.put("address", "object");
    	
    	JsonObject test = system().createObject();
    	test.put(".extends", "address");
    	test.put("street", "string");
    	test.put(".optional zipcode", "number");
    	try {
    	   	 Type testType = schema().resolve(test);
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    }
    
    @Test
    public void testAddTypeObjectWithoutDefinitions() {
    	JsonObject def = system().createObject();
    	def.put("name", "string");
    	def.put("address", "object");
    	schema.addAllTypes(def);
    	
    	JsonObject addressDef = system().createObject();
    	addressDef.put(".extends", "address");
    	addressDef.put("street", "string");
    	addressDef.put(".optional zipcode", "number");
    	Type addressType = schema().resolve(addressDef);
    	JsonObject addressInst = system().createObject();
    	addressInst.put("street", "First Ave");
    	schema.validate(addressType, addressInst);
    	addressInst.put("zipcode", "91234");
    	try {
    		schema.validate(addressType, addressInst);
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    }
    
    @Test
    public void testAddTypeObjectWithDefinitions() {
    	JsonObject def = system().createObject();
    	JsonObject addressDef = system().createObject();
    	addressDef.put(".extends", "object");
    	addressDef.put("street", "string");
    	addressDef.put(".optional zipcode", "number");
    	def.put("name", "string");
    	def.put("address", addressDef);
    	schema.addAllTypes(def);
    	Type addressType = schema.resolve(addressDef);
    	JsonObject addressInst = system().createObject();
    	addressInst.put("street", "First Ave");
    	schema.validate(addressType, addressInst);
    	addressInst.put("zipcode", "91234");
    	try {
    		schema.validate(addressType, addressInst);
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    }
    
    @Test
    public void testAddTypeSingleWithoutDefinitions() {
    	schema.addType("phone", system().createString("string"));
    	
    	JsonObject phoneDef = system().createObject();
    	phoneDef.put(".extends", "phone");
    	phoneDef.put(".isLength", 3);
    	phoneDef.put(".regExPattern", "[0-9]+");
    	Type areaCodeType = schema().resolve(phoneDef);
    	schema.validate(areaCodeType, system().createString("510"));
    	try {
    		schema.validate(areaCodeType, system().createString("1234"));
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    	threwException = false;
    	try {
    		schema.validate(areaCodeType, system().createString("abc"));
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    	threwException = false;
    	try {
    		schema.validate(areaCodeType, system().createString("51o"));
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    }
    
    @Test
    public void testAddTypeSingleWithDefinitions() {    	
    	JsonObject zipDef = system().createObject();
    	zipDef.put(".extends", "string");
    	zipDef.put(".isLength", 5);
    	zipDef.put(".regExPattern", "[0-9]+");
    	schema.addType("zipcode", zipDef);
    	Type zipType = schema.resolve(zipDef);
    	schema.validate(zipType, system().createString("91234"));
    	try {
    		schema.validate(zipType, system().createString("123456"));
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    	threwException = false;
    	try {
    		schema.validate(zipType, system().createString("abcde"));
    	} catch (ItemscriptError e) {
    		threwException = true;
    	}
    	assertTrue(threwException);
    	threwException = false;
    	try {
    		schema.validate(zipType, system().createString("9123a"));
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
    public void testStringIsLength() {
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
    public void testStringMaxLength() {
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
    public void testStringMinLength() {
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
    public void testStringInArray() {
        JsonObject def = system().createObject();
        def.put(".extends", "string");
        JsonArray inArray = def.createArray(".inArray");
        inArray.add("abc");
        inArray.add("123 ");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("abc"));
        schema.validate(type, system().createString("123 "));
        try {
            schema.validate(type, system().createString("a b c"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
        threwException = false;
        try {
            schema.validate(type, system().createString("abc "));
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
    public void testStringSinglePattern() {
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
    public void testStringArrayPattern() {
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
}