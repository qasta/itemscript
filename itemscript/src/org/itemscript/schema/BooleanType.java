
package org.itemscript.schema;

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

final class BooleanType extends TypeBase {
    BooleanType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (!value.isBoolean()) { throw ItemscriptError.internalError(this, "validate.value.was.not.boolean",
                schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
    }
}