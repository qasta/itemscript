
package org.itemscript.schema;

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonValue;

final class AnyType extends TypeBase {
    AnyType(Schema schema) {
        super(schema);
    }

    @Override
    public void validate(String path, JsonValue value) {
        if (value == null) { throw ItemscriptError.internalError(this, "validate.value.was.null",
                schema().pathParams(path)); }
    }
}