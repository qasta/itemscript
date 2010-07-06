
package org.itemscript.schema;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;
import org.itemscript.core.values.JsonArray;

final class ArrayType extends TypeBase {
	private static final String VALIDATE_ALL_KEY = ".validateAll";
	private static final String EXACT_SIZE_KEY = ".exactSize";
	private static final String MIN_SIZE_KEY = ".minSize";
	private static final String MAX_SIZE_KEY = ".maxSize";
	private final boolean hasDef;
	private final boolean validateAll;
	private final int exactSize;
	private final int minSize;
	private final int maxSize;
	
    ArrayType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
        if (def != null) {
			hasDef = true;
			if (def.hasBoolean(VALIDATE_ALL_KEY)) {
				validateAll = def.getBoolean(VALIDATE_ALL_KEY);
			} else {
				validateAll = false;
			}
			if (def.hasNumber(EXACT_SIZE_KEY)) {
				exactSize = def.getInt(EXACT_SIZE_KEY);
			} else {
				exactSize = -1;
			}
			if (def.hasNumber(MIN_SIZE_KEY)) {
				minSize = def.getInt(MIN_SIZE_KEY);
			} else {
				minSize = -1;
			}
			if (def.hasNumber(MAX_SIZE_KEY)) {
				maxSize = def.getInt(MAX_SIZE_KEY);
			} else {
				maxSize = -1;
			}
        } else {
        	hasDef = false;
        	validateAll = false;
        	exactSize = -1;
        	minSize = -1;
        	maxSize = -1;
        }
    }

    @Override
    public boolean isArray() {
        return true;
    }
    
    private Params pathValueParams(String path, JsonArray array) {
        return schema().pathParams(path)
                .p("value", array);
    }

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (!value.isArray()) { throw ItemscriptError.internalError(this, "validate.value.was.not.array",
                schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
        if (hasDef) {
            validateArray(path, value.asArray());
        }
    }
    
    private void validateArray(String path, JsonArray array) {
        /**if (validateAll) {
        	for(int i = 0; array.getObject(i) != null; i++) {
        		if ()
        	}
        }
            if (num <= greaterThan) { throw ItemscriptError.internalError(this,
                    "validateNumber.value.is.less.than.or.equal.to.min", pathValueParams(path, num)); }
        }**/
    	if (exactSize > 0) {
    		if (array.size() != exactSize) { throw ItemscriptError.internalError(this,
                    "validateArray.array.is.the.wrong.size", pathValueParams(path, array)); }
    	}
    	if (minSize > 0) {
    		if (array.size() < minSize) { throw ItemscriptError.internalError(this,
                    "validateArray.array.size.is.smaller.than.min", pathValueParams(path, array)); }
    	}
    	if (maxSize > 0) {
    		if (array.size() > maxSize) { throw ItemscriptError.internalError(this,
                    "validateArray.array.size.is.greater.than.max", pathValueParams(path, array)); }
    	}
    }
}