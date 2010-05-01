
package org.itemscript.schema;

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

final class NullType extends TypeBase {
    NullType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        if (!value.isNull()) { throw ItemscriptError.internalError(this, "validate.value.was.not.null",
                schema().pathParams(path)
                        .p("value", value.toCompactJsonString())); }
    }
}