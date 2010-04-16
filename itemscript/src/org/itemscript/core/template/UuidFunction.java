
package org.itemscript.core.template;

import java.util.List;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.values.JsonValue;

public class UuidFunction extends FunctionBase {
    public UuidFunction(JsonSystem system) {
        super(system);
    }

    @Override
    public JsonValue execute(JsonValue context, JsonValue value, List<JsonValue> args) {
        return system().createString(system().util()
                .generateUuid());
    }
}