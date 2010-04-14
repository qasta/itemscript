
package org.itemscript.core.template;

import java.util.List;

import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

public class B64idFunction implements Function {
    @Override
    public JsonString execute(JsonValue context, JsonValue value, List<JsonValue> args) {
        return value.system()
                .createString(value.system()
                        .util()
                        .generateB64id());
    }
}