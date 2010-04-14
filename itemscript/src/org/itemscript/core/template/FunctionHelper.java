
package org.itemscript.core.template;

import java.util.List;

import org.itemscript.core.values.JsonValue;

public class FunctionHelper {
    public static JsonValue execute(JsonValue context, Function function, JsonValue input, List<JsonValue> args) {
        if (function instanceof StringInputFunction) {
            return function.execute(context, input.system()
                    .createString(Interpreter.coerceToString(input)), args);
        } else {
            return function.execute(context, input, args);
        }
    }
}