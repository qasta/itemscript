
package org.itemscript.schema;

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

import org.itemscript.core.Params;
import org.itemscript.core.values.JsonArray;

final class NumberType extends TypeBase {
	private static final String GREATER_THAN_KEY = ".greaterThan";
	private static final String LESS_THAN_KEY = ".lessThan";
	private static final String GREATER_THAN_OR_EQUAL_TO_KEY = ".greaterThanOrEqualTo";
	private static final String LESS_THAN_OR_EQUAL_TO_KEY = ".lessThanOrEqualTo";
	private static final String EQUAL_TO_KEY = ".equalTo";
	//private static final String IN_ARRAY_KEY = ".inArray";
	//private static final String NOT_IN_ARRAY_KEY = "notInArray";
	private boolean hasDef;
	private final double greaterThan;
	private final boolean hasGreaterThan;
	private final double lessThan;
	private final boolean hasLessThan;
	private final double greaterThanOrEqualTo;
	private final boolean hasGreaterThanOrEqualTo;
	private final double lessThanOrEqualTo;
	private final boolean hasLessThanOrEqualTo;
	private final double equalTo;
	private final boolean hasEqualTo;
	//private final JsonArray inArray;
	//private final JsonArray notInArray;
	
    NumberType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
        if (def != null) {
            hasDef = true;
            if (def.hasNumber(GREATER_THAN_KEY)) {
            	hasGreaterThan = true;
                greaterThan = def.getDouble(GREATER_THAN_KEY);
            } else {
            	hasGreaterThan = false;
            	greaterThan = -1;
            }
            if (def.hasNumber(LESS_THAN_KEY)) {
            	hasLessThan = true;
            	lessThan = def.getDouble(LESS_THAN_KEY);
            } else {
            	hasLessThan = false;
            	lessThan = -1;
            }
            if (def.hasNumber(GREATER_THAN_OR_EQUAL_TO_KEY)) {
            	hasGreaterThanOrEqualTo = true;
            	greaterThanOrEqualTo = def.getDouble(GREATER_THAN_OR_EQUAL_TO_KEY);	
            } else {
            	hasGreaterThanOrEqualTo = false;
            	greaterThanOrEqualTo = -1;
            }
            if (def.hasNumber(LESS_THAN_OR_EQUAL_TO_KEY)) {
            	hasLessThanOrEqualTo = true;
            	lessThanOrEqualTo = def.getDouble(LESS_THAN_OR_EQUAL_TO_KEY);
            } else {
            	hasLessThanOrEqualTo = false;
            	lessThanOrEqualTo = -1;
            }
            if (def.hasNumber(EQUAL_TO_KEY)) {
            	hasEqualTo = true;
            	equalTo = def.getDouble(EQUAL_TO_KEY);
            } else {
            	hasEqualTo = false;
            	equalTo = -1;
            }
        } else {
        	greaterThan = -1;
        	hasGreaterThan = false;
        	lessThan = -1;
        	hasLessThan = false;
        	greaterThanOrEqualTo = -1;
        	hasGreaterThanOrEqualTo = false;
        	lessThanOrEqualTo = -1;
        	hasLessThanOrEqualTo = false;
        	equalTo = -1;
        	hasEqualTo = false;
        }
    }

    @Override
    public boolean isNumber() {
        return true;
    }
    
    private Params pathValueParams(String path, Double num) {
        return schema().pathParams(path)
                .p("value", num);
    }

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (!value.isNumber()) { throw ItemscriptError.internalError(this, "validate.value.was.not.number",
                schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
        if (hasDef) {
            validateNumber(path, value.doubleValue());
        }
    }
    
    private void validateNumber(String path, Double num) {
        if (hasGreaterThan) {
            if (num <= greaterThan) { throw ItemscriptError.internalError(this,
                    "validateNumber.value.is.less.than.or.equal.to.min", pathValueParams(path, num)); }
        }
        if (hasLessThan) {
        	if (num >= lessThan) { throw ItemscriptError.internalError(this,
                    "validateNumber.value.is.greater.than.or.equal.to.max", pathValueParams(path, num)); }
        }
        if (hasGreaterThanOrEqualTo) {
        	if (num < greaterThanOrEqualTo) { throw ItemscriptError.internalError(this,
                    "validateNumber.value.is.less.than.min", pathValueParams(path, num)); }
        }
        if (hasLessThanOrEqualTo) {
        	if (num > lessThanOrEqualTo) { throw ItemscriptError.internalError(this,
                    "validateNumber.value.is.greater.than.max", pathValueParams(path, num)); }
        }
        if (hasEqualTo) {
        	if (num != equalTo) { throw ItemscriptError.internalError(this,
                    "validateNumber.value.is.not.equal.to.equal.to", pathValueParams(path, num)); }
        }
    }
}