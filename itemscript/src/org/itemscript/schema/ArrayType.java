package org.itemscript.schema;

import java.util.ArrayList;
import java.util.List;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;
import org.itemscript.core.values.JsonArray;
/**
 * @author Eileen Bai
 */
final class ArrayType extends TypeBase {
	private static final String CONTAINS_KEY = ".contains";
	private static final String EXACT_SIZE_KEY = ".exactSize";
	private static final String IN_ARRAY_KEY = ".inArray";
	private static final String MAX_SIZE_KEY = ".maxSize";
	private static final String MIN_SIZE_KEY = ".minSize";
	private static final String NOT_IN_ARRAY_KEY = ".notInArray";
	private final boolean hasDef;
	private final JsonValue contains;
	private final Type containsType;
	private final int exactSize;
	private final int maxSize;
	private final int minSize;
    private final List<JsonArray> inArray;
    private final List<JsonArray> notInArray;

	ArrayType(Schema schema, Type extendsType, JsonObject def) {
		super(schema, extendsType, def);
		if (def != null) {
			hasDef = true;
			if (def.containsKey(CONTAINS_KEY)) {
				contains = def.getValue(CONTAINS_KEY);
				containsType = schema().resolve(contains);
			} else {
				contains = null;
				containsType = null;
			}
			if (def.hasNumber(EXACT_SIZE_KEY)) {
				exactSize = def.getInt(EXACT_SIZE_KEY);
			} else {
				exactSize = -1;
			}
            if (def.hasArray(IN_ARRAY_KEY)) {
            	inArray = new ArrayList<JsonArray>();
            	JsonArray array = def.getArray(IN_ARRAY_KEY);
            	for (int i = 0; i < array.size(); ++i) {
            		inArray.add(array.getRequiredArray(i));
            	}
            } else {
            	inArray = null;
            }
			if (def.hasNumber(MAX_SIZE_KEY)) {
				maxSize = def.getInt(MAX_SIZE_KEY);
			} else {
				maxSize = -1;
			}
			if (def.hasNumber(MIN_SIZE_KEY)) {
				minSize = def.getInt(MIN_SIZE_KEY);
			} else {
				minSize = -1;
			}
            if (def.hasArray(NOT_IN_ARRAY_KEY)) {
            	notInArray = new ArrayList<JsonArray>();
            	JsonArray array = def.getArray(NOT_IN_ARRAY_KEY);
            	for (int i = 0; i < array.size(); ++i) {
            		notInArray.add(array.getRequiredArray(i));
            	}
            } else {
            	notInArray = null;
            }
		} else {
			hasDef = false;
			contains = null;
			containsType = null;
			exactSize = -1;
			inArray = null;
			maxSize = -1;
			minSize = -1;
			notInArray = null;
		}
	}

	@Override
	public boolean isArray() {
		return true;
	}

	private Params pathValueParams(String path, JsonArray array) {
		return schema().pathParams(path).p("value", array);
	}

	@Override
	public void validate(String path, JsonValue value) {
		super.validate(path, value);
		if (!value.isArray()) {
			throw ItemscriptError.internalError(this,
					"validate.value.was.not.array", schema().pathParams(path)
							.p("value", value.toCompactJsonString()));
		}
		if (hasDef) {
			validateArray(path, value.asArray());
		}
	}

	private void validateArray(String path, JsonArray array) {
		if (contains != null) {
			boolean useSlash = path.length() > 0;
			for (int i = 0; i < array.size(); i++) {
				containsType.validate(path + (useSlash ? "/" : "") + i, array
						.get(i));
			}
		}
		if (exactSize > 0) {
			if (array.size() != exactSize) {
				throw ItemscriptError.internalError(this,
						"validateArray.array.is.the.wrong.size",
						pathValueParams(path, array));
			}
		}
        if (inArray != null) {
            boolean matched = false;
            for (int i = 0; i < inArray.size(); ++i) {
                JsonArray inArrayJsonArray = inArray.get(i);
                if (array.equals(inArrayJsonArray)) {
                    matched = true;
                    break;
                }
            }
            if (!matched) { throw ItemscriptError.internalError(this,
                    "validateArray.value.did.not.match.a.valid.choice", pathValueParams(path, array)); }
        }
		if (maxSize > 0) {
			if (array.size() > maxSize) {
				throw ItemscriptError.internalError(this,
						"validateArray.array.size.is.bigger.than.max",
						pathValueParams(path, array));
			}
		}
		if (minSize > 0) {
			if (array.size() < minSize) {
				throw ItemscriptError.internalError(this,
						"validateArray.array.size.is.smaller.than.min",
						pathValueParams(path, array));
			}
		}
        if (notInArray != null) {
            boolean matched = false;
            for (int i = 0; i < notInArray.size(); ++i) {
                JsonArray notInArrayJsonArray = notInArray.get(i);
                if (array.equals(notInArrayJsonArray)) {
                    matched = true;
                    break;
                }
            }
            if (matched) { throw ItemscriptError.internalError(this,
                    "validateArray.value.matched.an.invalid.choice", pathValueParams(path, array)); }
        }
	}
}