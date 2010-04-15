
package org.itemscript.core.template;

import java.util.List;

import org.itemscript.core.values.JsonValue;

public class LeftBraceFunction implements Function {
    @Override
    public JsonValue execute(JsonValue context, JsonValue value, List<JsonValue> args) {
        return value.system()
                .createString("{");
    }
}