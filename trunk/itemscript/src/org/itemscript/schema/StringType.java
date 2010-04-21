
package org.itemscript.schema;

import java.util.ArrayList;
import java.util.List;

import org.itemscript.core.Params;
import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonArray;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

class StringType extends TypeBase {
    private boolean hasDef;
    private final int min;
    private final int max;
    private final List<String> choose;
    private final List<String> pattern;

    public StringType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
        if (def != null) {
            hasDef = true;
            if (def.hasNumber("MIN")) {
                min = def.getInt("MIN");
            } else {
                min = -1;
            }
            if (def.hasNumber("MAX")) {
                max = def.getInt("MAX");
            } else {
                max = -1;
            }
            choose = new ArrayList<String>();
            if (def.hasArray("CHOOSE")) {
                JsonArray array = def.getArray("CHOOSE");
                for (int i = 0; i < array.size(); ++i) {
                    choose.add(array.getRequiredString(i));
                }
            } else if (def.hasString("CHOOSE")) {
                choose.add(def.getString("CHOOSE"));
            }
            pattern = new ArrayList<String>();
            if (def.hasArray("PATTERN")) {
                JsonArray array = def.getArray("PATTERN");
                for (int i = 0; i < array.size(); ++i) {
                    pattern.add(array.getRequiredString(i));
                }
            } else if (def.hasString("PATTERN")) {
                pattern.add(def.getString("PATTERN"));
            }
        } else {
            hasDef = false;
            min = -1;
            max = -1;
            choose = null;
            pattern = null;
        }
    }

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (!value.isString()) { throw ItemscriptError.internalError(this, "validate.value.was.not.string",
                schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
        if (hasDef) {
            validateString(path, value.stringValue());
        }
    }

    public boolean isString() {
        return true;
    }

    private void validateString(String path, String string) {
        if (min > 0) {
            if (string.length() < min) { throw ItemscriptError.internalError(this,
                    "validateString.value.shorter.than.min.length", pathValueParams(path, string)); }
        }
        if (max > 0) {
            if (string.length() > max) { throw ItemscriptError.internalError(this,
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
    }

    private Params pathValueParams(String path, String string) {
        return schema().pathParams(path)
                .p("value", string);
    }
}