
package org.itemscript.schema;

import java.util.ArrayList;
import java.util.List;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

class DecimalType extends TypeBase {
    private static final String PATTERN_KEY = ".pattern";
	private static final String MAX_LENGTH_KEY = ".maxLength";
	private static final String MIN_LENGTH_KEY = ".minLength";
	private static final String CHOOSE_KEY = ".choose";
	private static final String IS_LENGTH_KEY = ".isLength";
	private static final String EQUALS_KEY = ".equals";
	private static final String REG_EX_PATTERN_KEY = ".regExPattern";
	private static final String GREATER_THAN_KEY = ".greaterThan";
	private static final String LESS_THAN_KEY = ".lessThan";
	private static final String GREATER_THAN_OR_EQUAL_TO_KEY = ".greaterThanOrEqualTo";
	private static final String LESS_THAN_OR_EQUAL_TO_KEY = ".lessThanOrEqualTo";
	private static final String EQUAL_TO_KEY = ".equalTo";
	private static final String FRACTION_DIGITS_KEY = ".fractionDigits";
	//private static final String IN_ARRAY_KEY = ".inArray";
	//private static final String NOT_IN_ARRAY_KEY = ".notInArray";
	private boolean hasDef;
    private final int minLength;
    private final int maxLength;
    private final List<String> choose;
    private final List<String> pattern;
    private final int isLength;
    private final String equals;
    private final String regExPattern;
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
	private final int fractionDigits;
    //private final JsonArray inArray;
    //private final JsonArray notInArray;

