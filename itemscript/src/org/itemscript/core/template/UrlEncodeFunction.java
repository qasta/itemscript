
package org.itemscript.core.template;

import java.util.List;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.url.Url;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

public class UrlEncodeFunction extends FunctionBase {
    public UrlEncodeFunction(JsonSystem system) {
        super(system);
    }

    @Override
    public JsonString execute(JsonValue context, JsonValue value, List<JsonValue> args) {
        return system().createString(Url.encode(Interpreter.coerceToString(value)));
    }
}