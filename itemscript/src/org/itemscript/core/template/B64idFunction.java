
package org.itemscript.core.template;

import java.util.List;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

public class B64idFunction extends FunctionBase {
    public B64idFunction(JsonSystem system) {
        super(system);
    }

    @Override
    public JsonString execute(JsonValue context, JsonValue value, List<JsonValue> args) {
        return system().createString(system().util()
                .generateB64id());
    }
}