    public DecimalType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
        if (def != null) {
            hasDef = true;
            if (def.hasNumber(MIN_LENGTH_KEY)) {
                minLength = def.getInt(MIN_LENGTH_KEY);
            } else {
                minLength = -1;
            }
            if (def.hasNumber(MAX_LENGTH_KEY)) {
                maxLength = def.getInt(MAX_LENGTH_KEY);
            } else {
                maxLength = -1;
            }
            choose = new ArrayList<String>();
            if (def.hasArray(CHOOSE_KEY)) {
                JsonArray array = def.getArray(CHOOSE_KEY);
                for (int i = 0; i < array.size(); ++i) {
                    choose.add(array.getRequiredString(i));
                }
            } else if (def.hasString(CHOOSE_KEY)) {
                choose.add(def.getString(CHOOSE_KEY));
            }
            pattern = new ArrayList<String>();
            if (def.hasArray(PATTERN_KEY)) {
                JsonArray array = def.getArray(PATTERN_KEY);
                for (int i = 0; i < array.size(); ++i) {
                    pattern.add(array.getRequiredString(i));
                }
            } else if (def.hasString(PATTERN_KEY)) {
                pattern.add(def.getString(PATTERN_KEY));
            }
            if (def.hasNumber(IS_LENGTH_KEY)) {
            	isLength = def.getInt(IS_LENGTH_KEY);
            } else{
            	isLength = -1;
            }
            if (def.hasString(EQUALS_KEY)) {
            	equals = def.getString(EQUALS_KEY);
            } else{
            	equals = null;
            }
            if (def.hasString(REG_EX_PATTERN_KEY)) {
            	regExPattern = def.getString(REG_EX_PATTERN_KEY);
            } else{
            	regExPattern = null;
            }
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
            if (def.hasNumber(FRACTION_DIGITS_KEY)) {
            	fractionDigits = def.getInt(FRACTION_DIGITS_KEY);
            } else {
            	fractionDigits = -1;
            }
            //if (def.hasString(IN_ARRAY_KEY)) {
            //	inArray = def.getArray(IN_ARRAY_KEY);
            //} else{
            //	inArray = createArray();
            //}
            //if (def.hasString(NOT_IN_ARRAY_KEY)) {
            //	notInArray = def.getArray(NOT_IN_ARRAY_KEY);
            //} else{
            //	notInArray = false;
            //}
        } else {
            hasDef = false;
            minLength = -1;
            maxLength = -1;
            choose = null;
            pattern = null;
            isLength = -1;
            equals = null;
            regExPattern = null;
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
        	fractionDigits = -1;
            //inArray = false;
            //notInArray = false;
        }
    }

    @Override
    public boolean isDecimal() {
        return true;
    }

    private Params pathValueParams(String path, String dec) {
        return schema().pathParams(path)
                .p("value", dec);
    }

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (!value.isString()) { throw ItemscriptError.internalError(this, "validate.value.was.not.string",
                schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
        if (hasDef) {
        	if(isDecimal(value.stringValue())) {
        		validateDecimal(path, value.stringValue());
        	}
        	else {
        		throw ItemscriptError.internalError(this, "validate.value.was.not.decimal",
                schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
        }
    }
    
    private void validateDecimal(String path, String string) {
    	if (minLength > 0) {
    		if (string.length() < minLength) { throw ItemscriptError.internalError(this,
    				"validateString.value.shorter.than.min.length", pathValueParams(path, string)); }
    	}
    	if (maxLength > 0) {
    		if (string.length() > maxLength) { throw ItemscriptError.internalError(this,
    				"validateString.value.longer.than.max.length", pathValueParams(path, string)); }
    	}
    	if (choose.size() > 0) {
    		boolean matched = false;
    		for (int i = 0; i < choose.size(); ++i) {
    			String chooseString = choose.get(i);
    			if (string.equals(chooseString)) {
    				matched = true;
    			}
    		}
    		if (!matched) { throw ItemscriptError.internalError(this,
    				"validateString.value.did.not.match.any.choice", pathValueParams(path, string)); }
    	}
    	if (pattern.size() > 0) {
    		boolean matched = false;
    		for (int i = 0; i < pattern.size(); ++i) {
    			String patternString = pattern.get(i);
    			if (schema().match(patternString, string)) {
    				matched = true;
    			}
    		}
    		if (!matched) { throw ItemscriptError.internalError(this,
    				"validateString.value.did.not.match.any.pattern", pathValueParams(path, string)); }
    	}
    	if (isLength > 0) {
    		if (string.length() != isLength) { throw ItemscriptError.internalError(this,
    				"validateString.value.does.not.equal.is.length", pathValueParams(path, string)); }
    	}
    	if (equals != null) {
    		if (!string.equals(equals)) { throw ItemscriptError.internalError(this,
    				"validateString.value.does.not.equal.equal.to", pathValueParams(path, string)); }
    	}
    	if (regExPattern != null) {
    		if (!string.matches(regExPattern)) { throw ItemscriptError.internalError(this,
    				"validateString.value.does.not.match.reg.ex.pattern", pathValueParams(path, string)); }
    	}
    	/**
    	if (hasGreaterThan) {
    		if (string <= greaterThan) { throw ItemscriptError.internalError(this,
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
        }*/
    }
    
    /** 
     * Splits string into two parts if there where there is a decimal point.
     * Then calls checkDigits to verify that both parts are digits.
     * If there is no decimal point, then checkDigits verifies the entire string.
     * 
     * @param string
     * @return true if verified, false if not
     */
    private boolean isDecimal(String string) {
    	string = string.trim();
    	int startIndex = 0;
    	int pointIndex;
    	if (string.charAt(0) == '+' || string.charAt(0) == '-') {
    		startIndex = 1;
    	}
    	pointIndex = string.indexOf(".");
    	if (pointIndex > -1) {
    		if (checkDigits(string.substring(startIndex, pointIndex))) {
    			if (checkDigits(string.substring(pointIndex + 1))) {
    				return true;
    			}
    		}
    	}
    	else {
    		if(checkDigits(string)) {
    			return true;
    		}
    	}
		return false;
    }
    
    /**
     * Loops through the string and checks that every character is a numerical digit.
     * If it finds a character that is not a Digit it signals the badDigit flag and breaks out of the loop.
     * 
     * @param string
     * @return true if all digits, false if not
     */
    private boolean checkDigits(String string) {
    	boolean badDigit = false;
    	for (int i = 0; i < string.length(); i++) {
	    	char digit = string.charAt(i);
	    	if (Character.isDigit(digit)) {
	    		continue;
	    	}
	    	else {
	    		badDigit = true;
	    		break;
	    	}
    	}
    	if (badDigit = true) {
    		return false;
    	}
    	return true;
    }
}