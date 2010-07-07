package org.itemscript.schema;

import java.util.ArrayList;
import java.util.List;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

class DecimalType extends TypeBase {
	private static final String GREATER_THAN_KEY = ".greaterThan";
	private static final String LESS_THAN_KEY = ".lessThan";
	private static final String GREATER_THAN_OR_EQUAL_TO_KEY = ".greaterThanOrEqualTo";
	private static final String LESS_THAN_OR_EQUAL_TO_KEY = ".lessThanOrEqualTo";
	private static final String EQUAL_TO_KEY = ".equalTo";
	private static final String IN_ARRAY_KEY = ".inArray";
	private static final String NOT_IN_ARRAY_KEY = ".notInArray";
	private static final String FRACTION_DIGITS_KEY = ".fractionDigits";
	private static final String EVEN_KEY = ".even";
	private static final String ODD_KEY = ".odd";
	private final boolean hasDef;
	private final String greaterThan;
	private final String lessThan;
	private final String greaterThanOrEqualTo;
	private final String lessThanOrEqualTo;
	private final String equalTo;
	private final List<String> inArray;
	private final List<String> notInArray;
	private final int fractionDigits;
	private final boolean even;
	private final boolean hasEven;
	private final boolean odd;
	private final boolean hasOdd;

	public DecimalType(Schema schema, Type extendsType, JsonObject def) {
		super(schema, extendsType, def);
		if (def != null) {
			hasDef = true;
			if (def.hasString(GREATER_THAN_KEY)) {
				greaterThan = def.getString(GREATER_THAN_KEY);
			} else {
				greaterThan = null;
			}
			if (def.hasString(LESS_THAN_KEY)) {
				lessThan = def.getString(LESS_THAN_KEY);
			} else {
				lessThan = null;
			}
			if (def.hasString(GREATER_THAN_OR_EQUAL_TO_KEY)) {
				greaterThanOrEqualTo = def
						.getString(GREATER_THAN_OR_EQUAL_TO_KEY);
			} else {
				greaterThanOrEqualTo = null;
			}
			if (def.hasString(LESS_THAN_OR_EQUAL_TO_KEY)) {
				lessThanOrEqualTo = def.getString(LESS_THAN_OR_EQUAL_TO_KEY);
			} else {
				lessThanOrEqualTo = null;
			}
			if (def.hasString(EQUAL_TO_KEY)) {
				equalTo = def.getString(EQUAL_TO_KEY);
			} else {
				equalTo = null;
			}
			if (def.hasNumber(FRACTION_DIGITS_KEY)) {
				fractionDigits = def.getInt(FRACTION_DIGITS_KEY);
			} else {
				fractionDigits = -1;
			}
			if (def.hasArray(IN_ARRAY_KEY)) {
				inArray = new ArrayList<String>();
				JsonArray array = def.getArray(IN_ARRAY_KEY);
				for (int i = 0; i < array.size(); ++i) {
					inArray.add(array.getRequiredString(i));
				}
			} else {
				inArray = null;
			}
			if (def.hasArray(NOT_IN_ARRAY_KEY)) {
				notInArray = new ArrayList<String>();
				JsonArray array = def.getArray(NOT_IN_ARRAY_KEY);
				for (int i = 0; i < array.size(); ++i) {
					notInArray.add(array.getRequiredString(i));
				}
			} else {
				notInArray = null;
			}
			if (def.hasBoolean(EVEN_KEY)) {
				hasEven = true;
				even = def.getBoolean(EVEN_KEY);
			} else {
				hasEven = false;
				even = false;
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
			greaterThan = null;
			lessThan = null;
			greaterThanOrEqualTo = null;
			lessThanOrEqualTo = null;
			equalTo = null;
			fractionDigits = -1;
			inArray = null;
			notInArray = null;
			even = false;
			hasEven = false;
			odd = false;
			hasOdd = false;
			
		}
	}

	@Override
	public boolean isDecimal() {
		return true;
	}

	private Params pathValueParams(String path, String dec) {
		return schema().pathParams(path).p("value", dec);
	}

	@Override
	public void validate(String path, JsonValue value) {
		super.validate(path, value);
		if (!value.isString()) {
			throw ItemscriptError.internalError(this,
					"validate.value.was.not.string", schema().pathParams(path)
							.p("value", value.toCompactJsonString()));
		}
		if (!isDecimal(value.stringValue())) {
			throw ItemscriptError.internalError(this,
					"validate.value.was.not.proper.decimal", schema().pathParams(path)
							.p("value", value.toCompactJsonString()));
		}
		if (hasDef) {
			validateDecimal(path, value.stringValue());
		}
	}

	private void validateDecimal(String path, String string) {
		double decValue;
		double greaterThanValue;
		double greaterThanOrEqualToValue;
		double lessThanValue;
		double lessThanOrEqualToValue;
		try {
			decValue = Double.parseDouble(string);
		} catch (NumberFormatException e) {
			throw ItemscriptError.internalError(this,
					"validateDecimal.value.could.not.be.parsed.into.double",
					pathValueParams(path, string));
		}
		if (inArray != null) {
			boolean matched = false;
			for (int i = 0; i < inArray.size(); ++i) {
				String inArrayString = inArray.get(i);
				if (decimalEquals(string, inArrayString)) {
					matched = true;
				}
			}
			if (!matched) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.did.not.match.a.valid.choice",
						pathValueParams(path, string));
			}
		}
		if (notInArray != null) {
			boolean matched = false;
			for (int i = 0; i < notInArray.size(); ++i) {
				String notInArrayString = notInArray.get(i);
				if (decimalEquals(string, notInArrayString)) {
					matched = true;
				}
			}
			if (matched) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.matched.an.invalid.choice",
						pathValueParams(path, string));
			}
		}
		if (equalTo != null) {
			if (!decimalEquals(string, equalTo)) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.is.not.equal.to.equalTo",
						pathValueParams(path, string));
			}
		}
		if (greaterThan != null) {;
			try {
				greaterThanValue = Double.parseDouble(greaterThan);
			} catch (NumberFormatException e) {
				throw ItemscriptError
						.internalError(
								this,
								"validateDecimal.greaterThan.could.not.be.parsed.into.double",
								pathValueParams(path, string));
			}
			if (decValue <= greaterThanValue) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.is.less.than.or.equal.to.min",
						pathValueParams(path, string));
			}
		}
		if (lessThan != null) {
			try {
				lessThanValue = Double.parseDouble(lessThan);
			} catch (NumberFormatException e) {
				throw ItemscriptError
						.internalError(
								this,
								"validateDecimal.lessThan.could.not.be.parsed.into.double",
								pathValueParams(path, string));
			}
			if (decValue >= lessThanValue) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.is.greater.than.or.equal.to.max",
						pathValueParams(path, string));
			}
		}
		if (greaterThanOrEqualTo != null) {
			try {
				greaterThanOrEqualToValue = Double
						.parseDouble(greaterThanOrEqualTo);
			} catch (NumberFormatException e) {
				throw ItemscriptError
						.internalError(
								this,
								"validateDecimal.greaterThanOrEqualTo.could.not.be.parsed.into.double",
								pathValueParams(path, string));
			}
			if (decValue <= greaterThanOrEqualToValue && !decimalEquals(string, greaterThanOrEqualTo)) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.is.less.than.min",
						pathValueParams(path, string));
			}
		}
		if (lessThanOrEqualTo != null) {
			try {
				lessThanOrEqualToValue = Double.parseDouble(lessThanOrEqualTo);
			} catch (NumberFormatException e) {
				throw ItemscriptError
						.internalError(
								this,
								"validateDecimal.lessThanOrEqualTo.could.not.be.parsed.into.double",
								pathValueParams(path, string));
			}
			if (decValue >= lessThanOrEqualToValue && !decimalEquals(string, lessThanOrEqualTo)) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.is.greater.than.max",
						pathValueParams(path, string));
			}
		}
		if (fractionDigits > 0) {
			if (numFractionDigits(string) > fractionDigits) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.has.wrong.number.of.fraction.digits",
						pathValueParams(path, string));
			}
		}
		if (hasEven) {
			if (!isInteger(string)) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.cannot.test.parity.value.is.not.an.integer",
						pathValueParams(path, string));
			}
			if (even) {
				if ((onesDigit(string) % 2) != 0) { throw ItemscriptError.internalError(this,
                        "validateNumber.value.is.not.even", pathValueParams(path, string)); }
        	} else {
        		if ((onesDigit(string) % 2) == 0) { throw ItemscriptError.internalError(this,
                        "validateNumber.value.is.not.odd", pathValueParams(path, string)); }
        	}
        }
        if (hasOdd) {
        	if (!isInteger(string)) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.cannot.test.parity.value.is.not.an.integer",
						pathValueParams(path, string));
			}
        	if (odd) {
				if ((onesDigit(string) % 2) == 0) { throw ItemscriptError.internalError(this,
                        "validateNumber.value.is.not.odd", pathValueParams(path, string)); }
        	} else {
        		if ((onesDigit(string) % 2) != 0) { throw ItemscriptError.internalError(this,
                        "validateNumber.value.is.not.even", pathValueParams(path, string)); }
        	}
        }
	}

	/**
	 * Splits string into two parts if there where there is a decimal point.
	 * Then calls checkDigits to verify that both parts are digits. If there is
	 * no decimal point, then checkDigits verifies the entire string.
	 * 
	 * @param string
	 * @return true if verified, false if not
	 */
	private boolean isDecimal(String string) {
		string = string.trim();
		int startIndex = 0;
		int pointIndex = -1;
		if (string.charAt(0) == '+' || string.charAt(0) == '-') {
			startIndex = 1;
		}
		pointIndex = string.indexOf(".");
		if (pointIndex == string.length() - 1) {
			return false;
		}
		if (pointIndex > -1) {
			if (checkDigits(string.substring(startIndex, pointIndex))) {
				if (checkDigits(string.substring(pointIndex + 1))) {
					return true;
				}
			}
		} else {
			if (checkDigits(string.substring(startIndex))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Loops through the string and checks that every character is a numerical
	 * digit. If it finds a character that is not a Digit it breaks out of the loop.
	 * 
	 * @param string
	 * @return true if all digits, false if not
	 */
	private boolean checkDigits(String string) {
		for (int i = 0; i < string.length(); i++) {
			char digit = string.charAt(i);
			if (!Character.isDigit(digit)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns whether not the two string values are numerically equal by ignoring all
	 * leading and trailing 0's.
	 * 
	 * @param dec
	 * @param value
	 * @return true if the strings are equal, false if not
	 */	
	private boolean decimalEquals(String num1, String num2) {
		num1 = num1.trim();
		num2 = num2.trim();
		int num1BeginIndex = beginIndex(num1);
		int num1EndIndex = endIndex(num1);
		int num2BeginIndex = beginIndex(num2);
		int num2EndIndex = endIndex(num2);
		int num1len = num1EndIndex - num1BeginIndex + 1;
		int num2len = num2EndIndex - num2BeginIndex + 1;
		
		if (bothZero(num1, num2)) {
			return true;
		}
	
		if (num1len == num2len) { //same length
			if (sameSign(num1, num2)) { //same sign
				return num1.regionMatches(num1BeginIndex, num2, num2BeginIndex, num1len); //same region
			}
		}
		return false;
	}
	
	/**
	 * Returns whether or not both values have the same sign.
	 * 
	 * @param num1
	 * @param num2
	 * @return true if both pos/neg, false if not
	 */	
	private boolean sameSign(String num1, String num2) {
		if (num1.charAt(0) == '-' && num2.charAt(0) != '-') {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns whether or not both values are zero.
	 * Need this as a separate method because 0's can be expressed in many ways.
	 * 
	 * @param num1
	 * @param num2
	 * @return true if both zero, false if not
	 */
	private boolean bothZero(String num1, String num2) {
		int num1BeginIndex = beginIndex(num1);
		int num1EndIndex = endIndex(num1);
		int num2BeginIndex = beginIndex(num2);
		int num2EndIndex = endIndex(num2);
		int num1len = num1EndIndex - num1BeginIndex + 1;
		int num2len = num2EndIndex - num2BeginIndex + 1;
		
		//Case: Both values are multiple 0's but with no dot. I.e. num1 = 000, num2 = 0000000
		if (num1.matches("-?[0]+") && num2.matches("-?[0]+")) {
			return true;
		}		
		
		//Case: Both values are 0's with a dot. I.e. num1 = 0.00, num2 = .0
		if (num1len == 0 && num2len == 0) { //will only be zero if in the format "...000.000..."
			return true;
		}
		
		//Case: If one of the nums = 00.00 (then len = 0) and another is 0000
		if (num1len == 0 && num2.matches("-?[0]+")) {
			return true;
		}
		if (num2len == 0 && num1.matches("-?[0]+")) {
			return true;
		}
		return false;
	}
	
	/** 
	 * Finds the index of the first character in the decimal that is not a 0.
	 * Used to skip all instances of leading 0's.
	 * 
	 * @param num
	 * @return index to begin comparing at
	 */	
	private int beginIndex(String num) {
		int i = 0;
		if (num.charAt(i) == '+' || num.charAt(i) == '-') {
			i = 1;
		}
		for (; i < num.length(); i++) {
			if (num.charAt(i) != '0') {
				break;
			}
		}
		return i;
	}
	
	/**
	 * Finds the index of the last character in the decimal that is not a 0.
	 * Used to skip all instances of trailing 0's.
	 * 
	 * @param num
	 * @return index to end comparing at
	 */	
	private int endIndex(String num) {
		int i = num.length() - 1;
		for (; i > 0; i--) {
			if (num.charAt(i) != '0') {
				break;
			}
		}
		if (num.charAt(i) == '.') {
			i = i - 1;
		}
		return i;
	}
	
	/**
	 * Counts the number of fraction digits in the specified string (the number of digits after
	 * the decimal point).
	 * 
	 * @param string
	 * @return the number of fraction digits
	 */	
	private int numFractionDigits(String string) {
		string = string.trim();
		int pointIndex = string.indexOf(".");
		if (pointIndex > -1) {
			return string.substring(pointIndex + 1).length();
		}
		return 0;
	}
	
	/**
	 * Returns whether or not the string is an integer.
	 * 
	 * @param string
	 * @return true if int, false if not
	 */	
	private boolean isInteger(String string) {
		if (numFractionDigits(string) == 0) {
			return true;
		}
		int pointIndex = string.indexOf(".");
		if (decimalEquals(string.substring(pointIndex + 1), "0")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the digit in the one's column of the string.
	 * Value passed into here will always be an integer.
	 * 
	 * @param string
	 * @return int
	 */
	private int onesDigit(String string) {
		int pointIndex = string.indexOf(".");
		if (pointIndex == -1) {
			return string.charAt(string.length() - 1);
		} else {
			return string.charAt(pointIndex - 1);
		}
	}
}