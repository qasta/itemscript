
package org.itemscript.schema;

import org.itemscript.core.exceptions.ItemscriptError;
import org.itemscript.core.values.JsonObject;
import org.itemscript.core.values.JsonValue;

final class BinaryType extends TypeBase {
    BinaryType(Schema schema, Type extendsType, JsonObject def) {
        super(schema, extendsType, def);
    }

    public boolean isBinary() {
        return true;
    }

    @Override
    public void validate(String path, JsonValue value) {
        super.validate(path, value);
        try {
            byte[] binaryValue = value.binaryValue();
        } catch (RuntimeException e) {
            throw ItemscriptError.internalError(this, "validate.could.not.parse.as.base64", schema().pathParams(
                    path)
                    .p("value", value.toCompactJsonString()));
        }
    }
}