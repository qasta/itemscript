package org.itemscript.schema;

import java.util.ArrayList;
import java.util.List;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;
/**
 * @author Eileen Bai
 */
class DecimalType extends TypeBase {
	private static final String EQUAL_TO_KEY = ".equalTo";
	private static final String EVEN_KEY = ".even";
	private static final String FRACTION_DIGITS_KEY = ".fractionDigits";
	private static final String GREATER_THAN_KEY = ".greaterThan";
	private static final String GREATER_THAN_OR_EQUAL_TO_KEY = ".greaterThanOrEqualTo";
	private static final String IN_ARRAY_KEY = ".inArray";
	private static final String LESS_THAN_KEY = ".lessThan";
	private static final String LESS_THAN_OR_EQUAL_TO_KEY = ".lessThanOrEqualTo";
	private static final String NOT_IN_ARRAY_KEY = ".notInArray";
	private static final String ODD_KEY = ".odd";
	private final boolean hasDef;
	private final boolean even;
	private final boolean hasEven;
	private final boolean hasOdd;
	private final boolean odd;
	private final int fractionDigits;
	private final String equalTo;
	private final String greaterThan;
	private final String greaterThanOrEqualTo;
	private final String lessThan;
	private final String lessThanOrEqualTo;
	private final List<String> inArray;
	private final List<String> notInArray;



