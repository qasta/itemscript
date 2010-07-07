package org.itemscript.schema;

import java.util.ArrayList;
import java.util.List;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

class LongType extends TypeBase {
	private static final String GREATER_THAN_KEY = ".greaterThan";
	private static final String LESS_THAN_KEY = ".lessThan";
	private static final String GREATER_THAN_OR_EQUAL_TO_KEY = ".greaterThanOrEqualTo";
	private static final String LESS_THAN_OR_EQUAL_TO_KEY = ".lessThanOrEqualTo";
	private static final String EQUAL_TO_KEY = ".equalTo";
	private static final String IN_ARRAY_KEY = ".inArray";
	private static final String NOT_IN_ARRAY_KEY = ".notInArray";
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
	private final boolean even;
	private final boolean hasEven;
	private final boolean odd;
	private final boolean hasOdd;

	public LongType(Schema schema, Type extendsType, JsonObject def) {
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
			inArray = null;
			notInArray = null;
			even = false;
			hasEven = false;
			odd = false;
			hasOdd = false;
			
		}
	}

	@Override
	public boolean isLong() {
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
		if (hasDef) {
			if (isDecimal(value.stringValue())) {
				validateDecimal(path, value.stringValue());
			} else {
				throw ItemscriptError.internalError(this,
						"validate.value.was.not.decimal", schema().pathParams(
								path).p("value", value.toCompactJsonString()));
			}
		}
	}

	private void validateDecimal(String path, String string) {
		double decValue;
		double inArrayValue;
		double notInArrayValue;
		double greaterThanValue;
		double greaterThanOrEqualToValue;
		double lessThanValue;
		double lessThanOrEqualToValue;
		double equalToValue;
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
				try {
					inArrayValue = Double.parseDouble(inArrayString);
				} catch (NumberFormatException e) {
					throw ItemscriptError.internalError(this,
						"validateDecimal.inArrayString.could.not.be.parsed.into.double",
						pathValueParams(path, string));
				}
				if (decValue == inArrayValue) {
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
				try {
					notInArrayValue = Double.parseDouble(notInArrayString);
				} catch (NumberFormatException e) {
					throw ItemscriptError.internalError(this,
						"validateDecimal.notInArrayString.could.not.be.parsed.into.double",
						pathValueParams(path, string));
				}
				if (decValue == notInArrayValue) {
					matched = true;
				}
			}
			if (matched) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.did.not.match.a.valid.choice",
						pathValueParams(path, string));
			}
		};
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
			if (decValue < greaterThanOrEqualToValue) {
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
			if (decValue > lessThanOrEqualToValue) {
				throw ItemscriptError.internalError(this,
						"validateDecimal.value.is.greater.than.max",
						pathValueParams(path, string));
			}
		}
		/**
		if (fractionDigits > 0) {
			//CHECK DIGITS AFTER DECIMAL POINT.
		}
		*/
		/**
		if (hasEven) {
			//CHECK THAT THE NUMBER IS AN INTEGER BEFORE CONTINUING, WHICH MEANS GETTING RID
			 * OF TRAILING ZEROS AT THE END OF A DECIMAL POINT
			if (even) {
				if ((num % 2) != 0) { throw ItemscriptError.internalError(this,
                        "validateNumber.value.is.not.even", pathValueParams(path, num)); }
        	} else {
        		if ((num % 2) == 0) { throw ItemscriptError.internalError(this,
                        "validateNumber.value.is.not.odd", pathValueParams(path, num)); }
        	}
        }
        if (hasOdd) {
        	if (odd) {
        		if ((num % 2) == 0) { throw ItemscriptError.internalError(this,
                        "validateNumber.value.is.not.odd", pathValueParams(path, num)); }
        	} else {
        		if ((num % 2) != 0) { throw ItemscriptError.internalError(this,
                        "validateNumber.value.is.not.even", pathValueParams(path, num)); }
        	}
        }
			}
		}*/
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
		if (pointIndex > -1) {
			if (checkDigits(string.substring(startIndex, pointIndex))) {
				if (checkDigits(string.substring(pointIndex + 1))) {
					return true;
				}
			}
		} else {
			if (checkDigits(string)) {
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
}