
package org.itemscript.schema;


import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

final class BinaryType extends TypeBase {
	private static final String MAX_BYTES_KEY = ".maxBytes";
	private final boolean hasDef;
	private final int maxBytes;
	
    BinaryType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
    	if (def != null) {
			hasDef = true;
			if (def.hasNumber(MAX_BYTES_KEY)) {
				maxBytes = def.getInt(MAX_BYTES_KEY);
			} else {
				maxBytes = -1;
			}
    	} else {
    		hasDef = false;
    		maxBytes = -1;
    	}
    }

    @Override
    public boolean isBinary() {
        return true;
    }
    
	private Params pathValueParams(String path, String binary) {
		return schema().pathParams(path).p("value", binary);
	}

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
		if (!value.isString()) {
			throw ItemscriptError.internalError(this,
					"validate.value.was.not.string", schema().pathParams(path)
							.p("value", value.toCompactJsonString()));
		}
        if (hasDef) {
			validateBinary(path, value.asString());
        }
    }
    
    private void validateBinary(String path, JsonString binaryValue) {
		if (maxBytes > 0) {
			byte[] binaryArray = binaryValue.binaryValue();
			if (binaryArray.length > maxBytes) { throw ItemscriptError.internalError(this,
					"validateBinary.value.has.too.many.bytes", pathValueParams(path, binaryValue+"")); }
		}
    }
}