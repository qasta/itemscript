
package org.itemscript.core.template;

import java.util.List;

import org.itemscript.core.util.GeneralUtil;
import org.itemscript.core.values.JsonString;
import org.itemscript.core.values.JsonValue;

public class HtmlEscapeFunction implements StringInputFunction {
    @Override
    public JsonString execute(JsonValue context, JsonValue value, List<JsonValue> args) {
        return value.system()
                .createString(GeneralUtil.htmlEncode(value.stringValue()));
    }
}