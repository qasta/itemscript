
package org.itemscript.schema;

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

final class IntegerType extends TypeBase {
    IntegerType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
    }

    public boolean isInteger() {
        return true;
    }

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (value.doubleValue() != (double) Math.round(value.doubleValue())) { throw ItemscriptError.internalError(
                this, "validate.had.fractional.digits", schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
    }
}