
package org.itemscript.schema;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;
/**
 * @authors Eileen Bai and Jacob Davies
 */
final class BooleanType extends TypeBase {
	private static final String BOOLEAN_VALUE_KEY = ".booleanValue";
	private boolean hasDef;
    private final boolean booleanValue;
	private final boolean hasBooleanValue;	

    BooleanType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
        if (def != null) {
            hasDef = true;
            if (def.hasBoolean(BOOLEAN_VALUE_KEY)) {
            	hasBooleanValue = true;
                booleanValue = def.getBoolean(BOOLEAN_VALUE_KEY);
            } else {
            	hasBooleanValue = false;
            	booleanValue = false;
            }
        } else {
        	hasDef = false;
        	booleanValue = false;
        	hasBooleanValue = false;
        }
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    private Params pathValueParams(String path, Boolean bool) {
        return schema().pathParams(path)
                .p("value", bool);
    }
    
    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (!value.isBoolean()) { throw ItemscriptError.internalError(this, "validateBoolean.value.was.not.boolean",
                schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
        if (hasDef) {
            validateBoolean(path, value.booleanValue());
        }
    }
    
    private void validateBoolean(String path, Boolean bool) {
        if (hasBooleanValue) {
            if (bool != booleanValue) { throw ItemscriptError.internalError(this,
                    "validateBoolean.value.does.not.equal.required.boolean.value", pathValueParams(path, bool)); }
        }
    }
}