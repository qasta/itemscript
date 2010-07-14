
package org.itemscript.schema;

import java.util.ArrayList;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

import org.itemscript.core.values.JsonArray;
/**
 * Type class for the Integer Type. All IntegerTypes are represented by numbers.
 * @author Eileen Bai
 */
final class IntegerType extends TypeBase {
	private static final String EQUAL_TO_KEY = ".equalTo";
	private static final String EVEN_KEY = ".even";
	private static final String GREATER_THAN_KEY = ".greaterThan";
	private static final String GREATER_THAN_OR_EQUAL_TO_KEY = ".greaterThanOrEqualTo";
	private static final String IN_ARRAY_KEY = ".inArray";
	private static final String LESS_THAN_KEY = ".lessThan";
	private static final String LESS_THAN_OR_EQUAL_TO_KEY = ".lessThanOrEqualTo";
	private static final String NOT_IN_ARRAY_KEY = ".notInArray";
	private static final String ODD_KEY = ".odd";
	private boolean hasDef;
	private final boolean hasEqualTo;
	private final boolean hasEven;
	private final boolean hasGreaterThan;
	private final boolean hasGreaterThanOrEqualTo;
	private final boolean hasLessThan;
	private final boolean hasLessThanOrEqualTo;
	private final boolean hasOdd;
	private final int equalTo;
	private final boolean even;
	private final int greaterThan;
	private final int greaterThanOrEqualTo;
	private final int lessThan;
	private final int lessThanOrEqualTo;
	private final boolean odd;
	private final ArrayList<Integer> inArray;
	private final ArrayList<Integer> notInArray;
	
