
package org.itemscript.schema;

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonValue;
import org.itemscript.core.values.JsonObject;

final class AnyType extends TypeBase {
	private static final String STRING_KEY = ".string";
	private static final String NUMBER_KEY = ".number";
	private static final String BOOLEAN_KEY = ".boolean";
	private static final String ARRAY_KEY = ".array";
	private static final String OBJECT_KEY = ".object";
	private final boolean hasDef;
	JsonValue string;
	JsonValue number;
	JsonValue bool;
	JsonValue array;
	JsonValue object;
	
    AnyType(Schema schema) {
        super(schema);
        string = null;
        number = null;
        bool = null;
        array = null;
        object = null;
        hasDef = false;
    }
    
    AnyType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
		if (def != null) {
			hasDef = true;
	    	if (def.containsKey(STRING_KEY)) {
	    		string = def.getValue(STRING_KEY);
			} else {
				string = null;
	    	}
	    	if (def.containsKey(NUMBER_KEY)) {
	    		number = def.getValue(NUMBER_KEY);
	    	} else {
	    		number = null;
	    	}
	    	if (def.containsKey(BOOLEAN_KEY)) {
	    		bool = def.getValue(BOOLEAN_KEY);
	    	} else {
	    		bool = null;
	    	}
	    	if (def.containsKey(ARRAY_KEY)) {
	    		array = def.getValue(ARRAY_KEY);
	    	} else {
	    		array = null;
	    	}
	    	if (def.containsKey(OBJECT_KEY)) {
	    		object = def.getValue(OBJECT_KEY);
	    	} else {
	    		object = null;
	    	}
		} else {
			hasDef = false;
			string = null;
			number = null;
			bool = null;
			array = null;
			object = null;
		}
    }
  
    @Override
    public boolean isAny() {
        return true;
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
		Type stringType;
		Type numberType;
		Type boolType;
		Type arrayType;
		Type objectType;
		
		if (string != null) {
    		stringType = schema().resolve(string);
    		stringType.validate(path + (useSlash ? "/" : ""), value);
		}
		if (number != null) {
			numberType = schema().resolve(number);
    		numberType.validate(path + (useSlash ? "/" : ""), value);
		}
		if (bool != null) {
    		boolType = schema().resolve(bool);
    		boolType.validate(path + (useSlash ? "/" : ""), value);
    	}
		if (array != null) {
    		arrayType = schema().resolve(array);
    		arrayType.validate(path + (useSlash ? "/" : ""), value);
		}
		if (object != null) {
    		objectType = schema().resolve(object);
    		objectType.validate(path + (useSlash ? "/" : ""), value);
		}
    }
}