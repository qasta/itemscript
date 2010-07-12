
package org.itemscript.schema;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonValue;
import org.itemscript.core.values.JsonObject;

final class AnyType extends TypeBase {
	private static final String ARRAY_KEY = ".array";
	private static final String BOOLEAN_KEY = ".boolean";
	private static final String NUMBER_KEY = ".number";
	private static final String OBJECT_KEY = ".object";
	private static final String STRING_KEY = ".string";
	private final boolean hasDef;
	JsonValue array;
	JsonValue bool;
	JsonValue number;
	JsonValue object;
	JsonValue string;
	
    AnyType(Schema schema) {
        super(schema);
        array = null;
        bool = null;
        number = null;
        object = null;
        string = null;
        hasDef = false;
    }
    
    AnyType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
		if (def != null) {
			hasDef = true;
	    	if (def.containsKey(ARRAY_KEY)) {
	    		array = def.getValue(ARRAY_KEY);
	    	} else {
	    		array = null;
	    	}
	    	if (def.containsKey(BOOLEAN_KEY)) {
	    		bool = def.getValue(BOOLEAN_KEY);
	    	} else {
	    		bool = null;
	    	}
	    	if (def.containsKey(NUMBER_KEY)) {
	    		number = def.getValue(NUMBER_KEY);
	    	} else {
	    		number = null;
	    	}
	    	if (def.containsKey(OBJECT_KEY)) {
	    		object = def.getValue(OBJECT_KEY);
	    	} else {
	    		object = null;
	    	}
	    	if (def.containsKey(STRING_KEY)) {
	    		string = def.getValue(STRING_KEY);
			} else {
				string = null;
	    	}
		} else {
			array = null;
			bool = null;
			number = null;
			object = null;
			string = null;
			hasDef = false;
		}
    }
  
    @Override
    public boolean isAny() {
        return true;
    }
    
    private Params pathValueParams(String path, JsonValue value) {
        return schema().pathParams(path)
                .p("value", value);
    }
    
    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (value == null) { throw ItemscriptError.internalError(this, "validate.value.was.null",
                schema().pathParams(path));
        }
    	if (hasDef) {
    		validateAny(path, value);
    	}      
    }
    
    private void validateAny(String path, JsonValue value) {
		boolean useSlash = path.length() > 0;
		boolean validValue = false;
		Type stringType;
		Type numberType;
		Type boolType;
		Type arrayType;
		Type objectType;
		
		if (string != null) {
    		stringType = schema().resolve(string);
    		if (value.isString()) {
    			stringType.validate(path + (useSlash ? "/" : ""), value);
    			validValue = true;
    		}
		}
		if (number != null) {
			numberType = schema().resolve(number);
			if (value.isNumber()) {
				numberType.validate(path + (useSlash ? "/" : ""), value);
				validValue = true;
			}
		}
		if (bool != null) {
    		boolType = schema().resolve(bool);
    		if (value.isBoolean()) {
    			boolType.validate(path + (useSlash ? "/" : ""), value);
    			validValue = true;
    		}
    	}
		if (array != null) {
    		arrayType = schema().resolve(array);
    		if (value.isArray()) {
    			arrayType.validate(path + (useSlash ? "/" : ""), value);
    			validValue = true;
    		}
		}
		if (object != null) {
    		objectType = schema().resolve(object);
    		if (value.isObject()) {
    			objectType.validate(path + (useSlash ? "/" : ""), value);
    			validValue = true;
    		}
		}
		if (!validValue) {
			throw ItemscriptError.internalError(this,
        			"validateAny.value.was.not.of.specified.type", pathValueParams(path, value));
		}
    }
}