	/**
	 * Create a new IntegerType. Sets all associated ".keys" that are specified.
	 * 
	 * @param schema
	 * @param extendsType
	 * @param def
	 */
    IntegerType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
        if (def != null) {
            hasDef = true;
            if (def.hasNumber(EQUAL_TO_KEY)) {
            	hasEqualTo = true;
            	equalTo = def.getInt(EQUAL_TO_KEY);
            } else {
            	hasEqualTo = false;
            	equalTo = -1;
            }
            if (def.hasBoolean(EVEN_KEY)) {
            	hasEven = true;
            	even = def.getBoolean(EVEN_KEY);
            } else {
            	hasEven = false;
            	even = false;
            }
            if (def.hasNumber(GREATER_THAN_KEY)) {
            	hasGreaterThan = true;
                greaterThan = def.getInt(GREATER_THAN_KEY);
            } else {
            	hasGreaterThan = false;
            	greaterThan = -1;
            }
            if (def.hasNumber(GREATER_THAN_OR_EQUAL_TO_KEY)) {
            	hasGreaterThanOrEqualTo = true;
            	greaterThanOrEqualTo = def.getInt(GREATER_THAN_OR_EQUAL_TO_KEY);	
            } else {
            	hasGreaterThanOrEqualTo = false;
            	greaterThanOrEqualTo = -1;
            }
            if (def.hasNumber(LESS_THAN_KEY)) {
            	hasLessThan = true;
            	lessThan = def.getInt(LESS_THAN_KEY);
            } else {
            	hasLessThan = false;
            	lessThan = -1;
            }
            if (def.hasArray(IN_ARRAY_KEY)) {
            	inArray = new ArrayList<Integer>();
            	JsonArray array = def.getArray(IN_ARRAY_KEY);
            	for (int i = 0; i < array.size(); ++i) {
            		inArray.add(array.getRequiredInt(i));
            	}
            } else {
            	inArray = null;
            }
            if (def.hasNumber(LESS_THAN_OR_EQUAL_TO_KEY)) {
            	hasLessThanOrEqualTo = true;
            	lessThanOrEqualTo = def.getInt(LESS_THAN_OR_EQUAL_TO_KEY);
            } else {
            	hasLessThanOrEqualTo = false;
            	lessThanOrEqualTo = -1;
            }
            if (def.hasArray(NOT_IN_ARRAY_KEY)) {
            	notInArray = new ArrayList<Integer>();
            	JsonArray array = def.getArray(NOT_IN_ARRAY_KEY);
            	for (int i = 0; i < array.size(); ++i) {
            		notInArray.add(array.getRequiredInt(i));
            	}
            } else {
            	notInArray = null;
            }
            if (def.hasBoolean(ODD_KEY)) {
            	hasOdd = true;
            	odd = def.getBoolean(ODD_KEY);
            } else {
            	hasOdd = false;
            	odd = false;
            }
        } else {
        	hasDef = false;
        	hasEqualTo = false;
        	hasEven = false;
        	hasGreaterThan = false;
        	hasGreaterThanOrEqualTo = false;
        	hasLessThan = false;
        	hasLessThanOrEqualTo = false;
        	hasOdd = false;
        	equalTo = -1;
        	even = false;
        	greaterThan = -1;
        	greaterThanOrEqualTo = -1;
        	inArray = null;
        	lessThan = -1;
        	lessThanOrEqualTo = -1;
        	notInArray = null;
        	odd = false;
        }
    }

    @Override
    public boolean isInteger() {
        return true;
    }
    
    private Params pathValueParams(String path, Integer num) {
        return schema().pathParams(path)
                .p("value", num);
    }

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (!value.isNumber()) {
        	throw ItemscriptError.internalError(
                    this, "validateInteger.is.not.a.number", schema().pathParams(path)
                            .p("value", value.toCompactJsonString()));
        }
        if (value.doubleValue() != Math.round(value.doubleValue())) { throw ItemscriptError.internalError(
                this, "validateInteger.had.fractional.digits", schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
        if (hasDef) {
            validateInteger(path, value.intValue());
        }
    }
    
    private void validateInteger(String path, Integer num) {
        if (hasEqualTo) {
        	if (num != equalTo) { throw ItemscriptError.internalError(this,
                    "validateInteger.value.is.not.equal.to.equal.to", pathValueParams(path, num)); }
        }
        if (hasEven) {
        	if (even) {
        		if ((num % 2) != 0) { throw ItemscriptError.internalError(this,
                        "validateInteger.value.is.not.even", pathValueParams(path, num)); }
        	} else {
        		if ((num % 2) == 0) { throw ItemscriptError.internalError(this,
                        "validateInteger.value.is.not.odd", pathValueParams(path, num)); }
        	}
        }
        if (hasGreaterThan) {
            if (num <= greaterThan) { throw ItemscriptError.internalError(this,
                    "validateInteger.value.is.less.than.or.equal.to.min", pathValueParams(path, num)); }
        }
        if (hasGreaterThanOrEqualTo) {
        	if (num < greaterThanOrEqualTo) { throw ItemscriptError.internalError(this,
                    "validateInteger.value.is.less.than.min", pathValueParams(path, num)); }
        }
        if (hasLessThan) {
        	if (num >= lessThan) { throw ItemscriptError.internalError(this,
                    "validateInteger.value.is.greater.than.or.equal.to.max", pathValueParams(path, num)); }
        }
        if (hasLessThanOrEqualTo) {
        	if (num > lessThanOrEqualTo) { throw ItemscriptError.internalError(this,
                    "validateInteger.value.is.greater.than.max", pathValueParams(path, num)); }
        }
        if (hasOdd) {
        	if (odd) {
        		if ((num % 2) == 0) { throw ItemscriptError.internalError(this,
                        "validateInteger.value.is.not.odd", pathValueParams(path, num)); }
        	} else {
        		if ((num % 2) != 0) { throw ItemscriptError.internalError(this,
                        "validateInteger.value.is.not.even", pathValueParams(path, num)); }
        	}
        }
        if (inArray != null) {
            boolean matched = false;
            for (int i = 0; i < inArray.size(); ++i) {
                int inArrayInt = inArray.get(i);
                if (num == inArrayInt) {
                    matched = true;
                }
            }
            if (!matched) { throw ItemscriptError.internalError(this,
                    "validateInteger.value.did.not.match.a.valid.choice", pathValueParams(path, num)); }
        }
        if (notInArray != null) {
            boolean matched = false;
            for (int i = 0; i < notInArray.size(); ++i) {
                int notInArrayInt = notInArray.get(i);
                if (num == notInArrayInt) {
                    matched = true;
                }
            }
            if (matched) { throw ItemscriptError.internalError(this,
                    "validateInteger.value.matched.an.invalid.choice", pathValueParams(path, num)); }
        }
    }
}