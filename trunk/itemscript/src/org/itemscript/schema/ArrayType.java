
package org.itemscript.schema;

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

final class ArrayType extends TypeBase {
    ArrayType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
    }

    public boolean isArray() {
        return true;
    }

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (!value.isArray()) { throw ItemscriptError.internalError(this, "validate.value.was.not.array",
                schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
    }
}