	public DecimalType(Schema schema, Type extendsType, JsonObject def) {
		super(schema, extendsType, def);
		if (def != null) {
			hasDef = true;
			if (def.hasString(EQUAL_TO_KEY)) {
				equalTo = def.getString(EQUAL_TO_KEY);
			} else {
				equalTo = null;
			}
			if (def.hasBoolean(EVEN_KEY)) {
				hasEven = true;
				even = def.getBoolean(EVEN_KEY);
			} else {
				hasEven = false;
				even = false;
			}
			if (def.hasNumber(FRACTION_DIGITS_KEY)) {
				fractionDigits = def.getInt(FRACTION_DIGITS_KEY);
			} else {
				fractionDigits = -1;
			}			
			if (def.hasString(GREATER_THAN_KEY)) {
				greaterThan = def.getString(GREATER_THAN_KEY);
			} else {
				greaterThan = null;
			}
			if (def.hasString(GREATER_THAN_OR_EQUAL_TO_KEY)) {
				greaterThanOrEqualTo = def
						.getString(GREATER_THAN_OR_EQUAL_TO_KEY);
			} else {
				greaterThanOrEqualTo = null;
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
			if (def.hasString(LESS_THAN_KEY)) {
				lessThan = def.getString(LESS_THAN_KEY);
			} else {
				lessThan = null;
			}
			if (def.hasString(LESS_THAN_OR_EQUAL_TO_KEY)) {
				lessThanOrEqualTo = def.getString(LESS_THAN_OR_EQUAL_TO_KEY);
			} else {
				lessThanOrEqualTo = null;
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
			if (def.hasBoolean(ODD_KEY)) {
				hasOdd = true;
				odd = def.getBoolean(ODD_KEY);
			} else {
				hasOdd = false;
				odd = false;
			}
		} else {
			hasDef = false;
			equalTo = null;
			even = false;
			fractionDigits = -1;
			greaterThan = null;
			greaterThanOrEqualTo = null;
			hasEven = false;
			hasOdd = false;
			inArray = null;
			lessThan = null;
			lessThanOrEqualTo = null;
			notInArray = null;
			odd = false;
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

	private void validateDecimal(String path, String dec) {
		double decValue;
		double greaterThanValue;
		double greaterThanOrEqualToValue;
		double lessThanValue;
		double lessThanOrEqualToValue;
		
		try {
			decValue = Double.parseDouble(dec);
		} catch (NumberFormatException e) {
			throw ItemscriptError.internalError(this,
					"validateDecimal.value.could.not.be.parsed.into.double",
					pathValueParams(path, dec));
		}
		if (equalTo != null) {
			if (!decimalEquals(dec, equalTo)) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.is.not.equal.to.equalTo",
						pathValueParams(path, dec));
			}
		}
		if (fractionDigits > 0) {
			if (numFractionDigits(dec) > fractionDigits) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.has.wrong.number.of.fraction.digits",
						pathValueParams(path, dec));
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
								pathValueParams(path, dec));
			}
			if (decValue <= greaterThanValue) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.is.less.than.or.equal.to.min",
						pathValueParams(path, dec));
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
								pathValueParams(path, dec));
			}
			if (decValue <= greaterThanOrEqualToValue && !decimalEquals(dec, greaterThanOrEqualTo)) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.is.less.than.min",
						pathValueParams(path, dec));
			}
		}
		if (hasEven) {
			if (!isInteger(dec)) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.cannot.test.parity.value.is.not.an.integer",
						pathValueParams(path, dec));
			}
			if (even) {
				if ((onesDigit(dec) % 2) != 0) { throw ItemscriptError.internalError(this,
                        "validateDecimal.value.is.not.even", pathValueParams(path, dec)); }
        	} else {
        		if ((onesDigit(dec) % 2) == 0) { throw ItemscriptError.internalError(this,
                        "validateDecimal.value.is.not.odd", pathValueParams(path, dec)); }
        	}
        }
        if (hasOdd) {
        	if (!isInteger(dec)) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.cannot.test.parity.value.is.not.an.integer",
						pathValueParams(path, dec));
			}
        	if (odd) {
				if ((onesDigit(dec) % 2) == 0) { throw ItemscriptError.internalError(this,
                        "validateDecimal.value.is.not.odd", pathValueParams(path, dec)); }
        	} else {
        		if ((onesDigit(dec) % 2) != 0) { throw ItemscriptError.internalError(this,
                        "validateDecimal.value.is.not.even", pathValueParams(path, dec)); }
        	}
        }
		if (inArray != null) {
			boolean matched = false;
			for (int i = 0; i < inArray.size(); ++i) {
				String inArrayString = inArray.get(i);
				if (decimalEquals(dec, inArrayString)) {
					matched = true;
				}
			}
			if (!matched) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.did.not.match.a.valid.choice",
						pathValueParams(path, dec));
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
								pathValueParams(path, dec));
			}
			if (decValue >= lessThanValue) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.is.greater.than.or.equal.to.max",
						pathValueParams(path, dec));
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
								pathValueParams(path, dec));
			}
			if (decValue >= lessThanOrEqualToValue && !decimalEquals(dec, lessThanOrEqualTo)) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.is.greater.than.max",
						pathValueParams(path, dec));
			}
		}
		if (notInArray != null) {
			boolean matched = false;
			for (int i = 0; i < notInArray.size(); ++i) {
				String notInArrayString = notInArray.get(i);
				if (decimalEquals(dec, notInArrayString)) {
					matched = true;
				}
			}
			if (matched) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.matched.an.invalid.choice",
						pathValueParams(path, dec));
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
	public static boolean isDecimal(String string) {
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
	 * digit. If the character is not a Digit it breaks out of the loop.
	 * 
	 * @param string
	 * @return true if all digits, false if not
	 */
	private static boolean checkDigits(String string) {
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
	 * @param dec1
	 * @param dec2
	 * @return true if the strings are equal, false if not
	 */	
	public static boolean decimalEquals(String dec1, String dec2) {
		dec1 = dec1.trim();
		dec2 = dec2.trim();
		int num1BeginIndex = beginIndex(dec1);
		int num1EndIndex = endIndex(dec1);
		int num2BeginIndex = beginIndex(dec2);
		int num2EndIndex = endIndex(dec2);
		int num1len = num1EndIndex - num1BeginIndex + 1;
		int num2len = num2EndIndex - num2BeginIndex + 1;
		
		if (bothZero(dec1, dec2)) {
			return true;
		}
	
		if (num1len == num2len) {
			if (sameSign(dec1, dec2)) {
				return dec1.regionMatches(num1BeginIndex, dec2, num2BeginIndex, num1len);
			}
		}
		return false;
	}
	
	/**
	 * Returns whether or not both values have the same sign.
	 * 
	 * @param dec1
	 * @param dec2
	 * @return true if both pos/neg, false if not
	 */	
	private static boolean sameSign(String dec1, String dec2) {
		if (dec1.charAt(0) == '-' && dec2.charAt(0) != '-') {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns whether or not both values are zero.
	 * Need this as a separate method because 0's can be expressed in many ways.
	 * 
	 * @param dec1
	 * @param dec2
	 * @return true if both zero, false if not
	 */
	private static boolean bothZero(String dec1, String dec2) {
		int dec1BeginIndex = beginIndex(dec1);
		int dec1EndIndex = endIndex(dec1);
		int dec2BeginIndex = beginIndex(dec2);
		int dec2EndIndex = endIndex(dec2);
		int dec1len = dec1EndIndex - dec1BeginIndex + 1;
		int dec2len = dec2EndIndex - dec2BeginIndex + 1;
		
		//Case: Both values are multiple 0's but with no dot. I.e. num1 = 000, num2 = 0000000
		if (dec1.matches("-?\\+?[0]+") && dec2.matches("-?\\+?[0]+")) {
			return true;
		}		
		
		//Case: Both values are 0's with a dot. I.e. num1 = 0.00, num2 = .0
		if (dec1len == 0 && dec2len == 0) {
			return true;
		}
		
		//Case: If one of the nums = 00.00 (then len = 0) and another is 0000
		if (dec1len == 0 && dec2.matches("-?\\+?[0]+")) {
			return true;
		}
		if (dec2len == 0 && dec1.matches("-?\\+?[0]+")) {
			return true;
		}
		return false;
	}
	
	/** 
	 * Finds the index of the first character in the decimal that is not a 0.
	 * Used to skip all instances of leading 0's.
	 * 
	 * @param dec
	 * @return index to begin comparing at
	 */	
	private static int beginIndex(String dec) {
		int i = 0;
		if (dec.charAt(i) == '+' || dec.charAt(i) == '-') {
			i = 1;
		}
		for (; i < dec.length(); i++) {
			if (dec.charAt(i) != '0') {
				break;
			}
		}
		return i;
	}
	
	/**
	 * Finds the index of the last character in the decimal that is not a 0.
	 * Used to skip all instances of trailing 0's.
	 * 
	 * @param dec
	 * @return index to end comparing at
	 */	
	private static int endIndex(String dec) {
		int i = dec.length() - 1;
		for (; i > 0; i--) {
			if (dec.charAt(i) != '0') {
				break;
			}
		}
		if (dec.charAt(i) == '.') {
			i = i - 1;
		}
		return i;
	}
	
	/**
	 * Counts the number of fraction digits in the specified string (the number of digits after
	 * the decimal point).
	 * 
	 * @param dec
	 * @return the number of fraction digits
	 */	
	private int numFractionDigits(String dec) {
		dec = dec.trim();
		int pointIndex = dec.indexOf(".");
		if (pointIndex > -1) {
			return dec.substring(pointIndex + 1).length();
		}
		return 0;
	}
	
	/**
	 * Returns whether or not the string is an integer.
	 * 
	 * @param dec
	 * @return true if int, false if not
	 */	
	private boolean isInteger(String dec) {
		if (numFractionDigits(dec) == 0) {
			return true;
		}
		int pointIndex = dec.indexOf(".");
		if (decimalEquals(dec.substring(pointIndex + 1), "0")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the digit in the one's column of the string.
	 * Value passed into here will always be an integer.
	 * 
	 * @param dec
	 * @return int
	 */
	private int onesDigit(String dec) {
		int pointIndex = dec.indexOf(".");
		if (pointIndex == -1) {
			return dec.charAt(dec.length() - 1);
		} else {
			return dec.charAt(pointIndex - 1);
		}
	}
}