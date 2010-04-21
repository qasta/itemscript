
package org.itemscript.schema;

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

final class NumberType extends TypeBase {
    NumberType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
    }

    public boolean isNumber() {
        return true;
    }

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (!value.isNumber()) { throw ItemscriptError.internalError(this, "validate.value.was.not.number",
                schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
    }
}