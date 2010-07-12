package org.itemscript.schema;

import java.util.ArrayList;
import java.util.List;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

class LongType extends TypeBase {
	private static final String EQUAL_TO_KEY = ".equalTo";
	private static final String EVEN_KEY = ".even";
	private static final String GREATER_THAN_KEY = ".greaterThan";
	private static final String IN_ARRAY_KEY = ".inArray";
	private static final String GREATER_THAN_OR_EQUAL_TO_KEY = ".greaterThanOrEqualTo";
	private static final String LESS_THAN_KEY = ".lessThan";
	private static final String LESS_THAN_OR_EQUAL_TO_KEY = ".lessThanOrEqualTo";
	private static final String NOT_IN_ARRAY_KEY = ".notInArray";
	private static final String ODD_KEY = ".odd";
	private final boolean hasDef;
	private final boolean even;
	private final boolean hasEven;
	private final boolean odd;
	private final boolean hasOdd;
	private final String equalTo;
	private final String greaterThan;
	private final String greaterThanOrEqualTo;
	private final String lessThan;
	private final String lessThanOrEqualTo;
	private final List<String> inArray;
	private final List<String> notInArray;


	public LongType(Schema schema, Type extendsType, JsonObject def) {
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
			if (def.hasString(GREATER_THAN_KEY)) {
				greaterThan = def.getString(GREATER_THAN_KEY);
			} else {
				greaterThan = null;
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
			if (def.hasString(GREATER_THAN_OR_EQUAL_TO_KEY)) {
				greaterThanOrEqualTo = def
						.getString(GREATER_THAN_OR_EQUAL_TO_KEY);
			} else {
				greaterThanOrEqualTo = null;
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
	public boolean isLong() {
		return true;
	}

	private Params pathValueParams(String path, Long longVal) {
		return schema().pathParams(path).p("value", longVal);
	}

	@Override
	public void validate(String path, JsonValue value) {
		super.validate(path, value);
		Long longVal;
		if (!value.isString()) {
			throw ItemscriptError.internalError(this,
					"validate.value.was.not.string", schema().pathParams(path)
							.p("value", value.toCompactJsonString()));
		}
		try {
			longVal = Long.parseLong(value.stringValue());
		} catch (NumberFormatException e) {
			throw ItemscriptError.internalError(this,
					"validate.value.could.not.be.parsed.into.long", schema().pathParams(path)
							.p("value", value.toCompactJsonString()));
		}
		if (hasDef) {
			validateLong(path, longVal);
		}
	}

	private void validateLong(String path, Long longVal) {
		long equalToValue;
		long greaterThanValue;
		long greaterThanOrEqualToValue;
		long inArrayValue;
		long lessThanValue;
		long lessThanOrEqualToValue;
		long notInArrayValue;
		
		if (equalTo != null) {
			try {
				equalToValue = Long.parseLong(equalTo);
			} catch (NumberFormatException e) {
				throw ItemscriptError
						.internalError(this, "validateLong.equalTo.could.not.be.parsed.into.long",
								pathValueParams(path, longVal));
			}
			if (longVal != equalToValue) {
				throw ItemscriptError.internalError(this,
						"validateLong.value.is.not.equal.to.equalTo",
						pathValueParams(path, longVal));
			}
		}
		if (greaterThan != null) {;
			try {
				greaterThanValue = Long.parseLong(greaterThan);
			} catch (NumberFormatException e) {
				throw ItemscriptError
						.internalError(this, "validateLong.greaterThan.could.not.be.parsed.into.long",
								pathValueParams(path, longVal));
			}
			if (longVal <= greaterThanValue) {
				throw ItemscriptError.internalError(this,
						"validateLong.value.is.less.than.or.equal.to.min",
						pathValueParams(path, longVal));
			}
		}
		if (greaterThanOrEqualTo != null) {
			try {
				greaterThanOrEqualToValue = Long
						.parseLong(greaterThanOrEqualTo);
			} catch (NumberFormatException e) {
				throw ItemscriptError
						.internalError(this,
								"validateLong.greaterThanOrEqualTo.could.not.be.parsed.into.long",
								pathValueParams(path, longVal));
			}
			if (longVal < greaterThanOrEqualToValue) {
				throw ItemscriptError.internalError(this,
						"validateLong.value.is.less.than.min",
						pathValueParams(path, longVal));
			}
		}
		if (hasEven) {
			if (even) {
				if ((longVal % 2) != 0) { throw ItemscriptError.internalError(this,
                        "validateLong.value.is.not.even", pathValueParams(path, longVal)); }
        	} else {
        		if ((longVal % 2) == 0) { throw ItemscriptError.internalError(this,
                        "validateLong.value.is.not.odd", pathValueParams(path, longVal)); }
        	}
        }
        if (hasOdd) {
        	if (odd) {
        		if ((longVal % 2) == 0) { throw ItemscriptError.internalError(this,
                        "validateLong.value.is.not.odd", pathValueParams(path, longVal)); }
        	} else {
        		if ((longVal % 2) != 0) { throw ItemscriptError.internalError(this,
                        "validateLong.value.is.not.even", pathValueParams(path, longVal)); }
        	}
        }
		if (inArray != null) {
			boolean matched = false;
			for (int i = 0; i < inArray.size(); ++i) {
				String inArrayString = inArray.get(i);
				try {
					inArrayValue = Long.parseLong(inArrayString);
				} catch (NumberFormatException e) {
					throw ItemscriptError.internalError(this,
						"validateLong.inArrayString.could.not.be.parsed.into.long",
						pathValueParams(path, longVal));
				}
				if (longVal == inArrayValue) {
					matched = true;
				}
			}
			if (!matched) {
				throw ItemscriptError.internalError(this,
						"validateLong.value.did.not.match.a.valid.choice",
						pathValueParams(path, longVal));
			}
		}
		if (lessThan != null) {
			try {
				lessThanValue = Long.parseLong(lessThan);
			} catch (NumberFormatException e) {
				throw ItemscriptError
						.internalError(this, "validateLong.lessThan.could.not.be.parsed.into.long",
								pathValueParams(path, longVal));
			}
			if (longVal >= lessThanValue) {
				throw ItemscriptError.internalError(this,
						"validateLong.value.is.greater.than.or.equal.to.max",
						pathValueParams(path, longVal));
			}
		}
		if (lessThanOrEqualTo != null) {
			try {
				lessThanOrEqualToValue = Long.parseLong(lessThanOrEqualTo);
			} catch (NumberFormatException e) {
				throw ItemscriptError
						.internalError(this,
								"validateLong.lessThanOrEqualTo.could.not.be.parsed.into.long",
								pathValueParams(path, longVal));
			}
			if (longVal > lessThanOrEqualToValue) {
				throw ItemscriptError.internalError(this,
						"validateLong.value.is.greater.than.max",
						pathValueParams(path, longVal));
			}
		}
		if (notInArray != null) {
			boolean matched = false;
			for (int i = 0; i < notInArray.size(); ++i) {
				String notInArrayString = notInArray.get(i);
				try {
					notInArrayValue = Long.parseLong(notInArrayString);
				} catch (NumberFormatException e) {
					throw ItemscriptError.internalError(this,
						"validateLong.notInArrayString.could.not.be.parsed.into.long",
						pathValueParams(path, longVal));
				}
				if (longVal == notInArrayValue) {
					matched = true;
				}
			}
			if (matched) {
				throw ItemscriptError.internalError(this,
						"validateLong.value.matched.an.invalid.choice",
						pathValueParams(path, longVal));
			}
		}

	}
}