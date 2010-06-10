
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
    public void testArrayChoose() {
        JsonObject def = system().createObject();
        def.put("EXTENDS", "string");
        JsonArray pattern = def.createArray("CHOOSE");
        pattern.add("xyz");
        pattern.add("abc");
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
    public void testArrayPattern() {
        JsonObject def = system().createObject();
        def.put("EXTENDS", "string");
        JsonArray pattern = def.createArray("PATTERN");
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
    public void testSingleChoose() {
        JsonObject def = system().createObject();
        def.put("EXTENDS", "string");
        def.put("CHOOSE", "xyz");
        Type type = schema().resolve(def);
        schema.validate(type, system().createString("xyz"));
        try {
            schema.validate(type, system().createString("xyzijoij"));
        } catch (ItemscriptError e) {
            threwException = true;
        }
        assertTrue(threwException);
    }

    @Test
    public void testSinglePattern() {
        JsonObject def = system().createObject();
        def.put("EXTENDS", "string");
        def.put("PATTERN", "xyz*");
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
        def.put("EXTENDS", "string");
        def.put("MAX", 3);
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
        def.put("EXTENDS", "string");
        def.put("MIN", 10);
        Type type = schema().resolve(def);
        try {
            schema.validate(type, system().createString("xyz"));
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
        def.put("OPTIONAL phone", "string");
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