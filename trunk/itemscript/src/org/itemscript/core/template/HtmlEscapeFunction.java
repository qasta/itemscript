
package org.itemscript.core.template;

import java.util.List;

import org.itemscript.core.JsonSystem;
import org.itemscript.core.util.GeneralUtil;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

public class HtmlEscapeFunction extends FunctionBase {
    public HtmlEscapeFunction(JsonSystem system) {
        super(system);
    }

    @Override
    public JsonString execute(JsonValue context, JsonValue value, List<JsonValue> args) {
        return system().createString(GeneralUtil.htmlEncode(Interpreter.coerceToString(value)));
    }
}